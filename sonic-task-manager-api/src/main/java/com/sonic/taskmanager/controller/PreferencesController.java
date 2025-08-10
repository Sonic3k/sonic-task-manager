package com.sonic.taskmanager.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
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

    /**
     * Get all preferences
     */
    @GetMapping
    public PreferencesResponse getAllPreferences() {
        Map<String, String> preferences = preferencesService.getAllPreferences();
        
        PreferencesResponse response = new PreferencesResponse();
        response.setSuccess(true);
        response.setPreferences(preferences);
        return response;
    }

    /**
     * Get specific preference by key
     */
    @GetMapping("/{key}")
    public ResponseEntity<BaseResponse> getPreference(@PathVariable("key") String key) {
        String value = preferencesService.getPreferenceWithDefault(key);
        
        if (value != null) {
            PreferenceValueResponse response = new PreferenceValueResponse();
            response.setSuccess(true);
            response.setKey(key);
            response.setValue(value);
            return ResponseEntity.ok(response);
        } else {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Preference not found");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Set specific preference
     */
    @PutMapping("/{key}")
    public ResponseEntity<BaseResponse> setPreference(@PathVariable("key") String key, 
                                                     @RequestBody String value) {
        try {
            preferencesService.setPreference(key, value);
            
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("Preference updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to update preference");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Set multiple preferences at once
     */
    @PutMapping
    public ResponseEntity<BaseResponse> setPreferences(@RequestBody Map<String, String> preferences) {
        try {
            preferencesService.setPreferences(preferences);
            
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("Preferences updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to update preferences");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete preference
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<BaseResponse> deletePreference(@PathVariable("key") String key) {
        boolean deleted = preferencesService.deletePreference(key);
        
        BaseResponse response = new BaseResponse();
        if (deleted) {
            response.setSuccess(true);
            response.setMessage("Preference deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setError("Preference not found");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reset all preferences to defaults
     */
    @PostMapping("/reset")
    public PreferencesResponse resetPreferences() {
        try {
            preferencesService.resetToDefaults();
            Map<String, String> preferences = preferencesService.getAllPreferences();
            
            PreferencesResponse response = new PreferencesResponse();
            response.setSuccess(true);
            response.setMessage("Preferences reset to defaults");
            response.setPreferences(preferences);
            return response;
            
        } catch (Exception e) {
            PreferencesResponse response = new PreferencesResponse();
            response.setSuccess(false);
            response.setError("Failed to reset preferences");
            response.setMessage(e.getMessage());
            return response;
        }
    }

    /**
     * Initialize default preferences (useful for first run)
     */
    @PostMapping("/initialize")
    public PreferencesResponse initializePreferences() {
        try {
            preferencesService.initializeDefaultPreferences();
            Map<String, String> preferences = preferencesService.getAllPreferences();
            
            PreferencesResponse response = new PreferencesResponse();
            response.setSuccess(true);
            response.setMessage("Preferences initialized successfully");
            response.setPreferences(preferences);
            return response;
            
        } catch (Exception e) {
            PreferencesResponse response = new PreferencesResponse();
            response.setSuccess(false);
            response.setError("Failed to initialize preferences");
            response.setMessage(e.getMessage());
            return response;
        }
    }

}