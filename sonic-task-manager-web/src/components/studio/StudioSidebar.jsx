import React from 'react';
import { Link, useLocation } from 'react-router-dom';

/**
 * Studio Sidebar Navigation
 * YouTube Studio-inspired sidebar with sections
 */
const StudioSidebar = () => {
  const location = useLocation();
  
  const isActive = (path) => {
    return location.pathname === path;
  };

  const navigation = [
    {
      section: 'Management',
      items: [
        { path: '/studio/tasks', icon: '📋', label: 'All Tasks', description: 'Comprehensive task management' },
        { path: '/studio/habits', icon: '🎯', label: 'Habits & Learning', description: 'Track sessions and progress' },
        { path: '/studio/reminders', icon: '💭', label: 'Reminders', description: 'Scheduling and patterns' },
      ]
    },
    {
      section: 'Analytics',
      items: [
        { path: '/studio/focus', icon: '🔍', label: 'Focus Analysis', description: 'Score breakdowns and optimization' },
      ]
    },
    {
      section: 'Configuration',
      items: [
        { path: '/studio/settings', icon: '⚙️', label: 'Settings', description: 'Preferences and configuration' },
      ]
    }
  ];

  return (
    <aside className="studio-sidebar">
      {/* Header */}
      <div className="studio-sidebar-header">
        <Link to="/" className="back-to-workspace">
          <span className="back-icon">←</span>
          <span>Back to Workspace</span>
        </Link>
        <h1 className="studio-title">
          <span className="studio-icon">🎨</span>
          Task Studio
        </h1>
      </div>

      {/* Navigation */}
      <nav className="studio-nav">
        {navigation.map((section, sectionIndex) => (
          <div key={sectionIndex} className="nav-section">
            <h3 className="nav-section-title">{section.section}</h3>
            <ul className="nav-items">
              {section.items.map((item) => (
                <li key={item.path} className="nav-item">
                  <Link 
                    to={item.path}
                    className={`nav-link ${isActive(item.path) ? 'nav-link--active' : ''}`}
                  >
                    <span className="nav-icon">{item.icon}</span>
                    <div className="nav-content">
                      <span className="nav-label">{item.label}</span>
                      <span className="nav-description">{item.description}</span>
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </nav>

      {/* Footer */}
      <div className="studio-sidebar-footer">
        <div className="studio-version">
          <span>Studio v1.0</span>
        </div>
      </div>
    </aside>
  );
};

export default StudioSidebar;