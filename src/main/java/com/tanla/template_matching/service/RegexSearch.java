package com.tanla.template_matching.service;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import io.micrometer.common.util.StringUtils;

public class RegexSearch {
    public static boolean matchTemplate(String inputMsg, String preExistingTemplateBody, int index) {

        String first = preExistingTemplateBody;

        String escapeCharSet1 = "\\^$.|?*+()[]";

        String escapeCharSet2 = "{}";

        String rawInputMsg = inputMsg.replace("\n", "\\n");
        String inputMsgProcessed = escapeSpecialCharacters(rawInputMsg, escapeCharSet1);

        String x = escapeSpecialCharacters(preExistingTemplateBody, escapeCharSet1);
        String patternStr1 = x.replaceAll("\\{\\{\\d*\\}\\}", "(.*)");
        String finalPattern = escapeSpecialCharacters(patternStr1, escapeCharSet2);

        if (StringUtils.isBlank(finalPattern) || "null".equals(finalPattern)) {
            return false; // continue with the next template
        }

        List<String> params = new ArrayList<>(List.of("value_1", "value_2", "value_3"));

        if (StringUtils.isNotEmpty(finalPattern) && !"null".equals(finalPattern)) {
            try {
                Pattern pattern = Pattern.compile(finalPattern);
                Matcher matcher = pattern.matcher(inputMsg);
                while (matcher.matches()) {
                    // counter++;

                    Map<String, String> paramNames = new HashMap<String, String>();
                    for (int i = 0; i < params.size(); i++) {
                        paramNames.put(params.get(i) + "", matcher.group(i + 1));
                    }
                    System.out.println(paramNames);
                    System.out.println("Found the appropriate template: \n" + preExistingTemplateBody);
                    return true;
                }
                if (index == 126) {
                    System.out.println("Input message after processing the escape characters: " + inputMsgProcessed);
                    System.out.println("Pre Existing Template string : " +
                            preExistingTemplateBody);
                    System.out.println("Final Pattern for Id : " + index + " " + finalPattern +
                            "\n");
                    // Visualize step-by-step matching
                    matcher = pattern.matcher(inputMsg);
                    while (matcher.find()) {
                        System.out.println("Partial match found:");
                        System.out.println("Start index: " + matcher.start());
                        System.out.println("End index: " + matcher.end());
                        System.out.println("Matched text: " + matcher.group());
                    }
                }

            } catch (PatternSyntaxException exception) {
                System.out.println("Id of the document where the exception occured : " + index);
                System.out.println("Before the deletion of anything : " + first);
                System.out.println("\nAfter the escaping of character set 2 : " + patternStr1);
                System.err.println("\n" + exception);
            }
        }

        return false;
    }

    private static String escapeSpecialCharacters(String preExistingTemplateBody, String escapeCharSet) {
        return preExistingTemplateBody.replaceAll("([" + Pattern.quote(escapeCharSet) + "])", "\\\\$1");
    }
}
