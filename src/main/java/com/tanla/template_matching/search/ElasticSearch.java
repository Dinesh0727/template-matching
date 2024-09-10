package com.tanla.template_matching.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tanla.template_matching.entity.Message;
import com.tanla.template_matching.entity.Template;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.JsonData;

public class ElasticSearch {

    private static Logger logger = LogManager.getLogger(ElasticSearch.class);

    public static void createIndex(ElasticsearchClient esClient, String indexName) throws IOException {
        if (!esClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value())
            esClient.indices().create(c -> c
                    .index(indexName));
    }

    public static void bulkRequest(ElasticsearchClient esClient, List<String> strings, String template_text_index)
            throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (int i = 0; i < strings.size(); i++) {
            Message message = new Message(i + 1, strings.get(i));
            br.operations(op -> op.index(idx -> idx.index(template_text_index)
                    .id(String.valueOf(message.getId()))
                    .document(message)));
        }

        BulkResponse response = esClient.bulk(br.build());

        if (response.errors()) {
            System.err.println("Bulk had errors");
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    System.err.println(item.error().reason());
                }
            }
        }
    }

    public static void bulkRequest_templates(ElasticsearchClient esClient, List<Template> templates,
            String templates_index) {

        logger.info("Starting the bulk request through ingestor");
        logger.info("Size of the templates list: " + templates.size());

        long flushInterval = 2;

        BulkIngester<Void> ingester = BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(1500)
                .flushInterval(flushInterval, TimeUnit.SECONDS));

        for (int i = 0; i < templates.size(); i++) {
            final int index = i;
            ingester.add(op -> op
                    .index(idx -> idx
                            .index(templates_index)
                            .id(templates.get(index).getTemplate_name())
                            .document(templates.get(index))));
        }

        ingester.close();
    }

    public static void deleteDocument(ElasticsearchClient esClient, String deleteDocumentId, String index)
            throws ElasticsearchException, IOException {
        if ("not_found".equals(esClient.index(idx -> idx.index(index).id(deleteDocumentId)).result().name())) {
            logger.info("Document Not Found with Id " + deleteDocumentId);
            return;
        }
        esClient.delete(del -> del.index(index).id(deleteDocumentId));
    }

    public static void search(ElasticsearchClient esClient, String template_text_index,
            String searchText)
            throws IOException {

        System.out.print("===========================================================\nNormal Search\n\n");
        long start = System.currentTimeMillis();
        SearchResponse<Message> result = esClient.search(
                s -> s.index(template_text_index)
                        .query(q -> q.matchPhrase(t -> t.field("text")
                                .query(searchText))),
                Message.class);
        long end = System.currentTimeMillis();
        logger.info("Time to Complete the normal search is : " + (end - start));

        printResponseDetails(result);
        System.out.print("===========================================================\n");
    }

    public static void moreLikeThisSearch(ElasticsearchClient esClient, String template_text_index,
            String searchText) throws IOException {
        MoreLikeThisQuery mltQuery = MoreLikeThisQuery.of(mlt -> mlt
                .fields("text")
                .like(l -> l.text(searchText)));

        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(template_text_index)
                .query(q -> q.moreLikeThis(mltQuery))
                .size(3));

        SearchResponse<Message> searchResponse = esClient.search(searchRequest, Message.class);
        printResponseDetails(searchResponse);
    }

    public static boolean moreLikeThisTemplateSearch(ElasticsearchClient esClient, String template_text_index,
            String searchText, Integer numberOfHitsToBeConsidered) throws IOException {

        final String MSG_BODY = "msg_body";

        Integer searchTextWordCount = searchText.split(" ").length;

        MoreLikeThisQuery mltQuery = MoreLikeThisQuery.of(mlt -> mlt
                .fields(MSG_BODY)
                .like(l -> l.text(searchText))
                .boost(Float.valueOf(2))
                .minTermFreq(1)
                .minDocFreq(2));

        Script script = Script.of(
                s -> s.lang("painless")
                        .source("doc['msg_body'] >= params.searchTextWordCount")
                        .params("searchTextWordCount", JsonData.fromJson(searchTextWordCount.toString())));

        ScriptQuery scriptQuery = ScriptQuery.of(sq -> sq.script(script));

        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(template_text_index)
                .query(q -> q.bool(b -> b
                        .must(m -> m
                                .moreLikeThis(mltQuery))
                        .filter(f -> f.script(scriptQuery))))
                .size(numberOfHitsToBeConsidered));

        long start = System.currentTimeMillis();
        SearchResponse<Template> searchResponse = esClient.search(searchRequest, Template.class);
        long end = System.currentTimeMillis();
        logger.info("The searching time is : " + (end - start) + "ms");

        logger.info("========================================================");
        logger.info("========================================================");
        logger.info("Calling the print reponse details function : ");
        printTemplateSearchResponseDetails(searchResponse);

        // logger.info("========================================================");
        // logger.info("========================================================");
        // logger.info("Printing the search text" + "\n " + searchText);
        if (searchResponse.hits().total().value() == 0) {
            logger.info("There is no matching template through Elastic Search");
            return false;
        }

        return true;
    }

    public static Template moreLikeThisTemplateSearchWithRegex(ElasticsearchClient esClient, String template_text_index,
            String searchText) throws IOException {

        final String MSG_BODY = "msg_body";
        MoreLikeThisQuery mltQuery = MoreLikeThisQuery.of(mlt -> mlt
                .fields(MSG_BODY)
                .like(l -> l.text(searchText)));

        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(template_text_index)
                .query(q -> q.moreLikeThis(mltQuery))
                .size(3));

        SearchResponse<Template> searchResponse = esClient.search(searchRequest, Template.class);

        TotalHits total = searchResponse.hits().total();

        if (total.value() > 0) {
            // Do regex matching for the top hits
            List<Hit<Template>> hits = searchResponse.hits().hits();
            for (int i = 0; i < hits.size(); i++) {
                Template template = hits.get(i).source();
                if (RegexSearch.matchTemplate(searchText, template.getMsg_body(), i)) {
                    logger.info("Got the perfect match with hit number : " + i + 1);
                    return template;
                }
            }
        }
        logger.info("========================================================");
        logger.info("========================================================");
        logger.info("Calling the print reponse details function : ");
        printTemplateSearchResponseDetails(searchResponse);

        logger.info("There is no matching template");
        return null;
    }

    private static void printTemplateSearchResponseDetails(SearchResponse<Template> searchResponse) {
        TotalHits total = searchResponse.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<Template>> hits = searchResponse.hits().hits();
        for (Hit<Template> hit : hits) {
            Template template = hit.source();
            logger.info("Found template " + template.getTemplate_name() + ", score: " + hit.score()
                    + ", hitRank : " + hit.rank());
        }
        logger.info("======================");
        logger.info("======================");
        logger.info("The first fit template_name is : " + hits.get(0).source().getTemplate_name());

    }

    static void printResponseDetails(SearchResponse<Message> searchResponse) {
        TotalHits total = searchResponse.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }

        List<Hit<Message>> hits = searchResponse.hits().hits();
        for (Hit<Message> hit : hits) {
            Message product = hit.source();
            logger.info("Found product " + product.getId() + ", score " +
                    hit.score() + ", hitRank : " + hit.rank());
        }
    }

    public static boolean search(ArrayList<Template> cacheTemplateList, ElasticsearchClient esClient, String inputMsg,
            String indexName)
            throws IOException {

        boolean foundTemplate = false;
        if (cacheTemplateList.isEmpty()) {
            Template temp = searchInESDBWithRegex(cacheTemplateList, esClient, inputMsg, indexName);
            if (temp != null) {
                cacheTemplateList.add(temp);
                return true;
            } else {
                // Later if th approach works add the code for adding template in elastic search
                // database
                logger.info(
                        "No match found in Elastic Search database so we have to add a new template in ES DB itself");
            }
        } else {
            for (int i = 0; i < cacheTemplateList.size(); i++) {
                if (RegexSearch.matchTemplate(inputMsg, cacheTemplateList.get(i).getMsg_body(), i)) {
                    Template temp = cacheTemplateList.remove(i);
                    cacheTemplateList.add(0, temp);
                    foundTemplate = true;
                }
            }

            if (foundTemplate) {
                return true;
            } else {
                searchInESDBWithRegex(cacheTemplateList, esClient, inputMsg, indexName);
            }
        }
        return false;
    }

    static Template searchInESDBWithRegex(ArrayList<Template> cacheTemplateList, ElasticsearchClient esClient,
            String inputMsg, String indexName) throws IOException {
        Template searchedTemplate = moreLikeThisTemplateSearchWithRegex(esClient,
                indexName, inputMsg);
        if (searchedTemplate != null) {
            // add the template message in the cache template
            cacheTemplateList.add(searchedTemplate);
            return searchedTemplate;
        } else {
            // Later if th approach works add the code for adding template in elastic search
            // database
            logger.info(
                    "No match found in Elastic Search database so we have to add a new template in ES DB itself");
        }
        return null;
    }

}
