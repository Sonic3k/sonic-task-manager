package com.sonic.taskmanager.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.taskmanager.model.response.BaseResponse;
import com.sonic.taskmanager.model.response.PreferenceValueResponse;
import com.sonic.taskmanager.model.response.PreferencesResponse;
import com.sonic.taskmanager.service.PreferencesService;

@RestController
@RequestMapping("/api/preferences")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class PreferencesController {

    private final PreferencesService preferencesService;

    public PreferencesController(PreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public PreferencesResponse getAllPreferences() {
        Map<String, String> preferences = preferencesService.getAllPreferences();
        
        PreferencesResponse response = new PreferencesResponse();
        response.setSuccess(true);
        response.setPreferences(preferences);
        return response;
    }

    @GetMapping("/{key}")
    public PreferenceValueResponse getPreference(@PathVariable("key") String key) {
        String value = preferencesService.getPreferenceWithDefault(key);
        
        if (value == null) {
            throw new NoSuchElementException("Preference with key '" + key + "' not found");
        }
        
        PreferenceValueResponse response = new PreferenceValueResponse();
        response.setSuccess(true);
        response.setKey(key);
        response.setValue(value);
        return response;
    }

    @PutMapping("/{key}")
    public BaseResponse setPreference(@PathVariable("key") String key, 
                                     @RequestBody String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Preference key cannot be empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Preference value cannot be null");
        }
        
        preferencesService.setPreference(key, value);
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Preference updated successfully");
        return response;
    }

    @PutMapping
    public BaseResponse setPreferences(@RequestBody Map<String, String> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            throw new IllegalArgumentException("Preferences map cannot be empty");
        }
        
        preferencesService.setPreferences(preferences);
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Preferences updated successfully");
        return response;
    }

    @DeleteMapping("/{key}")
    public BaseResponse deletePreference(@PathVariable("key") String key) {
        boolean deleted = preferencesService.deletePreference(key);
        
        if (!deleted) {
            throw new NoSuchElementException("Preference with key '" + key + "' not found");
        }
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Preference deleted successfully");
        return response;
    }

    @PostMapping("/reset")
    public PreferencesResponse resetPreferences() {
        preferencesService.resetToDefaults();
        Map<String, String> preferences = preferencesService.getAllPreferences();
        
        PreferencesResponse response = new PreferencesResponse();
        response.setSuccess(true);
        response.setMessage("Preferences reset to defaults");
        response.setPreferences(preferences);
        return response;
    }

    @PostMapping("/initialize")
    public PreferencesResponse initializePreferences() {
        preferencesService.initializeDefaultPreferences();
        Map<String, String> preferences = preferencesService.getAllPreferences();
        
        PreferencesResponse response = new PreferencesResponse();
        response.setSuccess(true);
        response.setMessage("Preferences initialized successfully");
        response.setPreferences(preferences);
        return response;
    }
}