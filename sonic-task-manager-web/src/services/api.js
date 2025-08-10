import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api', // Proxy will redirect to http://localhost:8080/api
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Log requests in development
    if (import.meta.env.DEV) {
      console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    }
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - Handle new Response structure
api.interceptors.response.use(
  (response) => {
    // Log responses in development
    if (import.meta.env.DEV) {
      console.log(`API Response: ${response.status} ${response.config.url}`);
      
      // Log response structure in dev mode
      if (response.data) {
        const { success, message, error } = response.data;
        if (success) {
          console.log(`✅ Success: ${message || 'Operation completed'}`);
        } else if (error) {
          console.log(`❌ Error: ${error}`);
        }
      }
    }
    return response;
  },
  (error) => {
    console.error('API Response Error:', error.response?.status, error.response?.data || error.message);
    
    // Handle common errors with new Response structure
    if (error.response?.data) {
      const responseData = error.response.data;
      
      // If backend returns our BaseResponse structure even for errors
      if (responseData.success === false) {
        console.error('Backend Error:', responseData.error);
        if (responseData.message) {
          console.error('Error Details:', responseData.message);
        }
      }
    }
    
    // Handle HTTP status errors
    if (error.response?.status === 404) {
      console.warn('Resource not found');
    } else if (error.response?.status === 500) {
      console.error('Server error');
    } else if (!error.response) {
      console.error('Network error - is the backend running?');
    }
    
    return Promise.reject(error);
  }
);

/**
 * Helper function to extract data from different Response types
 * This helps maintain backward compatibility while using new Response structure
 */
export const extractResponseData = (response, dataKey = 'data') => {
  if (!response || !response.data) {
    return null;
  }

  const responseData = response.data;

  // Check if it's our new Response structure
  if (typeof responseData.success === 'boolean') {
    if (!responseData.success) {
      throw new Error(responseData.error || 'API request failed');
    }
    
    // Extract data based on response type
    if (dataKey === 'task' && responseData.task) {
      return responseData.task;
    }
    if (dataKey === 'tasks' && responseData.tasks) {
      return responseData.tasks;
    }
    if (dataKey === 'workspace' && responseData.workspace) {
      return responseData.workspace;
    }
    if (dataKey === 'preferences' && responseData.preferences) {
      return responseData.preferences;
    }
    
    // For BaseResponse (just success/error), return the whole response data
    return responseData;
  }

  // Fallback for old direct data responses
  return responseData;
};

/**
 * Helper function to check if response indicates success
 */
export const isSuccessResponse = (response) => {
  if (!response || !response.data) {
    return false;
  }
  
  // Check new Response structure
  if (typeof response.data.success === 'boolean') {
    return response.data.success;
  }
  
  // Fallback: assume success if we got a 2xx status
  return response.status >= 200 && response.status < 300;
};

/**
 * Helper function to get error message from response
 */
export const getResponseError = (error) => {
  if (error.response?.data) {
    const responseData = error.response.data;
    
    // New Response structure
    if (responseData.success === false) {
      return responseData.error || responseData.message || 'API request failed';
    }
  }
  
  // Fallback error messages
  if (error.response?.status === 404) {
    return 'Resource not found';
  }
  if (error.response?.status === 500) {
    return 'Internal server error';
  }
  if (!error.response) {
    return 'Network error - please check your connection';
  }
  
  return error.message || 'Unknown error occurred';
};

/**
 * Helper function to get success message from response
 */
export const getResponseMessage = (response) => {
  if (response?.data?.message) {
    return response.data.message;
  }
  return 'Operation completed successfully';
};

export default api;