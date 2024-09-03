package com.tanla.template_matching.entity;

import com.tanla.template_matching.Utils.StringUtil;

public class Template {
    private String esmeaddr;

    private String template_name;

    private String template_namespace_id;

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

    public String getTemplate_namespace_id() {
        return template_namespace_id;
    }

    public void setTemplate_namespace_id(String template_namespace_id) {
        this.template_namespace_id = template_namespace_id;
    }

    public String getMsg_body() {
        return msg_body;
    }

    public void setMsg_body(String msg_body) {
        this.msg_body = msg_body;
    }

    @Override
    public String toString() {
        return "Template [esmeaddr=" + esmeaddr + ", \ntemplate_name=" + template_name + "\n, template_namespace_id="
                + template_namespace_id + "\n, msg_body=\n" + StringUtil.escapeSpecialCharacters(msg_body, "\\") + "]";
    }

}
