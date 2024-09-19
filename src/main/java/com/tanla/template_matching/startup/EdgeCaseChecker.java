package com.tanla.template_matching.startup;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tanla.template_matching.Utils.DummyMessages;
import com.tanla.template_matching.constants.Constants;
import com.tanla.template_matching.search.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

/*
 * I will have to check the below use cases
 * 1. Case insensitivity behaviour
 *      1a : Exact Matching template is present for below text
 *      1b : Matching Template is present, but with different casing in some words
 *  Result: We can customise the usecase for case sensitive and insensitive. It is in our own way
 * 
 * 
 * 2. Special Characters like "\n", "\r", "\t", etc.
 *  Result: In the present approach we had, as we are using regex matching, "\n" will be taken care of. 
 *  Example:
 *    1. "Dear DHARMENDRA  SINGH,\n\nRs.2590.0 was successfully transferred to your AIRP account xx3543 on 04 July at 02:58. Transaction reference number (UTR) is 418652757670. \n\nClick here for more details: https://bharatpe.in/UvYCi"
 *    2. "Dear DHARMENDRA  SINGH,\n Rs.2590.0 was successfully transferred to your AIRP account xx3543 on 04 July at 02:58. Transaction reference number (UTR) is 418652757670. \n\nClick here for more details: https://bharatpe.in/UvYCi"
 * 
 *    The above two texts will not be matched to same template because of regex match.
 * 
 *    Try to print the top 5 hits from the ES Response, if there is no template, we create a new one.
 *    Elastic Search ignores 
 *    
 *    I have set the default analyzer for msg_body field as my_analyzer in the index, my-new-index and checked whether it is behaving the same as old index.
 * 
 * 3. Number of spaces behaviour
 *   Well, the number of spaces, will not be considered by elastic search, so I have used whitespace tokenizer for differentiating them, so it started differentating them.
 *    ==============================================
 *    We are running the Case with String 1 : KFS Acknowledgement!\nClick https://m.bflcomm.in/wBLkWEjXeb9 to view Key Fact Statement (KFS) accepted by you for loan appln no. PJE5GOL13116851\nBajaj Finance Ltd
 *    ==============================================
 *    The searching time in the ES Database is : 46ms
 *    I am here the number of hits are : 5
 *    The template_id : doc_pod_kfs_accepted_laip
 *    Hit 1 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : doc_pod_kfs_accepted_rtl
 *    Hit 2 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : docpod_kfs_acknowlegement_salpl
 *    Hit 3 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : b2c_plcard_loan_kfs_accepted
 *    Hit 4 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : doc_pod_rural_gpay_kfs_accepted_pl
 *    Hit 5 : KFS Acknowledgement!\nClick {{1}}  to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    I am in the true case in the top hit
 *    Found the Input Message matching template
 *    The execution time for Case 2 with string 1 : 52ms
 *    ==============================================
 *    We are running the Case with String 2 : KFS Acknowledgement! \nClick https://m.bflcomm.in/wBLkWEjXeb9 to view Key Fact Statement (KFS) accepted by you for loan appln no. PJE5GOL13116851\nBajaj Finance Ltd
 *    ==============================================
 *    The searching time in the ES Database is : 19ms
 *    I am here the number of hits are : 5
 *    The template_id : doc_pod_kfs_accepted_b2b
 *    Hit 1 : KFS Acknowledgement! \nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : doc_pod_kfs_accepted_pl
 *    Hit 2 : KFS Acknowledgement! \nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : docpod_kfs_accepted_twf
 *    Hit 3 : KFS Acknowledgement! \nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : doc_pod_kfs_accepted_laip
 *    Hit 4 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    The template_id : doc_pod_kfs_accepted_rtl
 *    Hit 5 : KFS Acknowledgement!\nClick {{1}} to view Key Fact Statement (KFS) accepted by you for loan appln no. {{2}}\nBajaj Finance Ltd
 *    I am in the true case in the top hit
 *    Found the Input Message matching template
 *    The execution time for Case 2 with string 2 : 20ms
 *    ====================================================
 * 
 * 
 * 4. When there is no matching template for the given text, that is for testing you can consider two type of texts, one with completely non existent words and other with an existing type but replace some words.
 * 
 *   Result :
 *     I have taken two different texts, one with dummy text with no similar tokens and other with similar tokens only space differs
 *     4a : "dfgh uytr lkijh jfhn uiop shfum jshaik hdgfek hgcsin hdhdkb jchknhjdj jdbhjksmnxj jkxhcjkd bwgey bcvdhhe"
 *     4b : "KFS Acknowledgement!\n\nClick https://m.bflcomm.in/wBLkWEjXeb9 to view Key Fact Statement (KFS) accepted by you for loan appln no. PJE5GOL13116851\nBajaj Finance Ltd"
 * 
 *     So the results turn out for 1000 searches for both of them is: 
*           The execution time for Case 4 with dummy string  : 7150ms
*           The average execution time for Case 4 with dummy string : 7.15ms    
*           The execution time for Case 4 with similar string  : 31682ms
*           The average execution time for Case 4 with similar string : 31.682ms 
 */

