package com.tanla.template_matching.startup;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.entity.Product;
import com.tanla.template_matching.service.RegexSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        private ElasticsearchClient esClient;

        @Override
        public void run(String... args) throws IOException {

                File file = new File(
                                "/home/dinesh/docker/output-1.txt");
                Scanner sc = new Scanner(file);

                List<String> strings = new ArrayList<>();

                while (sc.hasNextLine()) {
                        strings.add(sc.nextLine());
                }

                sc.close();
                String template_text_index = "template_texts_pre_prod";

                createIndex(esClient, template_text_index);

                // String deleteDocumentId = "280";
                // deleteDocument(esClient, deleteDocumentId, template_text_index);

                // Making Bulk Request to add data

                bulkRequest(esClient, strings, template_text_index);

                // String searchText = strings.get(12);
                // String searchText = "We strongly advise you to buy promptly, emphasizing on
                // taking action with caution and precision. Please take note of the following
                // information: Strike Price: $100, Recommended Price: $105, Target Price: $120,
                // Stop Loss: $95, Generated on: August 27, 2024.\n\nFor a seamless trading
                // experience, execute your trade via the MO Investor App on your smartphone or
                // the MO Trader App on your desktop now!\n\nWe appreciate your trust in
                // us.\n\nBest Regards,\nTeam MOFSL";
                // String searchText = "We highly recommend to install app at your earliest
                // convenience, with a focus on x y z. Please note the following details: Strike
                // Price: {{5}}, Recommended Price: {{6}}, Target Price: {{7}}, Stop Loss:
                // {{8}}, Generated on: {{9}}.\n\nTo execute your trade quickly and efficiently,
                // place your order through the MO Investor App {{10}} or the MO Trader App
                // {{11}} right away!\n\nThank you for choosing us.\n\nWarm Regards,\nTeam
                // MOFSL";

                // Searching the text through Normal Search API and More Like This API

                String searchText = "Dear Parent,\n\nThank you for registering with CodeAcademy\n\nRequest you to update your & your child’s names & email id to receive your personalized Coding Fundamentals Certificate\n\nClick here to fill in your details: https://codeacademy.com/update-details \n\nRegards,\nTeam CodeAcademy";

                normalSearch(esClient, template_text_index, searchText);

                moreLikeThisSearch(esClient, template_text_index,
                                searchText);

                existingSearch(searchText, strings);

        }

        private void deleteDocument(ElasticsearchClient esClient, String deleteDocumentId, String index)
                        throws ElasticsearchException, IOException {
                if ("not_found".equals(esClient.index(idx -> idx.index(index).id(deleteDocumentId)).result().name())) {
                        System.out.println("Document Not Found with Id " + deleteDocumentId);
                        return;
                }
                esClient.delete(del -> del.index(index).id(deleteDocumentId));
        }

        private void existingSearch(String searchText, List<String> strings) {
                System.out.print("===========================================================\nRegex Search\n");
                long start = System.currentTimeMillis();
                int i = 0;
                for (; i < strings.size(); i++) {
                        if (RegexSearch.matchTemplate(searchText, strings.get(i), i + 1)) {
                                System.out.println("Found the Template Match with regex matching: " + strings.get(i));
                                break;
                        }
                }
                if (i == strings.size())
                        System.out.println("No full match found.");
                long end = System.currentTimeMillis();
                System.out.println("Time taken to find the template through regex search : " + (end - start));
                System.out.println("============================================================");
        }

        private void bulkRequest(ElasticsearchClient esClient, List<String> strings, String template_text_index)
                        throws IOException {
                BulkRequest.Builder br = new BulkRequest.Builder();

                for (int i = 0; i < strings.size(); i++) {
                        Product product = new Product(i + 1, strings.get(i));
                        br.operations(op -> op.index(idx -> idx.index(template_text_index)
                                        .id(String.valueOf(product.getId()))
                                        .document(product)));
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

        private void createIndex(ElasticsearchClient esClient, String indexName) throws IOException {
                if (!esClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value())
                        esClient.indices().create(c -> c
                                        .index(indexName));
        }

        private void normalSearch(ElasticsearchClient esClient, String template_text_index,
                        String searchText)
                        throws IOException {

                System.out.print("===========================================================\nNormal Search\n");
                long start = System.currentTimeMillis();
                SearchResponse<Product> result = esClient.search(
                                s -> s.index(template_text_index)
                                                .query(q -> q.matchPhrase(t -> t.field("text")
                                                                .query(searchText))),
                                Product.class);
                long end = System.currentTimeMillis();
                System.out.println("Time to Complete the normal search is : " + (end - start));

                ResponseDetails(result);
                System.out.print("===========================================================\n");
        }

        private void moreLikeThisSearch(ElasticsearchClient esClient, String template_text_index,
                        String searchText) throws IOException {

                System.out.print(
                                "===========================================================\nMore Like This  Search\n");
                long requestMakingStart = System.currentTimeMillis();
                MoreLikeThisQuery mltQuery = MoreLikeThisQuery.of(mlt -> mlt
                                .fields("text")
                                .like(l -> l.text(searchText)));

                SearchRequest searchRequest = SearchRequest.of(sr -> sr
                                .index(template_text_index)
                                .query(q -> q.moreLikeThis(mltQuery))
                                .size(3) // You can adjust the number of results you want to fetch
                );
                long requestMakingEnd = System.currentTimeMillis();
                System.out.println("Time taken to make the more-like-this Request : "
                                + (requestMakingEnd - requestMakingStart));

                long morelikeThisSearchStart = System.currentTimeMillis();
                SearchResponse<Product> searchResponse = esClient.search(searchRequest, Product.class);
                long morelikeThisSearchEnd = System.currentTimeMillis();

                ResponseDetails(searchResponse);

                System.out.println("Time taken to complete the more-like-this search : "
                                + (morelikeThisSearchEnd - morelikeThisSearchStart));

                System.out.print("===========================================================\n");
        }

        private void ResponseDetails(SearchResponse<Product> searchResponse) {
                TotalHits total = searchResponse.hits().total();
                boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

                if (isExactResult) {
                        System.out.println("There are " + total.value() + " results");
                } else {
                        System.out.println("There are more than " + total.value() + " results");
                }

                List<Hit<Product>> hits = searchResponse.hits().hits();
                for (Hit<Product> hit : hits) {
                        Product product = hit.source();
                        System.out.println("Found product " + product.getId() + ", score " +
                                        hit.score() + ", hitRank : " + hit.rank());
                }
        }
}
