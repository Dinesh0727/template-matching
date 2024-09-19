package com.tanla.template_matching.startup;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Component;

// @Component
public class LoadTester implements CommandLineRunner {
    @Autowired
    public ElasticsearchClient elasticsearchClient;

    private String indexName = "my-index-000003";

    private final Logger logger = LogManager.getLogger(LoadTester.class);

    @Override
    public void run(String... args) throws IOException {
        Long start, end;
        int i;

        // Makes sure that the database has been set up
        InputMessageGenerator.run();

        logger.info("==========================================================================");
        logger.info("Running Elastic Search for 1, 1000 rounds");
        logger.info("==========================================================================");
        start = System.currentTimeMillis();
        // ElasticSearch.moreLikeThisSearch(elasticsearchClient,
        // PreExistingTemplates.templates_index, searchText);
        runSearch(elasticsearchClient);
        end = System.currentTimeMillis();
        logger.info("Execution time for 1 run = " + (end - start) + "ms");

        start = System.currentTimeMillis();
        for (i = 0; i < 1000; i++) {
            // ElasticSearch.moreLikeThisSearch(elasticsearchClient,
            // PreExistingTemplates.templates_index, searchText);
            runSearch(elasticsearchClient);
        }
        end = System.currentTimeMillis();
        logger.info("Execution time for 1000 runs = " + (end - start) + "ms");
        logger.info("Execution time for 1 run(avg) = " + (end - start) / 1000.0 + "ms");

        // start = System.currentTimeMillis();
        // for (i = 0; i < 100000; i++) {
        // ElasticSearch.moreLikeThisSearch(elasticsearchClient,
        // PreExistingTemplates.templates_index, searchText);
        // }
        // end = System.currentTimeMillis();
        // logger.info("Execution time for 100,000 runs = " + (end - start) +
        // "ms");
        // logger.info("Execution time for 1 run(avg) = " + (end - start) /
        // 100000.0 + "ms");

        // logger.info("==========================================================================");
        // logger.info("Running Regex Search for 1, 1000, 100,000 rounds");
        // logger.info("==========================================================================");
        // start = System.currentTimeMillis();
        // RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);
        // end = System.currentTimeMillis();
        // logger.info("Execution time for 1 run = " + (end - start) + "ms");

        // start = System.currentTimeMillis();
        // for (i = 0; i < 1000; i++) {
        // RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);
        // }
        // end = System.currentTimeMillis();
        // logger.info("Execution time for 1000 runs = " + (end - start) + "ms");
        // logger.info("Execution time for 1 run(avg) = " + (end - start) /
        // 1000.0 + "ms");

    }

    private void runSearch(ElasticsearchClient esClient) throws IOException {
        for (int i = 0; i < InputMessageGenerator.inputMessagesFromProd.getInputMessages().size(); i++) {
            ElasticSearch.moreLikeThisTemplateSearch(esClient, indexName, 5,
                    InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(i).getMessage_text(),
                    InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(i).getEsmeaddr());
        }
    }
}
