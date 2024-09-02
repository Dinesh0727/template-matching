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
public class LoadTester implements CommandLineRunner {
    public static String searchText = "Dear Parent,\n\nThank you for registering with CodeAcademy\n\nRequest you to update your & your child’s names & email id to receive your personalized Coding Fundamentals Certificate\n\nClick here to fill in your details: https://codeacademy.com/update-details \n\nRegards,\nTeam CodeAcademy";

    @Autowired
    public ElasticsearchClient elasticsearchClient;

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        Long start, end;
        int i;

        Thread.sleep(5000); // Makes sure that the database has been set up

        String searchText = "Dear Parent,\n\nThank you for registering with CodeAcademy\n\nRequest you to update your & your child’s names & email id to receive your personalized Coding Fundamentals Certificate\n\nClick here to fill in your details: https://codeacademy.com/update-details \n\nRegards,\nTeam CodeAcademy";

        System.out.println("==========================================================================");
        System.out.println("Running Elastic Search for 1, 1000, 100,000 rounds");
        System.out.println("==========================================================================");
        start = System.currentTimeMillis();
        ElasticSearch.moreLikeThisSearch(elasticsearchClient, PreExistingTemplates.templates_index, searchText);
        end = System.currentTimeMillis();
        System.out.println("Execution time for 1 run = " + (end - start) + "ms");

        start = System.currentTimeMillis();
        for (i = 0; i < 1000; i++) {
            ElasticSearch.moreLikeThisSearch(elasticsearchClient, PreExistingTemplates.templates_index, searchText);
        }
        end = System.currentTimeMillis();
        System.out.println("Execution time for 1000 runs = " + (end - start) + "ms");
        System.out.println("Execution time for 1 run(avg) = " + (end - start) / 1000.0 + "ms");

        start = System.currentTimeMillis();
        for (i = 0; i < 100000; i++) {
            ElasticSearch.moreLikeThisSearch(elasticsearchClient, PreExistingTemplates.templates_index, searchText);
        }
        end = System.currentTimeMillis();
        System.out.println("Execution time for 100,000 runs = " + (end - start) + "ms");
        System.out.println("Execution time for 1 run(avg) = " + (end - start) / 100000.0 + "ms");

        System.out.println("==========================================================================");
        System.out.println("Running Regex Search for 1, 1000, 100,000 rounds");
        System.out.println("==========================================================================");
        start = System.currentTimeMillis();
        RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);
        end = System.currentTimeMillis();
        System.out.println("Execution time for 1 run = " + (end - start) + "ms");

        start = System.currentTimeMillis();
        for (i = 0; i < 1000; i++) {
            RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);
        }
        end = System.currentTimeMillis();
        System.out.println("Execution time for 1000 runs = " + (end - start) + "ms");
        System.out.println("Execution time for 1 run(avg) = " + (end - start) / 1000.0 + "ms");

        start = System.currentTimeMillis();
        for (i = 0; i < 100000; i++) {
            RegexSearch.regexSearch(searchText, PreExistingTemplates.strings);
        }
        end = System.currentTimeMillis();
        System.out.println("Execution time for 100,000 runs = " + (end - start) + "ms");
        System.out.println("Execution time for 1 run(avg) = " + (end - start) / 100000.0 + "ms");

    }
}
