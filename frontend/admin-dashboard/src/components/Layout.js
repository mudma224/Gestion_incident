import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

function Layout() {
  return (
    <div className="layout">
      <Sidebar />
      <div className="main-content">
        <Navbar />
        <div className="page">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default Layout;