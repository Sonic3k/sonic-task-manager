import api from './api.js';

/**
 * Studio API Service
 * Handles comprehensive task management for Studio interface
 * Follows existing service patterns
 */

/**
 * Get paginated tasks with advanced filtering
 */
export const getTasksPaginated = async (params = {}) => {
  try {
    const {
      page = 0,
      size = 20,
      sortBy = 'createdAt',
      sortDir = 'desc',
      ...filters
    } = params;

    // Build query parameters
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir
    });

    // Add filter parameters
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        queryParams.append(key, value.toString());
      }
    });

    const response = await api.get(`/studio/tasks?${queryParams}`);
    
    // Extract data from PaginatedTaskResponse
    if (response.data.success) {
      return {
        tasks: response.data.tasks || [],
        pagination: response.data.pagination || {}
      };
    } else {
      throw new Error(response.data.error || 'Failed to fetch tasks');
    }
  } catch (error) {
    console.error('Failed to fetch paginated tasks:', error);
    throw error;
  }
};

/**
 * Advanced search with complex filters
 */
export const searchTasks = async (filters, pagination = {}) => {
  try {
    const {
      page = 0,
      size = 20,
      sortBy = 'createdAt',
      sortDir = 'desc'
    } = pagination;

    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir
    });

    const response = await api.post(`/studio/tasks/search?${queryParams}`, filters);
    
    if (response.data.success) {
      return {
        tasks: response.data.tasks || [],
        pagination: response.data.pagination || {}
      };
    } else {
      throw new Error(response.data.error || 'Failed to search tasks');
    }
  } catch (error) {
    console.error('Failed to search tasks:', error);
    throw error;
  }
};

/**
 * Bulk update tasks
 */
export const bulkUpdateTasks = async (taskIds, operation, options = {}) => {
  try {
    const request = {
      taskIds,
      operation,
      ...options
    };

    const response = await api.post('/studio/tasks/bulk-update', request);
    
    if (response.data.success) {
      return response.data.result;
    } else {
      throw new Error(response.data.error || 'Failed to bulk update tasks');
    }
  } catch (error) {
    console.error('Failed to bulk update tasks:', error);
    throw error;
  }
};

/**
 * Bulk complete tasks
 */
export const bulkCompleteTasks = async (taskIds) => {
  return bulkUpdateTasks(taskIds, 'complete');
};

/**
 * Bulk snooze tasks
 */
export const bulkSnoozeTasks = async (taskIds, days = 1) => {
  return bulkUpdateTasks(taskIds, 'snooze', { snoozeDays: days });
};

/**
 * Bulk update priority
 */
export const bulkUpdatePriority = async (taskIds, priority) => {
  return bulkUpdateTasks(taskIds, 'update_priority', { newPriority: priority });
};

/**
 * Bulk update status
 */
export const bulkUpdateStatus = async (taskIds, status) => {
  return bulkUpdateTasks(taskIds, 'update_status', { newStatus: status });
};

/**
 * Bulk update complexity
 */
export const bulkUpdateComplexity = async (taskIds, complexity) => {
  return bulkUpdateTasks(taskIds, 'update_complexity', { newComplexity: complexity });
};

/**
 * Bulk delete tasks
 */
export const bulkDeleteTasks = async (taskIds) => {
  return bulkUpdateTasks(taskIds, 'delete');
};

/**
 * Get task statistics
 */
export const getTaskStatistics = async () => {
  try {
    const response = await api.get('/studio/stats');
    
    if (response.data.success) {
      return response.data.stats || {};
    } else {
      throw new Error(response.data.error || 'Failed to fetch statistics');
    }
  } catch (error) {
    console.error('Failed to fetch task statistics:', error);
    throw error;
  }
};

/**
 * Studio health check
 */
export const checkStudioHealth = async () => {
  try {
    const response = await api.get('/studio/health');
    return response.data.success;
  } catch (error) {
    console.error('Studio health check failed:', error);
    return false;
  }
};

/**
 * Helper function to build filter objects
 */
export const createFilter = ({
  status,
  priority,
  complexity,
  type,
  deadlineFrom,
  deadlineTo,
  search,
  hasSubtasks,
  isOverdue,
  isUrgent
} = {}) => {
  const filter = {};
  
  if (status) filter.status = status;
  if (priority) filter.priority = priority;
  if (complexity) filter.complexity = complexity;
  if (type) filter.type = type;
  if (deadlineFrom) filter.deadlineFrom = deadlineFrom;
  if (deadlineTo) filter.deadlineTo = deadlineTo;
  if (search) filter.searchQuery = search;
  if (hasSubtasks !== undefined) filter.hasSubtasks = hasSubtasks;
  if (isOverdue !== undefined) filter.isOverdue = isOverdue;
  if (isUrgent !== undefined) filter.isUrgent = isUrgent;
  
  return filter;
};

/**
 * Helper function to create pagination object
 */
export const createPagination = ({
  page = 0,
  size = 20,
  sortBy = 'createdAt',
  sortDir = 'desc'
} = {}) => {
  return { page, size, sortBy, sortDir };
};