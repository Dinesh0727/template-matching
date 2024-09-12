package com.tanla.template_matching.entity;

public class InputMessage {
    private int sl_no;

    private String esmeaddr;

    private String template_name;

    private int word_count;

    private String msg_body;

    public InputMessage() {
    }

    public InputMessage(int sl_no, String esmeaddr, String template_name, int word_count, String msg_body) {
        this.sl_no = sl_no;
        this.esmeaddr = esmeaddr;
        this.template_name = template_name;
        this.word_count = word_count;
        this.msg_body = msg_body;
    }

    public int getSl_no() {
        return sl_no;
    }

    public void setSl_no(int sl_no) {
        this.sl_no = sl_no;
    }

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

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
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
        return "InputMessage [sl_no=" + sl_no + ", esmeaddr=" + esmeaddr + ", template_name=" + template_name
                + ", word_count=" + word_count + ", msg_body=" + msg_body.replace("\n", "\\n") + "]";
    }

}
