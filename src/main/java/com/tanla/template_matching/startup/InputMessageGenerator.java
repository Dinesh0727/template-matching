package com.tanla.template_matching.startup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanla.template_matching.data.InputData2;
import com.tanla.template_matching.entity.InputMessage;
import com.tanla.template_matching.entity.InputMessages;

public class InputMessageGenerator {

    public static ArrayList<InputMessage> inputMessages = new ArrayList<>();

    public static Logger logger = LogManager.getLogger(InputMessageGenerator.class);

    public static InputMessages inputMessagesFromProd;

    public static void run() {
        int size = InputData2.requestInputMsg.size();
        for (int i = 0; i < size; i++) {
            InputMessage inputMessage = new InputMessage(i + 1, InputData2.esmeaddr.get(i),
                    InputData2.templateNames.get(i), InputData2.requestInputMsg.get(i).split(" ").length,
                    InputData2.requestInputMsg.get(i));

            inputMessages.add(inputMessage);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File("/home/dinesh/Elastic_Search_POC/Autotemplate_req.json");
            inputMessagesFromProd = objectMapper.readValue(file, InputMessages.class);

            // Print the templates
            // logger.info(jsonNode);
        } catch (IOException e) {
            logger.info(e.fillInStackTrace());
        }
    }
}
