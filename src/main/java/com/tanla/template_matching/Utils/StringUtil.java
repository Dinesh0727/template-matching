package com.tanla.template_matching.Utils;

import java.util.regex.Pattern;

public class StringUtil {

    public static String escapeCharSet1 = "\\^$.|?*+()[]";

    public static String escapeCharSet2 = "{}";

    public static String escapeSpecialCharacters(String preExistingTemplateBody, String escapeCharSet) {
        return preExistingTemplateBody.replaceAll("([" + Pattern.quote(escapeCharSet) + "])", "\\\\$1");
    }
}
