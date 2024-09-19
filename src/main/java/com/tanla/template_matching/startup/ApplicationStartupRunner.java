package com.tanla.template_matching.startup;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.DummyMessages;
import com.tanla.template_matching.constants.Constants;
import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// @Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        private Logger logger = LogManager.getLogger(ApplicationStartupRunner.class);

        public static int counter = 0;

        public static int time = 0;

        @Override
        public void run(String... args) throws IOException {
                InputMessageGenerator.run();

                String prod_template_index_name = Constants.indexName;

                logger.info("Searching through Elastic Search Index : " + prod_template_index_name);

                Integer numberOfHitsToBeConsidered = Constants.numberOfHitsToBeConsidered;
                Integer inputMessagesWithNoMatchingTemplates = 0;

                // Using without regex elastic search
                logger.info("Working with the normal More like this query with esmeAddr filter");
                long start = System.currentTimeMillis();
                for (int i = 0; i < InputMessageGenerator.inputMessagesFromProd.getInputMessages()
                                .size(); i++) {
                        logger.info("\n\nSearching Message : " + i);
                        if (!ElasticSearch.templateNameSearch(esClient, i, prod_template_index_name)) {
                                logger.info("No matching template in database.");
                                logger.info("The actual template name is : "
                                                + InputMessageGenerator.inputMessagesFromProd.getInputMessages()
                                                                .get(i)
                                                                .getTemplate_id());
                                inputMessagesWithNoMatchingTemplates++;
                                continue;
                        }
                        if (ElasticSearch.moreLikeThisTemplateSearch(esClient,
                                        prod_template_index_name,
                                        numberOfHitsToBeConsidered,
                                        InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(i)
                                                        .getMessage_text(),
                                        InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(i)
                                                        .getEsmeaddr())) {
                                continue;
                        } else {
                                logger.info("No match even the actual template in ES DB");
                                logger.info("Actual Template Name : " + InputMessageGenerator.inputMessagesFromProd
                                                .getInputMessages().get(i).getTemplate_id());
                                logger.info("Actual Template Body: " + InputMessageGenerator.inputMessagesFromProd
                                                .getInputMessages().get(i).getMessage_text());
                        }
                }

                long end = System.currentTimeMillis();
                logger.info("Total Execution Time : " + (end - start) + "ms");
                logger.info("Counter value : " + counter);
                logger.info("The accuracy without word count is : "
                                + ((float) counter * 1.0f
                                                / (InputMessageGenerator.inputMessagesFromProd.getInputMessages()
                                                                .size()))
                                                * 100);
                logger.info("The average search time for the no match found : ");
                logger.info("The number of input messages whose actual templates are not present in the database are: "
                                + inputMessagesWithNoMatchingTemplates);
                logger.info("The average time for full scan in elastic search : " + (float) time / (70 * 1.0));
                logger.info("The total time for full scan in elastic search : " + time);

                ElasticSearch.moreLikeThisTemplateSearch(esClient, prod_template_index_name, numberOfHitsToBeConsidered,
                                InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(115)
                                                .getMessage_text(),
                                InputMessageGenerator.inputMessagesFromProd.getInputMessages().get(115).getEsmeaddr());

        }

}
