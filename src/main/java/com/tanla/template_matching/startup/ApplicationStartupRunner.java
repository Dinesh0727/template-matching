package com.tanla.template_matching.startup;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        private Logger logger = LogManager.getLogger(ApplicationStartupRunner.class);

        public static int counter = 0;

        @Override
        public void run(String... args) throws IOException {
                InputMessageGenerator.run();

                String prod_template_index_name = "my-index-000003";

                // logger.info("Searching through Elastic Search");

                Integer numberOfHitsToBeConsidered = 5;
                Integer inputMessagesWithNoMatchingTemplates = 0;

                // Using without regex elastic search
                logger.info("Working with the normal More like this query with esmeAddr filter");
                long start = System.currentTimeMillis();
                for (int i = 0; i < InputMessageGenerator.inputMessagesFromProd.getInputMessages().size(); i++) {
                        logger.info("\n\nSearching Message : " + i);
                        if (!ElasticSearch.templateNameSearch(esClient, i, prod_template_index_name)) {
                                logger.info("No matching template in database.");
                                logger.info("The actual template name is : "
                                                + InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(i)
                                                                .getTemplate_id());
                                inputMessagesWithNoMatchingTemplates++;
                                continue;
                        }
                        ElasticSearch.moreLikeThisTemplateSearch(esClient,
                                        prod_template_index_name,
                                        numberOfHitsToBeConsidered,
                                        i);
                }
                long end = System.currentTimeMillis();
                logger.info("Total Execution Time : " + (end - start) + "ms");
                logger.info("Counter value : " + counter);
                logger.info("The accuracy without word count is : "
                                + ((float) counter * 1.0f
                                                / (InputMessageGenerator.inputMessagesFromProd.getInputMessages()
                                                                .size()))
                                                * 100);
                logger.info("The number of input messages whose actual templates are not present in the database are: "
                                + inputMessagesWithNoMatchingTemplates);

        }

}
