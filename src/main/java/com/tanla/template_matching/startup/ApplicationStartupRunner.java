package com.tanla.template_matching.startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.Messages;
import com.tanla.template_matching.Utils.PreExistingTemplates;
import com.tanla.template_matching.entity.Message;
import com.tanla.template_matching.entity.Template;
import com.tanla.template_matching.search.ElasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        @Override
        public void run(String... args) throws IOException {
                System.out.println("Hi");

                ArrayList<Template> cacheTemplateList = new ArrayList<>();

                String indexNameLarge = "template_texts";

                // Using without regex elastic search
                for (int i = 0; i < Messages.exampleTexts.size(); i++) {
                        System.out.println("================================================");
                        System.out.println("================================================");
                        System.out.println("We are dealing with the input message : " + i);
                        if (ElasticSearch.moreLikeThisTemplateSearch(esClient, Messages.exampleTexts.get(i),
                                        indexNameLarge)) {
                                System.out.println("======================");
                                System.out.println("======================");
                                System.out.println("Found the template match for input message " + i);
                        }
                }

                // Using regex elastic search

        }

}
