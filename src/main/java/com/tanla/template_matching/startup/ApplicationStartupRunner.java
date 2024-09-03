package com.tanla.template_matching.startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.PreExistingTemplates;
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

                List<Template> cacheTemplateList = new ArrayList<>();

                ElasticSearch.moreLikeThisTemplateSearch(esClient,
                                PreExistingTemplates.templates_index, PreExistingTemplates.message);

        }

}
