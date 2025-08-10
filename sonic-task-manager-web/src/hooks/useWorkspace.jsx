import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import * as workspaceService from '../services/workspaceService';
import * as taskService from '../services/taskService';
import * as studioService from '../services/studioService';

const WorkspaceContext = createContext();

/**
 * Workspace Context Provider
 * Extended with Studio functionality
 * Smart state management to avoid multiple API calls
 * Optimistic updates for better UX
 */
export const WorkspaceProvider = ({ children }) => {
  // Existing workspace state
  const [workspace, setWorkspace] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [processingTasks, setProcessingTasks] = useState(new Set());

  // Studio-specific state
  const [studioTasks, setStudioTasks] = useState({ tasks: [], pagination: {} });
  const [studioLoading, setStudioLoading] = useState(false);
  const [studioError, setStudioError] = useState(null);
  const [studioFilters, setStudioFilters] = useState({});
  const [selectedTasks, setSelectedTasks] = useState(new Set());

  /**
   * Load workspace data - ONLY called once on mount
   */
  const loadWorkspace = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('🔄 Loading workspace...');
      
      const data = await workspaceService.getTodaysWorkspace();
      setWorkspace(data);
      console.log('✅ Workspace loaded:', data);
      
    } catch (err) {
      console.error('❌ Failed to load workspace:', err);
      setError('Failed to load workspace');
      // Set empty workspace to prevent crashes
      setWorkspace({
        dailyMood: 'relaxed',
        focusTask: null,
        nextUpStack: [],
        activeReminders: [],
        quickWins: [],
        showSections: { urgentTasks: false, quickWins: false, habits: false, reminders: false },
        workloadAssessment: { totalTasks: 0, urgentCount: 0, estimatedHours: 0, recommendation: 'No tasks found' }
      });
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Smart refresh - only when actually needed
   */
  const refreshWorkspace = useCallback(async () => {
    try {
      console.log('🔄 Refreshing workspace...');
      const data = await workspaceService.refreshWorkspace();
      setWorkspace(data);
      console.log('✅ Workspace refreshed');
    } catch (err) {
      console.error('❌ Failed to refresh workspace:', err);
    }
  }, []);

  /**
   * Load studio tasks with pagination and filtering
   */
  const loadStudioTasks = useCallback(async (params = {}) => {
    try {
      setStudioLoading(true);
      setStudioError(null);
      
      const mergedParams = { ...studioFilters, ...params };
      const data = await studioService.getTasksPaginated(mergedParams);
      
      setStudioTasks(data);
      console.log('✅ Studio tasks loaded:', data.tasks.length, 'tasks');
      
    } catch (error) {
      console.error('❌ Failed to load studio tasks:', error);
      setStudioError('Failed to load tasks');
      setStudioTasks({ tasks: [], pagination: {} });
    } finally {
      setStudioLoading(false);
    }
  }, [studioFilters]);

  /**
   * Optimistic task completion with smart state updates
   */
  const completeTask = useCallback(async (taskId) => {
    // Optimistic update - immediately remove from UI
    setWorkspace(prev => {
      if (!prev) return prev;
      
      const newWorkspace = { ...prev };
      
      // Remove from focus task
      if (newWorkspace.focusTask?.id === taskId) {
        newWorkspace.focusTask = null;
      }
      
      // Remove from other lists
      newWorkspace.nextUpStack = newWorkspace.nextUpStack.filter(t => t.id !== taskId);
      newWorkspace.quickWins = newWorkspace.quickWins.filter(t => t.id !== taskId);
      newWorkspace.activeReminders = newWorkspace.activeReminders.filter(t => t.id !== taskId);
      
      return newWorkspace;
    });

    // Add to processing
    setProcessingTasks(prev => new Set([...prev, taskId]));

    try {
      const success = await taskService.completeTask(taskId);
      if (success) {
        console.log(`✅ Task ${taskId} completed`);
        // Refresh to get updated focus task calculation
        await refreshWorkspace();
        // Also refresh studio tasks if loaded
        if (studioTasks.tasks.length > 0) {
          loadStudioTasks();
        }
      } else {
        // Revert optimistic update on failure
        await refreshWorkspace();
      }
    } catch (error) {
      console.error('❌ Failed to complete task:', error);
      // Revert optimistic update on error
      await refreshWorkspace();
    } finally {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        newSet.delete(taskId);
        return newSet;
      });
    }
  }, [refreshWorkspace, studioTasks.tasks.length, loadStudioTasks]);

  /**
   * Optimistic task snooze
   */
  const snoozeTask = useCallback(async (taskId, days = 1) => {
    // Optimistic update - immediately remove from UI
    setWorkspace(prev => {
      if (!prev) return prev;
      
      const newWorkspace = { ...prev };
      
      // Remove from focus task
      if (newWorkspace.focusTask?.id === taskId) {
        newWorkspace.focusTask = null;
      }
      
      // Remove from other lists
      newWorkspace.nextUpStack = newWorkspace.nextUpStack.filter(t => t.id !== taskId);
      newWorkspace.quickWins = newWorkspace.quickWins.filter(t => t.id !== taskId);
      newWorkspace.activeReminders = newWorkspace.activeReminders.filter(t => t.id !== taskId);
      
      return newWorkspace;
    });

    setProcessingTasks(prev => new Set([...prev, taskId]));

    try {
      const success = await taskService.snoozeTask(taskId, days);
      if (success) {
        console.log(`✅ Task ${taskId} snoozed for ${days} days`);
        await refreshWorkspace();
        if (studioTasks.tasks.length > 0) {
          loadStudioTasks();
        }
      } else {
        await refreshWorkspace();
      }
    } catch (error) {
      console.error('❌ Failed to snooze task:', error);
      await refreshWorkspace();
    } finally {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        newSet.delete(taskId);
        return newSet;
      });
    }
  }, [refreshWorkspace, studioTasks.tasks.length, loadStudioTasks]);

  /**
   * Add new task with optimistic update
   */
  const addTask = useCallback(async (taskData) => {
    try {
      const newTask = await taskService.createTask(taskData);
      console.log('✅ Task created:', newTask.title);
      
      // Smart refresh only for new tasks that might affect workspace
      if (taskData.priority === 'high' || taskData.deadline) {
        await refreshWorkspace();
      }
      
      // Refresh studio tasks if loaded
      if (studioTasks.tasks.length > 0) {
        loadStudioTasks();
      }
      
      return newTask;
    } catch (error) {
      console.error('❌ Failed to create task:', error);
      throw error;
    }
  }, [refreshWorkspace, studioTasks.tasks.length, loadStudioTasks]);

  /**
   * Snooze reminder
   */
  const snoozeReminder = useCallback(async (taskId, days = 7) => {
    // Optimistic update
    setWorkspace(prev => {
      if (!prev) return prev;
      return {
        ...prev,
        activeReminders: prev.activeReminders.filter(r => r.id !== taskId)
      };
    });

    try {
      await workspaceService.snoozeReminder(taskId, days);
      console.log(`✅ Reminder ${taskId} snoozed`);
    } catch (error) {
      console.error('❌ Failed to snooze reminder:', error);
      await refreshWorkspace();
    }
  }, [refreshWorkspace]);

  // === Studio Methods ===

  /**
   * Update studio filters and reload
   */
  const updateStudioFilters = useCallback(async (newFilters) => {
    setStudioFilters(newFilters);
    await loadStudioTasks({ ...newFilters, page: 0 }); // Reset to first page
  }, [loadStudioTasks]);

  /**
   * Bulk operations for studio
   */
  const bulkCompleteStudioTasks = useCallback(async (taskIds) => {
    try {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        taskIds.forEach(id => newSet.add(id));
        return newSet;
      });

      const result = await studioService.bulkCompleteTasks(taskIds);
      console.log('✅ Bulk complete result:', result);
      
      // Refresh both workspace and studio
      await Promise.all([refreshWorkspace(), loadStudioTasks()]);
      
      // Clear selections
      setSelectedTasks(new Set());
      
      return result;
    } catch (error) {
      console.error('❌ Bulk complete failed:', error);
      throw error;
    } finally {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        taskIds.forEach(id => newSet.delete(id));
        return newSet;
      });
    }
  }, [refreshWorkspace, loadStudioTasks]);

  const bulkSnoozeStudioTasks = useCallback(async (taskIds, days = 1) => {
    try {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        taskIds.forEach(id => newSet.add(id));
        return newSet;
      });

      const result = await studioService.bulkSnoozeTasks(taskIds, days);
      console.log('✅ Bulk snooze result:', result);
      
      await Promise.all([refreshWorkspace(), loadStudioTasks()]);
      setSelectedTasks(new Set());
      
      return result;
    } catch (error) {
      console.error('❌ Bulk snooze failed:', error);
      throw error;
    } finally {
      setProcessingTasks(prev => {
        const newSet = new Set(prev);
        taskIds.forEach(id => newSet.delete(id));
        return newSet;
      });
    }
  }, [refreshWorkspace, loadStudioTasks]);

  // Load workspace on mount
  useEffect(() => {
    loadWorkspace();
  }, [loadWorkspace]);

  const value = {
    // Existing workspace state
    workspace,
    loading,
    error,
    processingTasks,
    
    // Existing workspace actions
    completeTask,
    snoozeTask,
    addTask,
    snoozeReminder,
    refreshWorkspace,
    
    // Studio state
    studioTasks,
    studioLoading,
    studioError,
    studioFilters,
    selectedTasks,
    
    // Studio actions
    loadStudioTasks,
    updateStudioFilters,
    bulkCompleteStudioTasks,
    bulkSnoozeStudioTasks,
    setSelectedTasks,
    
    // Utils
    isTaskProcessing: (taskId) => processingTasks.has(taskId)
  };

  return (
    <WorkspaceContext.Provider value={value}>
      {children}
    </WorkspaceContext.Provider>
  );
};

/**
 * Hook to use workspace context
 */
export const useWorkspace = () => {
  const context = useContext(WorkspaceContext);
  if (!context) {
    throw new Error('useWorkspace must be used within WorkspaceProvider');
  }
  return context;
};