package com.tanla.template_matching.search;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.tanla.template_matching.Utils.StringUtil;

import io.micrometer.common.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class RegexSearch {

    public static void regexSearch(String searchText, List<String> strings) {
        System.out.print("===========================================================\nRegex Search\n\n");
        long start = System.currentTimeMillis();
        int i = 0;
        for (; i < strings.size(); i++) {
            if (RegexSearch.matchTemplate(searchText, strings.get(i), i + 1)) {
                System.out.println("Found the Template Match with regex matching: " + strings.get(i));
                break;
            }
        }
        if (i == strings.size())
            System.out.println("No full match found.");
        long end = System.currentTimeMillis();
        System.out.println("Time taken to find the template through regex search : " + (end - start));
        System.out.println("============================================================");
    }

    public static boolean matchTemplate(String inputMsg, String preExistingTemplateBody, int index) {

        String finalPattern = StringUtil.escapeSpecialCharacters(
                StringUtil.escapeSpecialCharacters(preExistingTemplateBody, StringUtil.escapeCharSet1)
                        .replaceAll("\\{\\{\\d*\\}\\}", "(.*)"),
                StringUtil.escapeCharSet2);

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
                    // System.out.println("Found the appropriate template: \n" +
                    // preExistingTemplateBody);
                    System.out.println("The template id : " + index);
                    return true;
                }

            } catch (PatternSyntaxException exception) {
                System.out.println("Id of the document where the exception occured : " + index);
                System.err.println("\n" + exception);
            }
        }

        return false;
    }
}
