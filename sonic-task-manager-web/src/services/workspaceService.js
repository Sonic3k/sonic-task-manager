import api from './api.js';

/**
 * Workspace API Service
 * Main service for fetching workspace data from backend
 * Updated to work with new Response structure
 */

/**
 * Get today's complete workspace data
 * This is the main API call that gets all data frontend needs
 */
export const getTodaysWorkspace = async () => {
  try {
    const response = await api.get('/workspace');
    
    // Extract workspace from WorkspaceResponse
    if (response.data.success && response.data.workspace) {
      return response.data.workspace;
    } else {
      console.warn('Workspace response unsuccessful:', response.data.error);
      return getEmptyWorkspace();
    }
  } catch (error) {
    console.error('Failed to fetch workspace:', error);
    // Return empty workspace on error to prevent app crash
    return getEmptyWorkspace();
  }
};

/**
 * Refresh workspace data
 * Call after task updates to get fresh calculations
 */
export const refreshWorkspace = async () => {
  try {
    const response = await api.post('/workspace/refresh');
    
    // Extract workspace from WorkspaceResponse
    if (response.data.success && response.data.workspace) {
      return response.data.workspace;
    } else {
      console.warn('Workspace refresh unsuccessful:', response.data.error);
      // Fallback to regular workspace fetch
      return await getTodaysWorkspace();
    }
  } catch (error) {
    console.error('Failed to refresh workspace:', error);
    // Fallback to regular workspace fetch
    return await getTodaysWorkspace();
  }
};

/**
 * Snooze a reminder
 */
export const snoozeReminder = async (taskId, days = 7) => {
  try {
    const response = await api.put(`/workspace/reminders/${taskId}/snooze?days=${days}`);
    
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || `Reminder snoozed for ${days} day(s)`
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to snooze reminder'
      };
    }
  } catch (error) {
    console.error('Failed to snooze reminder:', error);
    return {
      success: false,
      error: 'Failed to snooze reminder'
    };
  }
};

/**
 * Acknowledge a reminder (mark as seen)
 */
export const acknowledgeReminder = async (taskId) => {
  try {
    const response = await api.put(`/workspace/reminders/${taskId}/acknowledge`);
    
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message || 'Reminder acknowledged'
      };
    } else {
      return {
        success: false,
        error: response.data.error || 'Failed to acknowledge reminder'
      };
    }
  } catch (error) {
    console.error('Failed to acknowledge reminder:', error);
    return {
      success: false,
      error: 'Failed to acknowledge reminder'
    };
  }
};

/**
 * Get empty workspace for fallback cases
 */
const getEmptyWorkspace = () => {
  return {
    dailyMood: 'relaxed',
    focusTask: null,
    nextUpStack: [],
    activeReminders: [],
    quickWins: [],
    showSections: {
      urgentTasks: false,
      quickWins: false,
      habits: false,
      reminders: false
    },
    workloadAssessment: {
      totalTasks: 0,
      urgentCount: 0,
      estimatedHours: 0,
      recommendation: 'Unable to load tasks - please check connection'
    }
  };
};

/**
 * Helper function to check workspace response validity
 */
export const isValidWorkspace = (workspace) => {
  return workspace && 
         typeof workspace.dailyMood === 'string' &&
         Array.isArray(workspace.nextUpStack) &&
         Array.isArray(workspace.activeReminders) &&
         Array.isArray(workspace.quickWins) &&
         workspace.showSections &&
         workspace.workloadAssessment;
};

/**
 * Helper function to get workspace error info
 */
export const getWorkspaceError = (responseData) => {
  if (!responseData.success) {
    return {
      error: responseData.error || 'Unknown workspace error',
      message: responseData.message || 'Failed to load workspace'
    };
  }
  return null;
};