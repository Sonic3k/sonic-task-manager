import React, { useState, useEffect } from 'react';
import { useWorkspace } from '../../hooks/useWorkspace';
import TaskCard from '../TaskCard';
import FilterPanel from './shared/FilterPanel';
import BulkActions from './shared/BulkActions';
import Pagination from './shared/Pagination';
import LoadingSpinner from '../LoadingSpinner';

/**
 * Tasks Management - Core Studio Interface
 * Comprehensive task management with filtering, pagination, and bulk operations
 */
const TasksManagement = () => {
  const {
    studioTasks,
    studioLoading,
    studioError,
    studioFilters,
    selectedTasks,
    setSelectedTasks,
    loadStudioTasks,
    updateStudioFilters,
    bulkCompleteStudioTasks,
    bulkSnoozeStudioTasks
  } = useWorkspace();

  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [sortBy, setSortBy] = useState('createdAt');
  const [sortDir, setSortDir] = useState('desc');
  const [showFilters, setShowFilters] = useState(false);

  // Load initial data
  useEffect(() => {
    loadStudioTasks({
      page: currentPage,
      size: pageSize,
      sortBy,
      sortDir
    });
  }, [loadStudioTasks, currentPage, pageSize, sortBy, sortDir]);

  // Handle filter changes
  const handleFilterChange = async (newFilters) => {
    setCurrentPage(0); // Reset to first page
    await updateStudioFilters(newFilters);
  };

  // Handle page changes
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  // Handle sorting
  const handleSort = (field) => {
    const newDir = (sortBy === field && sortDir === 'asc') ? 'desc' : 'asc';
    setSortBy(field);
    setSortDir(newDir);
  };

  // Handle task selection
  const handleTaskSelect = (taskId, selected) => {
    setSelectedTasks(prev => {
      const newSet = new Set(prev);
      if (selected) {
        newSet.add(taskId);
      } else {
        newSet.delete(taskId);
      }
      return newSet;
    });
  };

  // Handle select all
  const handleSelectAll = (selectAll) => {
    if (selectAll) {
      const allTaskIds = studioTasks.tasks.map(task => task.id);
      setSelectedTasks(new Set(allTaskIds));
    } else {
      setSelectedTasks(new Set());
    }
  };

  // Handle bulk actions
  const handleBulkComplete = async () => {
    try {
      await bulkCompleteStudioTasks(Array.from(selectedTasks));
    } catch (error) {
      console.error('Bulk complete failed:', error);
    }
  };

  const handleBulkSnooze = async (days) => {
    try {
      await bulkSnoozeStudioTasks(Array.from(selectedTasks), days);
    } catch (error) {
      console.error('Bulk snooze failed:', error);
    }
  };

  const { tasks = [], pagination = {} } = studioTasks;
  const hasSelectedTasks = selectedTasks.size > 0;
  const allSelected = tasks.length > 0 && selectedTasks.size === tasks.length;

  if (studioError) {
    return (
      <div className="studio-page">
        <div className="studio-error">
          <h2>Error Loading Tasks</h2>
          <p>{studioError}</p>
          <button onClick={() => loadStudioTasks()} className="btn btn--primary">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="studio-page">
      {/* Header */}
      <div className="studio-page-header">
        <div className="page-title-section">
          <h1 className="page-title">All Tasks</h1>
          <p className="page-subtitle">
            Comprehensive task management with filtering and bulk operations
          </p>
        </div>

        <div className="page-actions">
          <button 
            className={`btn btn--secondary ${showFilters ? 'btn--active' : ''}`}
            onClick={() => setShowFilters(!showFilters)}
          >
            🔍 Filters
          </button>
          
          <div className="task-count">
            {pagination.totalElements || 0} tasks
          </div>
        </div>
      </div>

      {/* Filters */}
      {showFilters && (
        <FilterPanel
          filters={studioFilters}
          onFilterChange={handleFilterChange}
          onClose={() => setShowFilters(false)}
        />
      )}

      {/* Bulk Actions */}
      {hasSelectedTasks && (
        <BulkActions
          selectedCount={selectedTasks.size}
          onComplete={handleBulkComplete}
          onSnooze={handleBulkSnooze}
          onClearSelection={() => setSelectedTasks(new Set())}
        />
      )}

      {/* Table Header */}
      <div className="studio-table-header">
        <div className="table-controls">
          <label className="select-all-control">
            <input
              type="checkbox"
              checked={allSelected}
              onChange={(e) => handleSelectAll(e.target.checked)}
              disabled={tasks.length === 0}
            />
            <span>Select All ({tasks.length})</span>
          </label>
        </div>

        <div className="sort-controls">
          <button 
            className={`sort-btn ${sortBy === 'title' ? 'sort-btn--active' : ''}`}
            onClick={() => handleSort('title')}
          >
            Title {sortBy === 'title' && (sortDir === 'asc' ? '↑' : '↓')}
          </button>
          <button 
            className={`sort-btn ${sortBy === 'priority' ? 'sort-btn--active' : ''}`}
            onClick={() => handleSort('priority')}
          >
            Priority {sortBy === 'priority' && (sortDir === 'asc' ? '↑' : '↓')}
          </button>
          <button 
            className={`sort-btn ${sortBy === 'deadline' ? 'sort-btn--active' : ''}`}
            onClick={() => handleSort('deadline')}
          >
            Deadline {sortBy === 'deadline' && (sortDir === 'asc' ? '↑' : '↓')}
          </button>
          <button 
            className={`sort-btn ${sortBy === 'createdAt' ? 'sort-btn--active' : ''}`}
            onClick={() => handleSort('createdAt')}
          >
            Created {sortBy === 'createdAt' && (sortDir === 'asc' ? '↑' : '↓')}
          </button>
        </div>
      </div>

      {/* Tasks List */}
      <div className="studio-tasks-container">
        {studioLoading ? (
          <div className="studio-loading">
            <LoadingSpinner />
            <p>Loading tasks...</p>
          </div>
        ) : tasks.length === 0 ? (
          <div className="studio-empty">
            <h3>No tasks found</h3>
            <p>Try adjusting your filters or create a new task.</p>
          </div>
        ) : (
          <div className="studio-tasks-list">
            {tasks.map(task => (
              <div key={task.id} className="studio-task-row">
                <div className="task-select">
                  <input
                    type="checkbox"
                    checked={selectedTasks.has(task.id)}
                    onChange={(e) => handleTaskSelect(task.id, e.target.checked)}
                  />
                </div>
                <div className="task-content">
                  <TaskCard 
                    task={task} 
                    variant="studio"
                    showBulkSelect={false}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Pagination */}
      {pagination.totalPages > 1 && (
        <Pagination
          currentPage={pagination.page || 0}
          totalPages={pagination.totalPages || 0}
          totalElements={pagination.totalElements || 0}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          onPageSizeChange={setPageSize}
        />
      )}
    </div>
  );
};

export default TasksManagement;