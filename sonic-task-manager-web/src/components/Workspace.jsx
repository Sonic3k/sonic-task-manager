import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useWorkspace } from '../hooks/useWorkspace.jsx';
import Header from './Header';
import TaskCard from './TaskCard';
import AddTaskForm from './AddTaskForm';
import LoadingSpinner from './LoadingSpinner';

/**
 * Main Workspace Component
 * Single column layout following project philosophy:
 * - Single Focus Approach: One main task prominent
 * - Anti-Stress Design: Clean, not overwhelming
 * - Progressive Disclosure: Expand details on demand
 */
const Workspace = () => {
  const { workspace, loading, error } = useWorkspace();
  const [showAddForm, setShowAddForm] = useState(false);

  if (loading) {
    return (
      <div className="workspace-loading">
        <LoadingSpinner />
        <p>Loading your workspace...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="workspace-error">
        <h2>Something went wrong</h2>
        <p>{error}</p>
        <button onClick={() => window.location.reload()}>
          Try Again
        </button>
      </div>
    );
  }

  if (!workspace) {
    return <div className="workspace-empty">No workspace data</div>;
  }

  return (
    <div className="workspace">
      {/* Header with mood and stats */}
      <Header 
        mood={workspace.dailyMood}
        workloadAssessment={workspace.workloadAssessment}
      />

      {/* Studio Access */}
      <div className="studio-access">
        <Link to="/studio" className="studio-access-btn">
          <span className="studio-access-icon">🎨</span>
          <div className="studio-access-content">
            <span className="studio-access-title">Task Studio</span>
            <span className="studio-access-desc">Advanced management & analytics</span>
          </div>
          <span className="studio-access-arrow">→</span>
        </Link>
      </div>

      {/* Add Task Button */}
      <div className="add-task-section">
        {!showAddForm ? (
          <button 
            className="add-task-btn"
            onClick={() => setShowAddForm(true)}
          >
            + Add New Task
          </button>
        ) : (
          <AddTaskForm 
            onCancel={() => setShowAddForm(false)}
            onSuccess={() => setShowAddForm(false)}
          />
        )}
      </div>

      {/* Today's Main Task - Single Focus */}
      {workspace.focusTask && (
        <section className="focus-section">
          <h2 className="section-title">🎯 Today's Focus</h2>
          <TaskCard 
            task={workspace.focusTask}
            variant="focus"
            showDetails={true}
          />
        </section>
      )}

      {/* Quick Wins - Easy High Priority */}
      {workspace.quickWins && workspace.quickWins.length > 0 && (
        <section className="quick-wins-section">
          <h2 className="section-title">⚡ Quick Wins ({workspace.quickWins.length})</h2>
          <div className="task-list">
            {workspace.quickWins.map(task => (
              <TaskCard 
                key={task.id}
                task={task}
                variant="quick"
              />
            ))}
          </div>
        </section>
      )}

      {/* Next Up Stack - Other Important Tasks */}
      {workspace.nextUpStack && workspace.nextUpStack.length > 0 && (
        <section className="next-up-section">
          <h2 className="section-title">📋 Next Up ({workspace.nextUpStack.length})</h2>
          <div className="task-list">
            {workspace.nextUpStack.map(task => (
              <TaskCard 
                key={task.id}
                task={task}
                variant="next"
              />
            ))}
          </div>
        </section>
      )}

      {/* Gentle Reminders */}
      {workspace.activeReminders && workspace.activeReminders.length > 0 && (
        <section className="reminders-section">
          <h2 className="section-title">💭 Gentle Reminders ({workspace.activeReminders.length})</h2>
          <div className="task-list">
            {workspace.activeReminders.map(reminder => (
              <TaskCard 
                key={reminder.id}
                task={reminder}
                variant="reminder"
              />
            ))}
          </div>
        </section>
      )}

      {/* Empty State */}
      {!workspace.focusTask && 
       (!workspace.quickWins || workspace.quickWins.length === 0) &&
       (!workspace.nextUpStack || workspace.nextUpStack.length === 0) &&
       (!workspace.activeReminders || workspace.activeReminders.length === 0) && (
        <div className="empty-workspace">
          <h2>🎉 All caught up!</h2>
          <p>No urgent tasks right now. Good time to plan ahead or take a break.</p>
          <button 
            className="add-first-task-btn"
            onClick={() => setShowAddForm(true)}
          >
            Add Your First Task
          </button>
        </div>
      )}
    </div>
  );
};

export default Workspace;