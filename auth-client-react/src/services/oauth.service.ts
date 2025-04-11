/**
 * OAuth service for handling social login flows
 */

// OAuth configuration
const config = {
  google: {
    clientId: import.meta.env.VITE_GOOGLE_CLIENT_ID || 'your-google-client-id',
    redirectUri: `${window.location.origin}/oauth/callback/google`,
    authUrl: 'https://accounts.google.com/o/oauth2/v2/auth',
    scope: 'openid profile email',
  },
  facebook: {
    clientId: import.meta.env.VITE_FACEBOOK_CLIENT_ID || 'your-facebook-client-id',
    redirectUri: `${window.location.origin}/oauth/callback/facebook`,
    authUrl: 'https://www.facebook.com/v12.0/dialog/oauth',
    scope: 'public_profile,email',
  }
};

/**
 * Generate a random state string for OAuth security
 */
function generateState(): string {
  return Math.random().toString(36).substring(2, 15) +
         Math.random().toString(36).substring(2, 15);
}

/**
 * Store OAuth state in localStorage for verification
 */
function saveState(state: string): void {
  localStorage.setItem('oauth_state', state);
  localStorage.setItem('oauth_state_time', Date.now().toString());
}

/**
 * Verify that the returned state matches the stored state
 */
function verifyState(returnedState: string | null): boolean {
  if (!returnedState) return false;

  const storedState = localStorage.getItem('oauth_state');
  const stateTime = localStorage.getItem('oauth_state_time');

  // Clear stored state
  localStorage.removeItem('oauth_state');
  localStorage.removeItem('oauth_state_time');

  // Verify state is valid and not expired (10 minutes)
  if (!storedState || storedState !== returnedState) return false;
  if (!stateTime || Date.now() - parseInt(stateTime) > 10 * 60 * 1000) return false;

  return true;
}

/**
 * Initiate Google OAuth flow
 */
function loginWithGoogle(): void {
  const state = generateState();
  saveState(state);

  const params = new URLSearchParams({
    client_id: config.google.clientId,
    redirect_uri: config.google.redirectUri,
    response_type: 'code',
    scope: config.google.scope,
    state: state,
    prompt: 'select_account',
  });

  window.location.href = `${config.google.authUrl}?${params.toString()}`;
}

/**
 * Initiate Facebook OAuth flow
 */
function loginWithFacebook(): void {
  const state = generateState();
  saveState(state);

  const params = new URLSearchParams({
    client_id: config.facebook.clientId,
    redirect_uri: config.facebook.redirectUri,
    response_type: 'code',
    scope: config.facebook.scope,
    state: state,
  });

  window.location.href = `${config.facebook.authUrl}?${params.toString()}`;
}

/**
 * Extract OAuth code and state from URL
 */
function extractAuthParams(): { code: string | null, state: string | null, provider: string | null } {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  // Extract provider from URL path
  const pathParts = window.location.pathname.split('/');
  let provider = pathParts[pathParts.length - 1] || null;

  // Chuyển đổi provider thành chữ in hoa để phù hợp với backend
  if (provider === 'google') {
    provider = 'GOOGLE';
  } else if (provider === 'facebook') {
    provider = 'FACEBOOK';
  }

  return { code, state, provider };
}

export default {
  loginWithGoogle,
  loginWithFacebook,
  verifyState,
  extractAuthParams,
};
