import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Workspace from './components/Workspace';
import StudioLayout from './components/studio/StudioLayout';
import TasksManagement from './components/studio/TasksManagement';
import HabitsCenter from './components/studio/HabitsCenter';
import ReminderCenter from './components/studio/ReminderCenter';
import FocusAnalytics from './components/studio/FocusAnalytics';
import StudioSettings from './components/studio/StudioSettings';
import { WorkspaceProvider } from './hooks/useWorkspace.jsx';
import './styles/index.css';

/**
 * Main App Component
 * Dual interface: Workspace (simple) + Studio (comprehensive)
 */
function App() {
  return (
    <WorkspaceProvider>
      <Router>
        <div className="app">
          <Routes>
            {/* Main Workspace - Default landing */}
            <Route path="/" element={<Workspace />} />
            
            {/* Studio Interface - Comprehensive management */}
            <Route path="/studio" element={<StudioLayout />}>
              <Route index element={<Navigate to="/studio/tasks" replace />} />
              <Route path="tasks" element={<TasksManagement />} />
              <Route path="habits" element={<HabitsCenter />} />
              <Route path="reminders" element={<ReminderCenter />} />
              <Route path="focus" element={<FocusAnalytics />} />
              <Route path="settings" element={<StudioSettings />} />
            </Route>
            
            {/* Fallback redirect */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </Router>
    </WorkspaceProvider>
  );
}

export default App;