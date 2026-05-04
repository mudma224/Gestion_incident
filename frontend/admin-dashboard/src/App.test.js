import { render, screen, waitFor } from '@testing-library/react';
import { useKeycloak } from '@react-keycloak/web';
import { syncCurrentUser } from './api/profile';

jest.mock('react-router-dom', () => {
  const React = require('react');

  return {
    BrowserRouter: ({ children }) => <div>{children}</div>,
    Routes: ({ children }) => <div>{children}</div>,
    Route: ({ element }) => element ?? null,
    Navigate: () => <div>Navigate</div>,
    Outlet: () => <div>Outlet</div>,
  };
}, { virtual: true });

jest.mock('@react-keycloak/web', () => ({
  useKeycloak: jest.fn(),
}));

jest.mock('./api/profile', () => ({
  syncCurrentUser: jest.fn(),
}));

jest.mock('./components/Layout', () => () => <div>Admin Layout</div>);
jest.mock('./pages/Dashboard', () => () => <div>Page Dashboard</div>);
jest.mock('./pages/IncidentList', () => () => <div>Page Incident List</div>);
jest.mock('./pages/IncidentDetail', () => () => <div>Page Incident Detail</div>);
jest.mock('./pages/UserList', () => () => <div>Page User List</div>);

const App = require('./App').default;

describe('Admin App', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    syncCurrentUser.mockResolvedValue({});
  });

  it('renders the protected admin shell for an administrator and syncs the profile', async () => {
    useKeycloak.mockReturnValue({
      initialized: true,
      keycloak: {
        authenticated: true,
        tokenParsed: {
          realm_access: {
            roles: ['ROLE_ADMIN'],
          },
        },
      },
    });

    render(<App />);

    expect(await screen.findByText('Admin Layout')).toBeInTheDocument();
    expect(screen.queryByText('Acces reserve aux administrateurs.')).not.toBeInTheDocument();

    await waitFor(() => {
      expect(syncCurrentUser).toHaveBeenCalledTimes(1);
    });
  });

  it('shows the access denied screen to a non-admin user', async () => {
    const logout = jest.fn();
    useKeycloak.mockReturnValue({
      initialized: true,
      keycloak: {
        authenticated: true,
        logout,
        tokenParsed: {
          realm_access: {
            roles: ['ROLE_USER'],
          },
        },
      },
    });

    render(<App />);

    expect(await screen.findByText('Acces reserve aux administrateurs.')).toBeInTheDocument();
    expect(syncCurrentUser).toHaveBeenCalledTimes(1);
  });
});
