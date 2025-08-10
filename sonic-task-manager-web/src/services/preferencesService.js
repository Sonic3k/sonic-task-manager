import api from './api.js';

/**
 * Preferences API Service
 * Handles user preferences and settings
 * Works with new Response structure
 */

/**
 * Get all preferences
 */
export const getAllPreferences = async () => {
  try {
    const response = await api.get('/preferences');
    
    // Extract preferences from PreferencesResponse
    if (response.data.success && response.data.preferences) {
      return response.data.preferences;
    } else {
      console.warn('Preferences response unsuccessful:', response.data.error);
      return {};
    }
  } catch (error) {
    console.error('Failed to fetch preferences:', error);
    return {};
  }
};

/**
 * Get specific preference by key
 */
export const getPreference = async (key) => {
  try {
    const response = await api.get(`/preferences/${key}`);
    
    // Extract value from PreferenceValueResponse
    if (response.data.success && response.data.value !== undefined) {
      return response.data.value;
    } else {
      console.warn(`Preference '${key}' not found:`, response.data.error);
      return null;
    }
  } catch (error) {
    console.error(`Failed to fetch preference '${key}':`, error);
    return null;
  }
};

/**
 * Set specific preference
 */
export const setPreference = async (key, value) => {
  try {
    const response = await api.put(`/preferences/${key}`, value, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
    
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Preference updated successfully'
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to update preference'
      };
    }
  } catch (error) {
    console.error(`Failed to set preference '${key}':`, error);
    return {
      success: false,
      error: 'Failed to update preference'
    };
  }
};

/**
 * Set multiple preferences at once
 */
export const setPreferences = async (preferences) => {
  try {
    const response = await api.put('/preferences', preferences);
    
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Preferences updated successfully'
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to update preferences'
      };
    }
  } catch (error) {
    console.error('Failed to set preferences:', error);
    return {
      success: false,
      error: 'Failed to update preferences'
    };
  }
};

/**
 * Delete preference
 */
export const deletePreference = async (key) => {
  try {
    const response = await api.delete(`/preferences/${key}`);
    
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Preference deleted successfully'
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Preference not found'
      };
    }
  } catch (error) {
    console.error(`Failed to delete preference '${key}':`, error);
    return {
      success: false,
      error: 'Failed to delete preference'
    };
  }
};

/**
 * Reset all preferences to defaults
 */
export const resetPreferences = async () => {
  try {
    const response = await api.post('/preferences/reset');
    
    // Extract preferences from PreferencesResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Preferences reset to defaults',
        preferences: response.data.preferences || {}
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to reset preferences'
      };
    }
  } catch (error) {
    console.error('Failed to reset preferences:', error);
    return {
      success: false,
      error: 'Failed to reset preferences'
    };
  }
};

/**
 * Initialize default preferences
 */
export const initializePreferences = async () => {
  try {
    const response = await api.post('/preferences/initialize');
    
    // Extract preferences from PreferencesResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Preferences initialized successfully',
        preferences: response.data.preferences || {}
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to initialize preferences'
      };
    }
  } catch (error) {
    console.error('Failed to initialize preferences:', error);
    return {
      success: false,
      error: 'Failed to initialize preferences'
    };
  }
};

/**
 * Helper functions for common preferences
 */

/**
 * Get theme preference
 */
export const getTheme = async () => {
  const theme = await getPreference('workspace_theme');
  return theme || 'warm';
};

/**
 * Set theme preference
 */
export const setTheme = async (theme) => {
  return await setPreference('workspace_theme', theme);
};

/**
 * Get work hours
 */
export const getWorkHours = async () => {
  const preferences = await getAllPreferences();
  return {
    start: preferences.work_hours_start || '09:00',
    end: preferences.work_hours_end || '17:00'
  };
};

/**
 * Set work hours
 */
export const setWorkHours = async (start, end) => {
  return await setPreferences({
    work_hours_start: start,
    work_hours_end: end
  });
};

/**
 * Get notification preferences
 */
export const getNotificationPreferences = async () => {
  const preferences = await getAllPreferences();
  return {
    reminderFrequency: parseInt(preferences.gentle_reminder_frequency || '3'),
    showCompletedTasks: preferences.show_completed_tasks === 'true'
  };
};

/**
 * Helper to check if preferences are initialized
 */
export const arePreferencesInitialized = async () => {
  const preferences = await getAllPreferences();
  return Object.keys(preferences).length > 0;
};