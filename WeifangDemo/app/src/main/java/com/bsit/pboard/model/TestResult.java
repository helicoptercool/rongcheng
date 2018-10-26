package com.bsit.pboard.model;

public class TestResult {
    private String code; //"100001"
    private String message; //"算法标示空"
    private String content; //null

    public TestResult(String code, String message, String content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
