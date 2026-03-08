# HadassahHub Frontend

Academic resource sharing platform for computer science students.

## Phase 1 Features

- ✅ Homepage with hero section and feature cards
- ✅ User authentication (login/register)
- ✅ Responsive navigation bar
- ✅ Design system implementation
- ✅ Course placeholder page

## Tech Stack

- React 18 with TypeScript
- Vite for build tooling
- TailwindCSS for styling
- React Router for navigation
- React Query for API state management
- Axios for HTTP requests

## Getting Started

1. Install dependencies:
```bash
npm install
```

2. Start development server:
```bash
npm run dev
```

3. Build for production:
```bash
npm run build
```

## Project Structure

```
src/
├── components/     # Reusable UI components
├── layouts/        # Layout components
├── pages/          # Page components
├── router/         # Routing configuration
├── services/       # API services
├── types/          # TypeScript types
├── utils/          # Utility functions
├── styles/         # Global styles and design tokens
├── context/        # React Context providers
├── hooks/          # Custom React hooks
└── providers/      # App providers setup
```

## Available Routes

- `/` - Homepage (public)
- `/login` - Login page
- `/register` - Register page
- `/courses` - Course catalog (placeholder)

## Environment Setup

Make sure the backend is running on `http://localhost:8080` for API calls to work properly.