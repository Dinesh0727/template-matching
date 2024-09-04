package com.tanla.template_matching.startup;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.Messages;
import com.tanla.template_matching.entity.Template;
import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        public static String present_template_name = "alert_1";

        @Override
        public void run(String... args) throws IOException {
                System.out.println("Hi");

                // ArrayList<Template> cacheTemplateList = new ArrayList<>();
                System.out.println("The size of the example texts array is : " + Messages.exampleTexts.size());

                String indexNameLarge = "template_texts";

                // Using without regex elastic search
                for (int i = 0; i < Messages.exampleTexts.size(); i++) {
                        System.out.println("================================================");
                        System.out.println("================================================");
                        System.out.println("We are dealing with the input message : " + i);
                        present_template_name = Messages.exampleTextsTemplateNames.get(i);
                        if (ElasticSearch.moreLikeThisTemplateSearch(esClient, indexNameLarge,
                                        Messages.exampleTexts.get(i))) {
                                System.out.println("======================");
                                System.out.println("======================");
                                System.out.println("Actual matching template_name is : "
                                                + Messages.exampleTextsTemplateNames.get(i));
                                // System.out.println("Actual matching template body \n" +
                                // Messages.exampleTexts.get(i));
                        }
                }

                // Using regex elastic search

        }

}
