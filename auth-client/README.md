# AuthenHub Client

A Vue.js 3 + TypeScript + Vite frontend for the AuthenHub authentication gateway.

## Features

- User registration and login
- Social login with Google and Facebook
- JWT authentication
- Protected routes
- User profile management
- Responsive design with Tailwind CSS

## Project Setup

### Prerequisites

- Node.js (v16+)
- npm or yarn
- AuthenHub backend server running

### Installation

1. Clone the repository
2. Install dependencies:

```bash
npm install
# or
yarn install
```

### Configuration

1. Create a `.env` file based on the `.env.example` template:

```bash
cp .env.example .env
```

2. Update the `.env` file with your OAuth client IDs:

```
VITE_GOOGLE_CLIENT_ID=your-google-client-id
VITE_FACEBOOK_CLIENT_ID=your-facebook-client-id
```

3. The application is configured to connect to the backend server running at `http://localhost:8118`. If your backend is running on a different port or host, update the `BASE_URL` in `src/services/api.ts`:

```typescript
// API base URL
const BASE_URL = 'http://your-backend-url:port'
```

### Development

Start the development server:

```bash
npm run dev
# or
yarn dev
```

The application will be available at `http://localhost:5173`.

### Build for Production

```bash
npm run build
# or
yarn build
```

The built files will be in the `dist` directory.

### Preview Production Build

```bash
npm run preview
# or
yarn preview
```

## Project Structure

- `src/assets` - Static assets like CSS
- `src/components` - Vue components
- `src/router` - Vue Router configuration
- `src/services` - API services
- `src/stores` - Pinia stores for state management
- `src/views` - Page components

## Authentication Flow

1. User registers or logs in through the UI
2. Backend validates credentials and returns a JWT token
3. Frontend stores the token in localStorage
4. Token is included in the Authorization header for subsequent API requests
5. Protected routes check for valid authentication before rendering

## Social Login

The application implements OAuth2 authentication flow for Google and Facebook:

1. When a user clicks on a social login button, they are redirected to the provider's login page
2. After successful authentication, the provider redirects back to the application with an authorization code
3. The application exchanges this code for user information via the backend
4. The backend verifies the code, retrieves user information, and returns a JWT token

### OAuth Configuration

To use social login, you need to:

1. Register your application with Google and Facebook developer consoles
2. Configure the redirect URIs to point to your application's callback URL:
   - Google: `http://localhost:5173/oauth/callback/google`
   - Facebook: `http://localhost:5173/oauth/callback/facebook`
3. Update the `.env` file with your client IDs
4. Configure your backend to handle the OAuth callback

## License

MIT
