package com.sonic.taskmanager.service;

import com.sonic.taskmanager.model.Preferences;
import com.sonic.taskmanager.repository.PreferencesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;

    // Default preferences
    private static final Map<String, String> DEFAULT_PREFERENCES = Map.of(
        "daily_mood", "chill",
        "work_hours_start", "09:00",
        "work_hours_end", "17:00", 
        "focus_session_duration", "90",
        "gentle_reminder_frequency", "3",
        "show_completed_tasks", "true",
        "workspace_theme", "warm"
    );

    public PreferencesService(PreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    /**
     * Get preference by key
     */
    @Transactional(readOnly = true)
    public Optional<String> getPreference(String key) {
        return preferencesRepository.findByKey(key)
                .map(Preferences::getValue);
    }

    /**
     * Get preference by key with default value
     */
    @Transactional(readOnly = true)
    public String getPreference(String key, String defaultValue) {
        return getPreference(key).orElse(defaultValue);
    }

    /**
     * Get preference by key with system default
     */
    @Transactional(readOnly = true)
    public String getPreferenceWithDefault(String key) {
        return getPreference(key).orElse(DEFAULT_PREFERENCES.get(key));
    }

    /**
     * Set preference value
     */
    public void setPreference(String key, String value) {
        Optional<Preferences> existingPref = preferencesRepository.findByKey(key);
        
        if (existingPref.isPresent()) {
            // Update existing
            Preferences pref = existingPref.get();
            pref.setValue(value);
            preferencesRepository.save(pref);
        } else {
            // Create new
            Preferences newPref = new Preferences();
            newPref.setKey(key);
            newPref.setValue(value);
            preferencesRepository.save(newPref);
        }
    }

    /**
     * Get all preferences as a map
     */
    @Transactional(readOnly = true)
    public Map<String, String> getAllPreferences() {
        List<Preferences> allPrefs = preferencesRepository.findAll();
        
        Map<String, String> prefsMap = allPrefs.stream()
                .collect(Collectors.toMap(
                    Preferences::getKey,
                    Preferences::getValue
                ));
        
        // Add defaults for missing keys
        for (Map.Entry<String, String> defaultEntry : DEFAULT_PREFERENCES.entrySet()) {
            prefsMap.putIfAbsent(defaultEntry.getKey(), defaultEntry.getValue());
        }
        
        return prefsMap;
    }

    /**
     * Set multiple preferences at once
     */
    public void setPreferences(Map<String, String> preferences) {
        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            setPreference(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Delete preference
     */
    public boolean deletePreference(String key) {
        if (preferencesRepository.existsByKey(key)) {
            preferencesRepository.deleteById(key);
            return true;
        }
        return false;
    }

    /**
     * Initialize default preferences if they don't exist
     */
    @Transactional
    public void initializeDefaultPreferences() {
        for (Map.Entry<String, String> defaultPref : DEFAULT_PREFERENCES.entrySet()) {
            if (!preferencesRepository.existsByKey(defaultPref.getKey())) {
                setPreference(defaultPref.getKey(), defaultPref.getValue());
            }
        }
    }

    /**
     * Reset preferences to defaults
     */
    public void resetToDefaults() {
        preferencesRepository.deleteAll();
        initializeDefaultPreferences();
    }

    /**
     * Get work hours start as integer (hour of day)
     */
    @Transactional(readOnly = true)
    public int getWorkHoursStart() {
        String workStart = getPreferenceWithDefault("work_hours_start");
        try {
            return Integer.parseInt(workStart.split(":")[0]);
        } catch (Exception e) {
            return 9; // Default to 9 AM
        }
    }

    /**
     * Get work hours end as integer (hour of day)
     */
    @Transactional(readOnly = true)
    public int getWorkHoursEnd() {
        String workEnd = getPreferenceWithDefault("work_hours_end");
        try {
            return Integer.parseInt(workEnd.split(":")[0]);
        } catch (Exception e) {
            return 17; // Default to 5 PM
        }
    }

    /**
     * Get focus session duration in minutes
     */
    @Transactional(readOnly = true)
    public int getFocusSessionDuration() {
        String duration = getPreferenceWithDefault("focus_session_duration");
        try {
            return Integer.parseInt(duration);
        } catch (Exception e) {
            return 90; // Default 90 minutes
        }
    }

    /**
     * Get gentle reminder frequency in days
     */
    @Transactional(readOnly = true)
    public int getGentleReminderFrequency() {
        String frequency = getPreferenceWithDefault("gentle_reminder_frequency");
        try {
            return Integer.parseInt(frequency);
        } catch (Exception e) {
            return 3; // Default 3 days
        }
    }

    /**
     * Check if completed tasks should be shown
     */
    @Transactional(readOnly = true)
    public boolean shouldShowCompletedTasks() {
        String showCompleted = getPreferenceWithDefault("show_completed_tasks");
        return "true".equalsIgnoreCase(showCompleted);
    }
}