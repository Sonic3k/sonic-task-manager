import React from 'react';

/**
 * Simple Header component
 * Shows date, mood, and basic stats
 */
const Header = ({ mood, workloadAssessment }) => {
  const getMoodEmoji = (mood) => {
    const moodEmojis = {
      'intense': '🔥',
      'busy': '⚡',
      'active': '🎯',
      'steady': '✊',
      'chill': '🌤️',
      'relaxed': '😌'
    };
    return moodEmojis[mood] || '👍';
  };

  const getMoodDescription = (mood) => {
    const descriptions = {
      'intense': 'Intense day ahead',
      'busy': 'Busy day with important tasks',
      'active': 'Active day with good momentum', 
      'steady': 'Steady progress day',
      'chill': 'Light day - good for deep work',
      'relaxed': 'Relaxed day'
    };
    return descriptions[mood] || 'Ready for the day';
  };

  const formatDate = () => {
    const today = new Date();
    return today.toLocaleDateString('en-US', { 
      weekday: 'long', 
      month: 'long', 
      day: 'numeric' 
    });
  };

  return (
    <header className="workspace-header">
      <div className="header-main">
        <div className="date-section">
          <h1 className="date">{formatDate()}</h1>
          <div className="mood">
            <span className="mood-emoji">{getMoodEmoji(mood)}</span>
            <span className="mood-text">{getMoodDescription(mood)}</span>
          </div>
        </div>

        {workloadAssessment && (
          <div className="stats-section">
            {workloadAssessment.totalTasks > 0 && (
              <div className="stat">
                <span className="stat-number">{workloadAssessment.totalTasks}</span>
                <span className="stat-label">tasks</span>
              </div>
            )}
            
            {workloadAssessment.urgentCount > 0 && (
              <div className="stat stat--urgent">
                <span className="stat-number">{workloadAssessment.urgentCount}</span>
                <span className="stat-label">urgent</span>
              </div>
            )}
            
            {workloadAssessment.estimatedHours > 0 && (
              <div className="stat">
                <span className="stat-number">~{workloadAssessment.estimatedHours}h</span>
                <span className="stat-label">estimated</span>
              </div>
            )}
          </div>
        )}
      </div>
      
      {workloadAssessment?.recommendation && (
        <div className="recommendation">
          💡 {workloadAssessment.recommendation}
        </div>
      )}
    </header>
  );
};

export default Header;