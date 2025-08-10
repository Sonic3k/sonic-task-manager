import React, { useState } from 'react';

/**
 * Bulk Actions Component
 * Actions for multiple selected tasks
 */
const BulkActions = ({ selectedCount, onComplete, onSnooze, onClearSelection }) => {
  const [showSnoozeOptions, setShowSnoozeOptions] = useState(false);

  const handleSnooze = (days) => {
    onSnooze(days);
    setShowSnoozeOptions(false);
  };

  return (
    <div className="bulk-actions">
      <div className="bulk-actions-info">
        <span className="selected-count">{selectedCount} task{selectedCount !== 1 ? 's' : ''} selected</span>
        <button 
          className="btn btn--text"
          onClick={onClearSelection}
        >
          Clear Selection
        </button>
      </div>

      <div className="bulk-actions-buttons">
        <button 
          className="btn btn--success"
          onClick={onComplete}
        >
          ✓ Complete All
        </button>

        <div className="snooze-dropdown">
          <button 
            className="btn btn--secondary"
            onClick={() => setShowSnoozeOptions(!showSnoozeOptions)}
          >
            💤 Snooze ▼
          </button>
          
          {showSnoozeOptions && (
            <div className="snooze-options">
              <button onClick={() => handleSnooze(1)}>1 day</button>
              <button onClick={() => handleSnooze(3)}>3 days</button>
              <button onClick={() => handleSnooze(7)}>1 week</button>
              <button onClick={() => handleSnooze(30)}>1 month</button>
            </div>
          )}
        </div>

        <button className="btn btn--warning">
          📝 Edit
        </button>

        <button className="btn btn--danger">
          🗑️ Delete
        </button>
      </div>
    </div>
  );
};

export default BulkActions;