package com.sonic.taskmanager.model.response;

import java.util.Map;

public class PreferencesResponse extends BaseResponse {
    private Map<String, String> preferences;

    public PreferencesResponse() {
        super();
    }

    public Map<String, String> getPreferences() { return preferences; }
    public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }
}
