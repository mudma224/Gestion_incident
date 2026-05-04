import { render, screen, waitFor } from '@testing-library/react';
import { useKeycloak } from '@react-keycloak/web';
import { syncCurrentUser } from './api/users';

jest.mock('react-router-dom', () => {
  const React = require('react');

  return {
    BrowserRouter: ({ children }) => <div>{children}</div>,
    Routes: ({ children }) => <div>{children}</div>,
    Route: ({ element }) => element ?? null,
    Navigate: () => <div>Navigate</div>,
  };
}, { virtual: true });

jest.mock('@react-keycloak/web', () => ({
  useKeycloak: jest.fn(),
}));

jest.mock('./api/users', () => ({
  syncCurrentUser: jest.fn(),
}));

jest.mock('./components/Navbar', () => () => <div>Navbar</div>);
jest.mock('./pages/Home', () => () => <div>Page Home</div>);
jest.mock('./pages/MyIncidents', () => () => <div>Page Incidents</div>);
jest.mock('./pages/CreateIncident', () => () => <div>Page New Incident</div>);
jest.mock('./pages/Chat', () => () => <div>Page Chat</div>);

const App = require('./App').default;

describe('Client App', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    syncCurrentUser.mockResolvedValue({});
  });

  it('renders the protected application shell and syncs the current user', async () => {
    useKeycloak.mockReturnValue({
      initialized: true,
      keycloak: {
        authenticated: true,
        login: jest.fn(),
      },
    });

    render(<App />);

    expect(await screen.findByText('Navbar')).toBeInTheDocument();
    expect(await screen.findByText('Page Home')).toBeInTheDocument();

    await waitFor(() => {
      expect(syncCurrentUser).toHaveBeenCalledTimes(1);
    });
  });

  it('starts the login flow when the user is not authenticated', () => {
    const login = jest.fn();
    useKeycloak.mockReturnValue({
      initialized: true,
      keycloak: {
        authenticated: false,
        login,
      },
    });

    render(<App />);

    expect(login).toHaveBeenCalledTimes(1);
  });
});
