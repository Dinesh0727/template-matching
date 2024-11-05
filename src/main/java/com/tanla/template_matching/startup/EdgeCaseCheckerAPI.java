package com.tanla.template_matching.startup;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.tanla.template_matching.Utils.DummyMessages;
import com.tanla.template_matching.constants.Constants;
import com.tanla.template_matching.search.ElasticSearch;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EdgeCaseCheckerAPI {

    @Autowired
    public ElasticsearchClient esClient;

    public static final Logger logger = LogManager.getLogger(EdgeCaseCheckerAPI.class);

    public void runCase1a(boolean caseSensitive) throws IOException {
        logger.info("Running Case 1a with caseSensitive=" + caseSensitive);
        logger.info("The string that is being searched is : "
                + DummyMessages.correctTextForFullScanExecutionTime.replace("\n", "\\n"));
        long start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.correctTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCorrectTextForFullScanExecutionTime, caseSensitive)) {
            logger.info("Found the Input Message matching template");
        }
        long end = System.currentTimeMillis();
        logger.info("Execution time for Case 1a: " + (end - start) + "ms");
    }

    public void runCase1b(boolean caseSensitive) throws IOException {
        logger.info("Running Case 1b with caseSensitive=" + caseSensitive);
        logger.info("The string that is being searched is : "
                + DummyMessages.charReplaceTextForFullScanExecutionTime.replace("\n", "\\n"));
        long start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.charReplaceTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCharReplaceTextForFullScanExecutionTime, caseSensitive)) {
            logger.info("Found the Input Message matching template");
        }
        long end = System.currentTimeMillis();
        logger.info("Execution time for Case 1b: " + (end - start) + "ms");
    }

    public void runCase2() throws IOException {
        logger.info("Running Case 2");
        logger.info("The string that is being searched is : "
                + DummyMessages.specialCharactersCaseString1.replace("\n", "\\n"));
        long start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchForNumberOfSpaces(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.specialCharactersCaseString1,
                DummyMessages.esmeAddrForSpecialCharactersCaseString1)) {
            logger.info("Found the Input Message matching template");
        }
        long end = System.currentTimeMillis();
        logger.info("Execution time for Case 2 with string 1: " + (end - start) + "ms");
    }

    public void runCase4(boolean withSimilarTokens) throws IOException {
        logger.info("Running Case 4 with similarTokens=" + withSimilarTokens);
        logger.info("The string that is being searched is : "
                + DummyMessages.similarTextForCase4.replace("\n", "\\n"));
        long start = System.currentTimeMillis();
        if (withSimilarTokens) {
            for (int i = 0; i < 1000; i++) {
                ElasticSearch.moreLikeThisTemplateSearch(esClient, Constants.indexName,
                        Constants.numberOfHitsToBeConsidered, DummyMessages.similarTextForCase4,
                        DummyMessages.esmeaddrForSimilarTextForCase4);
            }
        } else {
            if (ElasticSearch.moreLikeThisTemplateSearchForNumberOfSpaces(esClient,
                    Constants.indexName,
                    Constants.numberOfHitsToBeConsidered,
                    DummyMessages.dummyTextWithNoSimilarTokensForCase4,
                    DummyMessages.esmeaddrFordummyTextWithNoSimilarTokenForCase4)) {
                logger.info("Found the Input Message matching template");
            } else {
                logger.info("Not Found in the database even after full scan");
            }
        }
        long end = System.currentTimeMillis();
        logger.info("Execution time for Case 4: " + (end - start) + "ms");
    }
}
