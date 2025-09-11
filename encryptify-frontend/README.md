# Encryptify Frontend

A secure, encrypted file management system with microservices architecture.

## Features

### 🔐 End-to-End Encryption (E2EE)
- **AES-256 encryption** for all files before upload
- **WebCrypto API** for secure key generation and management
- **IndexedDB storage** for encrypted keys
- **Automatic key cleanup** on logout/account deletion

### 📁 File Management
- **Upload files** to root directory or specific folders
- **Download files** with original filenames preserved
- **Delete files** with confirmation dialogs
- **Grid and List views** for different browsing preferences
- **Context menus** for quick actions (right-click)

### 📂 Folder Management
- **Create new folders** in any location
- **Navigate folder hierarchy** with breadcrumb navigation
- **Rename folders** with inline editing
- **Delete folders** with recursive deletion
- **Upload files directly into folders**

### 👤 User Management
- **User authentication** with login/register
- **Profile management** with account information
- **Account deletion** with complete data cleanup
- **Secure logout** with encryption key removal

### 🎨 User Interface
- **Modern, responsive design** with Tailwind CSS
- **Dark/Light theme support** (configurable)
- **Toast notifications** for user feedback
- **Loading states** and error handling
- **Mobile-friendly** responsive layout

## Technical Implementation

### Frontend Stack
- **Next.js 14** with App Router
- **React 18** with TypeScript
- **Tailwind CSS** for styling
- **Lucide React** for icons
- **React Hook Form** for form handling
- **React Hot Toast** for notifications

### Security Features
- **Client-side encryption** before file upload
- **Secure key storage** in IndexedDB
- **Password-derived key encryption** for stored keys
- **Automatic session management** with token refresh
- **Secure logout** with complete key cleanup

### Backend Compatibility
- **RESTful API** integration with microservices
- **Cookie-based authentication** for security
- **Error handling** for all HTTP status codes
- **Type-safe API calls** with TypeScript interfaces

## Getting Started

### Prerequisites
- Node.js 18+ 
- npm or yarn

### Installation
```bash
# Install dependencies
npm install

# Set up environment variables
cp environment.example .env.local
# Edit .env.local with your backend URLs

# Start development server
npm run dev
```

### Environment Variables
```bash
NEXT_PUBLIC_AUTH_URL=http://localhost:8083/api/v1/auth
NEXT_PUBLIC_CORE_URL=http://localhost:8081/api/v1
NEXT_PUBLIC_ENCRYPTION_URL=http://localhost:8081/api/v1
NEXT_PUBLIC_EMAIL_URL=http://localhost:8082/api/v1
```

## Usage

### File Operations
1. **Upload**: Click "Upload File" button and select a file
2. **Download**: Click the download icon on any file
3. **Delete**: Right-click and select "Delete" from context menu
4. **Navigate**: Click on folders to enter them

### Folder Operations
1. **Create**: Click "New Folder" button and enter a name
2. **Rename**: Right-click folder and select "Rename"
3. **Delete**: Right-click folder and select "Delete"

### Security Features
- All files are automatically encrypted before upload
- Encryption keys are stored securely in your browser
- Keys are automatically cleared on logout
- Account deletion removes all stored keys

## Architecture

### Component Structure
```
app/
├── page.tsx              # Main application entry point
├── layout.tsx            # Root layout with providers
└── globals.css           # Global styles and utilities

components/
├── Dashboard.tsx         # Dashboard overview
├── FilesView.tsx         # File and folder management
├── Profile.tsx           # User profile and account management
├── Settings.tsx          # Application settings
├── Navigation.tsx        # Main navigation
├── LoginForm.tsx         # User authentication
└── RegisterForm.tsx      # User registration

hooks/
├── useAuth.ts            # Authentication state management
└── useEncryption.ts      # Encryption and key management

services/
├── authService.ts        # Authentication API calls
└── fileService.ts        # File management API calls
```

### State Management
- **React Context** for authentication state
- **Local state** for component-specific data
- **IndexedDB** for persistent encryption key storage
- **localStorage** for master key storage

## Security Considerations

### Encryption
- **AES-256-GCM** for file encryption
- **Random IVs** for each encryption operation
- **Key derivation** from user password
- **Secure key storage** in browser's IndexedDB

### Authentication
- **Cookie-based sessions** for security
- **Automatic token refresh** every 29 minutes
- **Secure logout** with complete cleanup
- **CSRF protection** through proper headers

### Data Privacy
- **No plaintext storage** of sensitive data
- **Client-side encryption** before transmission
- **Automatic key cleanup** on session end
- **Secure deletion** of all stored data

## Development

### Available Scripts
```bash
npm run dev      # Start development server
npm run build    # Build for production
npm run start    # Start production server
npm run lint     # Run ESLint
```

### Code Quality
- **TypeScript** for type safety
- **ESLint** for code quality
- **Prettier** for code formatting
- **Tailwind CSS** for consistent styling

## Backend Integration

### API Endpoints
The frontend integrates with the following backend services:

- **Auth Service** (Port 8083): User authentication and management
- **Core Service** (Port 8080): File and folder operations
- **Encryption Service** (Port 8081): Key sharing and other encryption related operations
- **Email Service** (Port 8082): Email notifications

### Data Flow
1. **File Upload**: File → Client Encryption → Backend Storage
2. **File Download**: Backend Retrieval → Client Decryption → User
3. **Authentication**: Login → Token Generation → Session Management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository. 