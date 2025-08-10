package com.sonic.taskmanager.model.response;

public class PreferenceValueResponse extends BaseResponse {
    private String key;
    private String value;

    public PreferenceValueResponse() {
        super();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
