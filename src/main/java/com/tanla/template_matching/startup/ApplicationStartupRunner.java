package com.tanla.template_matching.startup;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.PreExistingTemplates;
import com.tanla.template_matching.search.ElasticSearch;
import com.tanla.template_matching.search.RegexSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

        @Autowired
        public ElasticsearchClient esClient;

        @Override
        public void run(String... args) throws IOException {

                String searchText = "Dear Parent,\n\nThank you for registering with CodeAcademy\n\nRequest you to update your & your child’s names & email id to receive your personalized Coding Fundamentals Certificate\n\nClick here to fill in your details: https://codeacademy.com/update-details \n\nRegards,\nTeam CodeAcademy";

                String template_text_index = PreExistingTemplates.templates_index;

                ElasticSearch.createIndex(esClient, template_text_index);

                ElasticSearch.bulkRequest(esClient, PreExistingTemplates.strings, template_text_index);

                // Searching for the template match
                ElasticSearch.moreLikeThisSearch(esClient, template_text_index,
                                searchText);

                RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);

        }

}
