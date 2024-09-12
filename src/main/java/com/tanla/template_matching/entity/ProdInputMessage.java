package com.tanla.template_matching.entity;

public class ProdInputMessage {
    private String template_id;

    private String message_text;

    private String esmeaddr;

    public ProdInputMessage() {
    }

    public ProdInputMessage(String template_id, String message_text, String esmeaddr) {
        this.template_id = template_id;
        this.message_text = message_text;
        this.esmeaddr = esmeaddr;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getEsmeaddr() {
        return esmeaddr;
    }

    public void setEsmeaddr(String esmeaddr) {
        this.esmeaddr = esmeaddr;
    }

    @Override
    public String toString() {
        return "ProdInputMessage [template_id=" + template_id + ", message_text="
                + message_text.replace("\n", "\\n").replace("\r", "\\r") + ", esmeaddr="
                + esmeaddr + "]";
    }

}
