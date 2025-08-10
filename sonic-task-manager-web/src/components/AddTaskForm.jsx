import React, { useState } from 'react';
import { useWorkspace } from '../hooks/useWorkspace.jsx';

/**
 * Simple form to add new tasks
 * Auto-detects type and complexity based on keywords
 */
const AddTaskForm = ({ onCancel, onSuccess }) => {
  const { addTask } = useWorkspace();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    type: 'deadline',
    priority: 'medium',
    complexity: 'medium',
    deadline: '',
    focusContext: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Auto-detect task type based on title keywords
  const autoDetectType = (title) => {
    const lowerTitle = title.toLowerCase();
    
    if (lowerTitle.includes('practice') || lowerTitle.includes('learn') || 
        lowerTitle.includes('study') || lowerTitle.includes('tập')) {
      return 'habit';
    }
    
    if (lowerTitle.includes('think') || lowerTitle.includes('consider') || 
        lowerTitle.includes('nghĩ') || lowerTitle.includes('remember')) {
      return 'reminder';
    }
    
    return 'deadline';
  };

  // Auto-detect complexity based on title keywords
  const autoDetectComplexity = (title) => {
    const lowerTitle = title.toLowerCase();
    
    // Easy tasks
    if (lowerTitle.includes('call') || lowerTitle.includes('email') || 
        lowerTitle.includes('reply') || lowerTitle.includes('backup') ||
        lowerTitle.includes('gọi') || lowerTitle.includes('trả lời')) {
      return 'easy';
    }
    
    // Hard tasks
    if (lowerTitle.includes('design') || lowerTitle.includes('develop') || 
        lowerTitle.includes('research') || lowerTitle.includes('thiết kế') ||
        lowerTitle.includes('phát triển') || lowerTitle.includes('nghiên cứu')) {
      return 'hard';
    }
    
    return 'medium';
  };

  const handleTitleChange = (e) => {
    const title = e.target.value;
    setFormData(prev => ({
      ...prev,
      title,
      type: autoDetectType(title),
      complexity: autoDetectComplexity(title)
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      setError('Task title is required');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const taskData = {
        ...formData,
        title: formData.title.trim(),
        deadline: formData.deadline || null
      };

      await addTask(taskData);
      console.log('✅ Task added successfully');
      
      if (onSuccess) onSuccess();
    } catch (err) {
      console.error('❌ Failed to add task:', err);
      setError('Failed to create task. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="add-task-form" onSubmit={handleSubmit}>
      <div className="form-header">
        <h3>Add New Task</h3>
        <button 
          type="button" 
          className="close-btn"
          onClick={onCancel}
        >
          ✕
        </button>
      </div>

      {error && (
        <div className="form-error">
          {error}
        </div>
      )}

      {/* Title */}
      <div className="form-group">
        <label>What needs to be done?</label>
        <input
          type="text"
          value={formData.title}
          onChange={handleTitleChange}
          placeholder="e.g., Design user interface, Call dentist, Learn React..."
          disabled={loading}
          autoFocus
        />
      </div>

      {/* Description */}
      <div className="form-group">
        <label>Description (optional)</label>
        <textarea
          value={formData.description}
          onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
          placeholder="Additional details..."
          rows="2"
          disabled={loading}
        />
      </div>

      {/* Quick Settings Row */}
      <div className="form-row">
        <div className="form-group">
          <label>Type</label>
          <select
            value={formData.type}
            onChange={(e) => setFormData(prev => ({ ...prev, type: e.target.value }))}
            disabled={loading}
          >
            <option value="deadline">Task with deadline</option>
            <option value="habit">Habit/Learning</option>
            <option value="reminder">Gentle reminder</option>
            <option value="event">Event/Meeting</option>
          </select>
        </div>

        <div className="form-group">
          <label>Priority</label>
          <select
            value={formData.priority}
            onChange={(e) => setFormData(prev => ({ ...prev, priority: e.target.value }))}
            disabled={loading}
          >
            <option value="high">High</option>
            <option value="medium">Medium</option>
            <option value="low">Low</option>
          </select>
        </div>

        <div className="form-group">
          <label>Complexity</label>
          <select
            value={formData.complexity}
            onChange={(e) => setFormData(prev => ({ ...prev, complexity: e.target.value }))}
            disabled={loading}
          >
            <option value="easy">Quick task</option>
            <option value="medium">Medium effort</option>
            <option value="hard">Needs focus</option>
          </select>
        </div>
      </div>

      {/* Deadline */}
      {formData.type === 'deadline' && (
        <div className="form-group">
          <label>Deadline (optional)</label>
          <input
            type="date"
            value={formData.deadline}
            onChange={(e) => setFormData(prev => ({ ...prev, deadline: e.target.value }))}
            disabled={loading}
          />
        </div>
      )}

      {/* Focus Context */}
      <div className="form-group">
        <label>Focus Context (optional)</label>
        <input
          type="text"
          value={formData.focusContext}
          onChange={(e) => setFormData(prev => ({ ...prev, focusContext: e.target.value }))}
          placeholder="e.g., Just rough sketch needed, no need to be perfect"
          disabled={loading}
        />
      </div>

      {/* Actions */}
      <div className="form-actions">
        <button 
          type="button" 
          className="btn btn--secondary"
          onClick={onCancel}
          disabled={loading}
        >
          Cancel
        </button>
        <button 
          type="submit" 
          className="btn btn--primary"
          disabled={loading || !formData.title.trim()}
        >
          {loading ? 'Creating...' : 'Add Task'}
        </button>
      </div>
    </form>
  );
};

export default AddTaskForm;