@Component
public class EdgeCaseChecker implements CommandLineRunner {

    @Autowired
    public ElasticsearchClient esClient;

    public static final Logger logger = LogManager.getLogger(EdgeCaseChecker.class);

    @Override
    public void run(String... args) throws IOException {

        logger.info("====================================================");
        logger.info("Correct Text : " + DummyMessages.correctTextForFullScanExecutionTime.replace("\n", "\\n"));
        logger.info("==============================================");
        logger.info(
                "Case Altered Text : " + DummyMessages.charReplaceTextForFullScanExecutionTime.replace("\n", "\\n"));
        logger.info("====================================================");

        // Case 1:

        // Case Sensitive Searching
        // 1a
        long start, end;

        logger.info("====================================================");
        logger.info("Case sensitive searching");
        logger.info("==============================================");
        logger.info("We are running the Case 1a");
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.correctTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCorrectTextForFullScanExecutionTime, true)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 1a : " + (end - start) + "ms");

        // 1b

        logger.info("==============================================");
        logger.info("We are running the Case 1b");
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.charReplaceTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCharReplaceTextForFullScanExecutionTime, true)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 1b : " + (end - start) + "ms");

        logger.info("====================================================");
        logger.info("Case Insensitive searching");
        logger.info("==============================================");
        logger.info("We are running the Case 1a");
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.correctTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCorrectTextForFullScanExecutionTime, false)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 1a : " + (end - start) + "ms");

        // 1b

        logger.info("==============================================");
        logger.info("We are running the Case 1b");
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchWithCaseSensitivity(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.charReplaceTextForFullScanExecutionTime,
                DummyMessages.esmeAddrForCharReplaceTextForFullScanExecutionTime, false)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 1b : " + (end - start) + "ms");

        // Case 2:

        logger.info("====================================================");
        logger.info("Case 2 :");
        logger.info("The index in which I am searching the text : " + Constants.indexName);
        logger.info("==============================================");
        logger.info("We are running the Case with String 1 : "
                + DummyMessages.specialCharactersCaseString1.replace("\n", "\\n"));
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchForNumberOfSpaces(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.specialCharactersCaseString1,
                DummyMessages.esmeAddrForSpecialCharactersCaseString1)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 2 with string 1 : " + (end - start) + "ms");

        logger.info("==============================================");
        logger.info("We are running the Case with String 2 : "
                + DummyMessages.specialCharactersCaseString2.replace("\n", "\\n"));
        logger.info("==============================================");

        start = System.currentTimeMillis();
        if (ElasticSearch.moreLikeThisTemplateSearchForNumberOfSpaces(esClient, Constants.indexName,
                Constants.numberOfHitsToBeConsidered, DummyMessages.specialCharactersCaseString2,
                DummyMessages.esmeAddrForSpecialCharactersCaseString2)) {
            logger.info("Found the Input Message matching template");
        }
        end = System.currentTimeMillis();

        logger.info("The execution time for Case 2 with string 2 : " + (end - start) + "ms");

        // Case 4:

        // logger.info("====================================================");
        // logger.info("Case 4 :");
        // logger.info("The index in which I am searching the text : " +
        // Constants.indexName);
        // logger.info("==============================================");
        // logger.info("We are running the Case 4 with dummy text with no similar tokens
        // : "
        // + DummyMessages.dummyTextWithNoSimilarTokensForCase4.replace("\n", "\\n"));
        // logger.info("==============================================");

        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000; i++) {
        // if (ElasticSearch.moreLikeThisTemplateSearchForNumberOfSpaces(esClient,
        // Constants.indexName,
        // Constants.numberOfHitsToBeConsidered,
        // DummyMessages.dummyTextWithNoSimilarTokensForCase4,
        // DummyMessages.esmeaddrFordummyTextWithNoSimilarTokenForCase4)) {
        // logger.info("Found the Input Message matching template");
        // } else {
        // logger.info("Not Found in the database even after full scan");
        // }
        // }
        // end = System.currentTimeMillis();

        // logger.info("The execution time for Case 4 with dummy string : " + (end -
        // start) + "ms");
        // logger.info("The average execution time for Case 4 with dummy string : " +
        // (end - start) / (1000 * 1.0) + "ms");

        // logger.info("==============================================");
        // logger.info("We are running the Case 4 with similar tokens : "
        // + DummyMessages.esmeaddrForSimilarTextForCase4.replace("\n", "\\n"));
        // logger.info("==============================================");

        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000; i++) {
        // if (ElasticSearch.moreLikeThisTemplateSearch(esClient, Constants.indexName,
        // Constants.numberOfHitsToBeConsidered, DummyMessages.similarTextForCase4,
        // DummyMessages.esmeaddrForSimilarTextForCase4)) {
        // // logger.info("Found the Input Message matching template");
        // } else {
        // // logger.info("Not Found in the database even after full scan");
        // }
        // }
        // end = System.currentTimeMillis();

        // logger.info("The execution time for Case 4 with similar string : " + (end -
        // start) + "ms");
        // logger.info(
        // "The average execution time for Case 4 with similar string : " + (end -
        // start) / (1000 * 1.0) + "ms");

    }
}