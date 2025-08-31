#!/bin/bash

echo "Installing Encryptify Frontend Dependencies..."
echo

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "Error: Node.js is not installed. Please install Node.js 18+ first."
    echo "Visit: https://nodejs.org/"
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "Error: npm is not available. Please check your Node.js installation."
    exit 1
fi

echo "Node.js version:"
node --version
echo "npm version:"
npm --version
echo

echo "Installing dependencies..."
npm install

if [ $? -ne 0 ]; then
    echo "Error: Failed to install dependencies."
    exit 1
fi

echo
echo "Dependencies installed successfully!"
echo
echo "Next steps:"
echo "1. Copy environment.example to .env.local"
echo "2. Update .env.local with your backend URLs"
echo "3. Start development server: npm run dev"
echo "4. Build for production: npm run build"
echo 