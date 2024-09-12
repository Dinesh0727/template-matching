package com.tanla.template_matching.entity;

import java.util.List;

public class InputMessages {
    private List<ProdInputMessage> inputMessages;

    public InputMessages() {
    }

    public InputMessages(List<ProdInputMessage> inputMessages) {
        this.inputMessages = inputMessages;
    }

    public List<ProdInputMessage> getInputMessages() {
        return inputMessages;
    }

    public void setInputMessages(List<ProdInputMessage> inputMessages) {
        this.inputMessages = inputMessages;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("");
        for (ProdInputMessage x : inputMessages) {
            string.append(x.toString());
            string.append("\n\n");
        }
        return "InputMessages : \n " + string;
    }

}
