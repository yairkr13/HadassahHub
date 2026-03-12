#!/bin/bash

# Script to fix TypeScript cache issues
# Run this script from the frontend directory

echo "🔧 Fixing TypeScript errors..."
echo ""

# Step 1: Clean TypeScript cache
echo "1️⃣  Cleaning TypeScript cache..."
rm -rf node_modules/.cache
rm -rf .vite
rm -rf dist
rm -rf tsconfig.tsbuildinfo

# Step 2: Reinstall dependencies
echo "2️⃣  Reinstalling dependencies..."
rm -rf node_modules
rm -f package-lock.json
npm install

# Step 3: Run TypeScript check
echo "3️⃣  Running TypeScript check..."
npx tsc --noEmit

echo ""
echo "✅ Done! If you still see errors in VS Code:"
echo "   1. Press Ctrl+Shift+P (Cmd+Shift+P on Mac)"
echo "   2. Type 'TypeScript: Restart TS Server'"
echo "   3. Select it and wait for the server to restart"
echo ""
echo "   Then reload VS Code window:"
echo "   1. Press Ctrl+Shift+P (Cmd+Shift+P on Mac)"
echo "   2. Type 'Developer: Reload Window'"
echo "   3. Select it"
