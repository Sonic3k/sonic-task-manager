import api from './api.js';

/**
 * Task API Service
 * Handles all task-related operations
 * Updated to work with new Response structure
 */

/**
 * Get all active tasks
 */
export const getAllTasks = async () => {
  try {
    const response = await api.get('/tasks');
    // Extract tasks from TaskListResponse
    return response.data.tasks || [];
  } catch (error) {
    console.error('Failed to fetch tasks:', error);
    return [];
  }
};

/**
 * Get single task by ID
 */
export const getTaskById = async (taskId) => {
  try {
    const response = await api.get(`/tasks/${taskId}`);
    // Extract task from TaskResponse
    return response.data.task || null;
  } catch (error) {
    console.error('Failed to fetch task:', error);
    return null;
  }
};

/**
 * Create new task
 */
export const createTask = async (taskData) => {
  try {
    const response = await api.post('/tasks', taskData);
    // Extract task from TaskResponse
    if (response.data.success) {
      return response.data.task;
    } else {
      throw new Error(response.data.error || 'Failed to create task');
    }
  } catch (error) {
    console.error('Failed to create task:', error);
    throw error;
  }
};

/**
 * Update existing task
 */
export const updateTask = async (taskId, taskData) => {
  try {
    const response = await api.put(`/tasks/${taskId}`, taskData);
    // Extract task from TaskResponse
    if (response.data.success) {
      return response.data.task;
    } else {
      throw new Error(response.data.error || 'Failed to update task');
    }
  } catch (error) {
    console.error('Failed to update task:', error);
    throw error;
  }
};

/**
 * Delete task
 */
export const deleteTask = async (taskId) => {
  try {
    const response = await api.delete(`/tasks/${taskId}`);
    // Check success from BaseResponse
    return response.data.success;
  } catch (error) {
    console.error('Failed to delete task:', error);
    return false;
  }
};

/**
 * Mark task as completed
 */
export const completeTask = async (taskId) => {
  try {
    const response = await api.put(`/tasks/${taskId}/complete`);
    // Check success from BaseResponse
    return response.data.success;
  } catch (error) {
    console.error('Failed to complete task:', error);
    return false;
  }
};

/**
 * Snooze task
 */
export const snoozeTask = async (taskId, days = 1) => {
  try {
    const response = await api.put(`/tasks/${taskId}/snooze?days=${days}`);
    // Check success from BaseResponse
    return response.data.success;
  } catch (error) {
    console.error('Failed to snooze task:', error);
    return false;
  }
};

/**
 * Get subtasks for a parent task
 */
export const getSubtasks = async (parentId) => {
  try {
    const response = await api.get(`/tasks/${parentId}/subtasks`);
    // Extract tasks from TaskListResponse
    return response.data.tasks || [];
  } catch (error) {
    console.error('Failed to fetch subtasks:', error);
    return [];
  }
};

/**
 * Create subtask
 */
export const createSubtask = async (parentId, subtaskData) => {
  try {
    const response = await api.post(`/tasks/${parentId}/subtasks`, subtaskData);
    // Extract task from TaskResponse
    if (response.data.success) {
      return response.data.task;
    } else {
      throw new Error(response.data.error || 'Failed to create subtask');
    }
  } catch (error) {
    console.error('Failed to create subtask:', error);
    throw error;
  }
};

/**
 * Get quick win tasks
 */
export const getQuickWins = async () => {
  try {
    const response = await api.get('/tasks/quick-wins');
    // Extract tasks from TaskListResponse
    return response.data.tasks || [];
  } catch (error) {
    console.error('Failed to fetch quick wins:', error);
    return [];
  }
};

/**
 * Complete multiple tasks at once
 */
export const completeMultipleTasks = async (taskIds) => {
  try {
    const response = await api.put('/tasks/complete-multiple', taskIds);
    // Check success from BaseResponse
    if (response.data.success) {
      return {
        success: true,
        message: response.data.message
      };
    } else {
      return {
        success: false,
        error: response.data.error
      };
    }
  } catch (error) {
    console.error('Failed to complete multiple tasks:', error);
    return {
      success: false,
      error: 'Failed to complete multiple tasks'
    };
  }
};

/**
 * Helper function to check if response has error
 */
export const isErrorResponse = (responseData) => {
  return !responseData.success && responseData.error;
};

/**
 * Helper function to get error message from response
 */
export const getErrorMessage = (responseData) => {
  return responseData.error || responseData.message || 'Unknown error occurred';
};

/**
 * Helper function to get success message from response
 */
export const getSuccessMessage = (responseData) => {
  return responseData.message || 'Operation completed successfully';
};