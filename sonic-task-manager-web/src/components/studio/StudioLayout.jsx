import React from 'react';
import { Outlet } from 'react-router-dom';
import StudioSidebar from './StudioSidebar';

/**
 * Studio Layout Component
 * YouTube Studio-inspired layout with sidebar navigation
 */
const StudioLayout = () => {
  return (
    <div className="studio-layout">
      <StudioSidebar />
      <main className="studio-content">
        <Outlet />
      </main>
    </div>
  );
};

export default StudioLayout;