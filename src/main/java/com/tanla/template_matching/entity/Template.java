package com.tanla.template_matching.entity;

import com.tanla.template_matching.Utils.StringUtil;

public class Template {
    private String esmeaddr;

    private String template_name;

    private String word_count;

    private String msg_body;

    public String getEsmeaddr() {
        return esmeaddr;
    }

    public void setEsmeaddr(String esmeaddr) {
        this.esmeaddr = esmeaddr;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getWord_count() {
        return word_count;
    }

    public void setWord_count(String word_count) {
        this.word_count = word_count;
    }

    public String getMsg_body() {
        return msg_body;
    }

    public void setMsg_body(String msg_body) {
        this.msg_body = msg_body;
    }

    @Override
    public String toString() {
        return "Template [esmeaddr=" + esmeaddr + ", \ntemplate_name=" + template_name + "\n, word_count="
                + word_count + "\n, msg_body=\n" + StringUtil.escapeSpecialCharacters(msg_body, "\\") + "]";
    }

}
