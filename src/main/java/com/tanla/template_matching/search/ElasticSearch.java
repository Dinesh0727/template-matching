package com.tanla.template_matching.search;

import java.io.IOException;
import java.util.List;

import com.tanla.template_matching.entity.Message;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;

public class ElasticSearch {

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

    public static void deleteDocument(ElasticsearchClient esClient, String deleteDocumentId, String index)
            throws ElasticsearchException, IOException {
        if ("not_found".equals(esClient.index(idx -> idx.index(index).id(deleteDocumentId)).result().name())) {
            System.out.println("Document Not Found with Id " + deleteDocumentId);
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
        System.out.println("Time to Complete the normal search is : " + (end - start));

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
    }

    static void printResponseDetails(SearchResponse<Message> searchResponse) {
        TotalHits total = searchResponse.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            System.out.println("There are " + total.value() + " results");
        } else {
            System.out.println("There are more than " + total.value() + " results");
        }

        List<Hit<Message>> hits = searchResponse.hits().hits();
        for (Hit<Message> hit : hits) {
            Message product = hit.source();
            System.out.println("Found product " + product.getId() + ", score " +
                    hit.score() + ", hitRank : " + hit.rank());
        }
    }
}
