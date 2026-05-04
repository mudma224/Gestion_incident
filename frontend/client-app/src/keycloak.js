import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: process.env.REACT_APP_KEYCLOAK_URL || 'http://localhost:8180',
  realm: process.env.REACT_APP_KEYCLOAK_REALM || 'incidents-realm',
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID || 'react-client',
});

export default keycloak;
