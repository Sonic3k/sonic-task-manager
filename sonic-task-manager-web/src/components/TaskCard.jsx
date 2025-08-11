import React, { useState } from 'react';
import { useWorkspace } from '../hooks/useWorkspace.jsx';

/**
 * Flexible TaskCard component
 * Variants: focus, quick, next, reminder, studio
 * Easy to customize and extend
 */
const TaskCard = ({ task, variant = 'default', showDetails = false }) => {
  const { completeTask, snoozeTask, snoozeReminder, isTaskProcessing } = useWorkspace();
  const [expanded, setExpanded] = useState(showDetails);
  const isProcessing = isTaskProcessing(task.id);

  // Get card styling based on variant
  const getCardClass = () => {
    const baseClass = 'task-card';
    const variantClass = `task-card--${variant}`;
    const processingClass = isProcessing ? 'task-card--processing' : '';
    const urgencyClass = task.isOverdue ? 'task-card--overdue' : 
                        task.isUrgent ? 'task-card--urgent' : '';
    
    const statusClass = variant === 'studio' ? `task-card--status-${task.status}` : '';
    
    return `${baseClass} ${variantClass} ${processingClass} ${urgencyClass} ${statusClass}`.trim();
  };

  // Get priority indicator
  const getPriorityIcon = () => {
    switch (task.priority) {
      case 'high': return '🔴';
      case 'medium': return '🟡'; 
      case 'low': return '🔵';
      default: return '⚪';
    }
  };

  // Get complexity label
  const getComplexityLabel = () => {
    switch (task.complexity) {
      case 'easy': return 'Quick';
      case 'medium': return 'Medium';
      case 'hard': return 'Complex';
      default: return '';
    }
  };

  // Get deadline info
  const getDeadlineInfo = () => {
    if (!task.deadline) return null;
    
    if (task.isOverdue) {
      return `Overdue ${Math.abs(task.daysUntilDeadline)} days`;
    } else if (task.daysUntilDeadline === 0) {
      return 'Due today';
    } else if (task.daysUntilDeadline === 1) {
      return 'Due tomorrow';
    } else {
      return `${task.daysUntilDeadline} days left`;
    }
  };

  // Handle complete action
  const handleComplete = () => {
    if (!isProcessing) {
      completeTask(task.id);
    }
  };

  // Handle snooze action
  const handleSnooze = (days = 1) => {
    if (!isProcessing) {
      if (task.type === 'reminder') {
        snoozeReminder(task.id, days);
      } else {
        snoozeTask(task.id, days);
      }
    }
  };

  // Studio variant - compact table-like display
  if (variant === 'studio') {
    return (
      <div className={getCardClass()}>
        <div className="studio-task-header">
          <div className="studio-task-main">
            <div className="studio-task-title-row">
  <span className="priority-icon">{getPriorityIcon()}</span>
  <h4 className="studio-task-title">{task.title}</h4>
  {isProcessing && <span className="processing-indicator">⏳</span>}
  {variant === 'studio' && task.status === 'done' && <span className="status-badge">✓</span>}
  {variant === 'studio' && task.status === 'snoozed' && <span className="status-badge">💤</span>}
</div>
            
            {task.description && (
              <p className="studio-task-description">{task.description}</p>
            )}
          </div>

          <div className="studio-task-meta">
            <div className="studio-badges">
              {getComplexityLabel() && (
                <span className="complexity-badge">{getComplexityLabel()}</span>
              )}
              {task.type !== 'deadline' && (
                <span className="type-badge">{task.type}</span>
              )}
              {getDeadlineInfo() && (
                <span className="deadline-badge">{getDeadlineInfo()}</span>
              )}
            </div>
            
            <div className="studio-task-stats">
              {task.progressTotal > 1 && (
                <span className="progress-stat">
                  {task.progressCurrent}/{task.progressTotal}
                </span>
              )}
              <span className="created-date">
                {new Date(task.createdAt).toLocaleDateString()}
              </span>
            </div>
          </div>
        </div>

        {/* Progress bar for studio variant */}
        {task.progressTotal > 1 && (
          <div className="studio-progress">
            <div className="progress-bar">
              <div 
                className="progress-fill"
                style={{ width: `${task.progressPercentage}%` }}
              />
            </div>
          </div>
        )}

        {/* Compact actions for studio */}
        <div className="studio-task-actions">
          <button 
            className="studio-action-btn studio-action-btn--complete"
            onClick={handleComplete}
            disabled={isProcessing}
            title="Complete task"
          >
            {isProcessing ? '⏳' : '✓'}
          </button>
          <button 
            className="studio-action-btn studio-action-btn--snooze"
            onClick={() => handleSnooze(1)}
            disabled={isProcessing}
            title="Snooze for 1 day"
          >
            {isProcessing ? '⏳' : '💤'}
          </button>
        </div>
      </div>
    );
  }

  // Render action buttons based on variant and task type
  const renderActions = () => {
    if (variant === 'reminder') {
      return (
        <div className="task-actions">
          <button 
            className="action-btn action-btn--snooze"
            onClick={() => handleSnooze(3)}
            disabled={isProcessing}
          >
            {isProcessing ? '⏳' : '👍'} 3d
          </button>
          <button 
            className="action-btn action-btn--snooze"
            onClick={() => handleSnooze(7)}
            disabled={isProcessing}
          >
            {isProcessing ? '⏳' : '📅'} 1w
          </button>
          <button 
            className="action-btn action-btn--snooze"
            onClick={() => handleSnooze(30)}
            disabled={isProcessing}
          >
            {isProcessing ? '⏳' : '📅'} 1m
          </button>
        </div>
      );
    }

    return (
      <div className="task-actions">
        <button 
          className="action-btn action-btn--complete"
          onClick={handleComplete}
          disabled={isProcessing}
        >
          {isProcessing ? '⏳' : '✓'} Done
        </button>
        
        {variant !== 'focus' && (
          <button 
            className="action-btn action-btn--snooze"
            onClick={() => handleSnooze(1)}
            disabled={isProcessing}
          >
            {isProcessing ? '⏳' : '💤'} Tomorrow
          </button>
        )}
        
        {variant === 'focus' && (
          <button 
            className="action-btn action-btn--snooze"
            onClick={() => handleSnooze(1)}
            disabled={isProcessing}
          >
            {isProcessing ? '⏳' : '⏭️'} Skip Today
          </button>
        )}
      </div>
    );
  };

  // Render focus context for focus variant
  const renderFocusContext = () => {
    if (variant !== 'focus' || !task.focusContext) return null;
    
    return (
      <div className="focus-context">
        💡 {task.focusContext}
      </div>
    );
  };

  // Render progress for tasks with subtasks
  const renderProgress = () => {
    if (task.progressTotal <= 1) return null;
    
    return (
      <div className="task-progress">
        <div className="progress-bar">
          <div 
            className="progress-fill"
            style={{ width: `${task.progressPercentage}%` }}
          />
        </div>
        <span className="progress-text">
          {task.progressCurrent}/{task.progressTotal} completed
        </span>
      </div>
    );
  };

  // Render subtasks if expanded
  const renderSubtasks = () => {
    if (!expanded || !task.subtasks || task.subtasks.length === 0) return null;
    
    return (
      <div className="subtasks">
        <h4>Steps:</h4>
        {task.subtasks.map(subtask => (
          <div key={subtask.id} className={`subtask ${subtask.status === 'done' ? 'completed' : ''}`}>
            <span className="subtask-status">
              {subtask.status === 'done' ? '✅' : '⏳'}
            </span>
            <span className="subtask-title">{subtask.title}</span>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className={getCardClass()}>
      {/* Card Header */}
      <div className="task-header">
        <div className="task-title-row">
          <span className="priority-icon">{getPriorityIcon()}</span>
          <h3 className="task-title">{task.title}</h3>
          {isProcessing && <span className="processing-indicator">⏳</span>}
        </div>
        
        {/* Task Meta Info */}
        <div className="task-meta">
          {getComplexityLabel() && (
            <span className="complexity-badge">{getComplexityLabel()}</span>
          )}
          {getDeadlineInfo() && (
            <span className="deadline-badge">{getDeadlineInfo()}</span>
          )}
          {task.type && task.type !== 'deadline' && (
            <span className="type-badge">{task.type}</span>
          )}
        </div>
      </div>

      {/* Task Description */}
      {task.description && (
        <div className="task-description">
          {task.description}
        </div>
      )}

      {/* Focus Context */}
      {renderFocusContext()}

      {/* Progress */}
      {renderProgress()}

      {/* Subtasks Toggle */}
      {task.subtasks && task.subtasks.length > 0 && (
        <button 
          className="expand-btn"
          onClick={() => setExpanded(!expanded)}
        >
          {expanded ? '▲' : '▼'} {task.subtasks.length} steps
        </button>
      )}

      {/* Subtasks */}
      {renderSubtasks()}

      {/* Actions */}
      {renderActions()}
    </div>
  );
};

export default TaskCard;