package com.tanla.template_matching.startup;

import com.tanla.template_matching.search.RegexSearch;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.Messages;
import com.tanla.template_matching.data.InputData2;
import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        public static String present_template_name = "alert_1";

        private Logger logger = LogManager.getLogger(ApplicationStartupRunner.class);

        public static int counter = 0;

        @Override
        public void run(String... args) throws IOException {
                // logger.info("Hi");

                // ArrayList<Template> cacheTemplateList = new ArrayList<>();
                // logger.info("The size of the example texts array is : " +
                // Messages.exampleTexts.size());

                String prod_template_index_name = "prod_templates_with_wordcount_shingle";

                // logger.info("Searching through Elastic Search");

                Integer numberOfHitsToBeConsidered = 3;
                // int i = 24;
                // Using without regex elastic search
                logger.info("Working with the normal More like this query");
                for (int i = 0; i < InputData2.requestInputMsg.size(); i++) {
                        // logger.info("================================================");
                        // logger.info("================================================");
                        // logger.info("We are dealing with the input message : " + i);
                        present_template_name = InputData2.templateNames.get(i);
                        if (ElasticSearch.moreLikeThisTemplateSearch(esClient,
                                        prod_template_index_name,
                                        InputData2.requestInputMsg.get(i), numberOfHitsToBeConsidered,
                                        present_template_name)) {
                                // logger.info("======================");
                                // logger.info("======================");
                                // logger.info("Actual matching template_name is : " + present_template_name);
                                // // logger.info("Actual matching template body \n" +
                                // Messages.exampleTexts.get(i));
                        }
                }
                logger.info("Counter value : " + counter);
                logger.info("The accuracy without word count is : "
                                + (float) counter * 1.0f / InputData2.requestInputMsg.size() * 100);

                counter = 0;
                logger.info("Working with the search of Range Filter");
                for (int i = 0; i < InputData2.requestInputMsg.size(); i++) {
                        // logger.info("================================================");
                        // logger.info("================================================");
                        // logger.info("We are dealing with the input message : " + i);
                        present_template_name = InputData2.templateNames.get(i);
                        if (ElasticSearch.moreLikeThisTemplateSearchWithRangeQuery(esClient,
                                        prod_template_index_name,
                                        InputData2.requestInputMsg.get(i), numberOfHitsToBeConsidered,
                                        present_template_name)) {
                                // logger.info("======================");
                                // logger.info("======================");
                                // logger.info("Actual matching template_name is : " + present_template_name);
                                // // logger.info("Actual matching template body \n" +
                                // Messages.exampleTexts.get(i));
                        }
                }
                logger.info("Counter value : " + counter);
                logger.info("The accuracy with word count is : "
                                + (float) counter * 1.0f / InputData2.requestInputMsg.size() * 100);

                // // Using regex search on the actual templates
                // // logger.info("Matching Regex Search over the correct templates");

                // for (int j = 0; j < InputData2.requestInputMsg.size(); j++) {
                // boolean flag = RegexSearch.matchTemplate(InputData2.requestInputMsg.get(j),
                // InputData2.msgBodyList.get(j), j);

                // if (flag == true) {
                // // logger.info("The message at index " + j + " matched with the pre-existing
                // template");
                // continue;
                // } else {
                // // logger.info("Dealing with the anamoly at index: " + j);
                // // logger.info("Input Message : ");
                // // logger.info(InputData2.requestInputMsg.get(j));
                // // logger.info("Pre-existing Template : ");
                // // logger.info(InputData2.msgBodyList.get(j));
                // }
                // }

                // Using regex elastic search

        }

}
