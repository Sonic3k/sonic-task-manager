import React, { useState } from 'react';

/**
 * Filter Panel Component
 * Advanced filtering interface for Studio
 */
const FilterPanel = ({ filters, onFilterChange, onClose }) => {
  const [localFilters, setLocalFilters] = useState({
    status: filters.status || '',
    priority: filters.priority || '',
    complexity: filters.complexity || '',
    type: filters.type || '',
    deadlineFrom: filters.deadlineFrom || '',
    deadlineTo: filters.deadlineTo || '',
    search: filters.search || '',
    hasSubtasks: filters.hasSubtasks,
    isOverdue: filters.isOverdue,
    isUrgent: filters.isUrgent
  });

  const handleInputChange = (field, value) => {
    setLocalFilters(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleApplyFilters = () => {
    // Clean up empty values
    const cleanFilters = Object.entries(localFilters).reduce((acc, [key, value]) => {
      if (value !== '' && value !== null && value !== undefined) {
        acc[key] = value;
      }
      return acc;
    }, {});

    onFilterChange(cleanFilters);
  };

  const handleClearFilters = () => {
    setLocalFilters({
      status: '',
      priority: '',
      complexity: '',
      type: '',
      deadlineFrom: '',
      deadlineTo: '',
      search: '',
      hasSubtasks: undefined,
      isOverdue: undefined,
      isUrgent: undefined
    });
    onFilterChange({});
  };

  const hasActiveFilters = Object.values(localFilters).some(value => 
    value !== '' && value !== null && value !== undefined
  );

  return (
    <div className="filter-panel">
      <div className="filter-panel-header">
        <h3>Filter Tasks</h3>
        <button className="filter-close-btn" onClick={onClose}>
          ✕
        </button>
      </div>

      <div className="filter-panel-content">
        {/* Search */}
        <div className="filter-group">
          <label>Search</label>
          <input
            type="text"
            value={localFilters.search}
            onChange={(e) => handleInputChange('search', e.target.value)}
            placeholder="Search title or description..."
          />
        </div>

        {/* Quick Filters Row */}
        <div className="filter-row">
          <div className="filter-group">
            <label>Status</label>
            <select
              value={localFilters.status}
              onChange={(e) => handleInputChange('status', e.target.value)}
            >
              <option value="">All Status</option>
              <option value="todo">To Do</option>
              <option value="doing">In Progress</option>
              <option value="done">Completed</option>
              <option value="snoozed">Snoozed</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Priority</label>
            <select
              value={localFilters.priority}
              onChange={(e) => handleInputChange('priority', e.target.value)}
            >
              <option value="">All Priorities</option>
              <option value="high">High</option>
              <option value="medium">Medium</option>
              <option value="low">Low</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Complexity</label>
            <select
              value={localFilters.complexity}
              onChange={(e) => handleInputChange('complexity', e.target.value)}
            >
              <option value="">All Complexities</option>
              <option value="easy">Easy</option>
              <option value="medium">Medium</option>
              <option value="hard">Hard</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Type</label>
            <select
              value={localFilters.type}
              onChange={(e) => handleInputChange('type', e.target.value)}
            >
              <option value="">All Types</option>
              <option value="deadline">Deadline</option>
              <option value="habit">Habit</option>
              <option value="reminder">Reminder</option>
              <option value="event">Event</option>
            </select>
          </div>
        </div>

        {/* Date Range */}
        <div className="filter-row">
          <div className="filter-group">
            <label>Deadline From</label>
            <input
              type="date"
              value={localFilters.deadlineFrom}
              onChange={(e) => handleInputChange('deadlineFrom', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Deadline To</label>
            <input
              type="date"
              value={localFilters.deadlineTo}
              onChange={(e) => handleInputChange('deadlineTo', e.target.value)}
            />
          </div>
        </div>

        {/* Boolean Filters */}
        <div className="filter-checkboxes">
          <label className="checkbox-filter">
            <input
              type="checkbox"
              checked={localFilters.hasSubtasks === true}
              onChange={(e) => handleInputChange('hasSubtasks', e.target.checked ? true : undefined)}
            />
            <span>Has Subtasks</span>
          </label>

          <label className="checkbox-filter">
            <input
              type="checkbox"
              checked={localFilters.isOverdue === true}
              onChange={(e) => handleInputChange('isOverdue', e.target.checked ? true : undefined)}
            />
            <span>Overdue</span>
          </label>

          <label className="checkbox-filter">
            <input
              type="checkbox"
              checked={localFilters.isUrgent === true}
              onChange={(e) => handleInputChange('isUrgent', e.target.checked ? true : undefined)}
            />
            <span>Urgent</span>
          </label>
        </div>
      </div>

      {/* Actions */}
      <div className="filter-panel-actions">
        <button 
          className="btn btn--secondary"
          onClick={handleClearFilters}
          disabled={!hasActiveFilters}
        >
          Clear All
        </button>
        <button 
          className="btn btn--primary"
          onClick={handleApplyFilters}
        >
          Apply Filters
        </button>
      </div>
    </div>
  );
};

export default FilterPanel;