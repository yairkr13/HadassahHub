@echo off
REM Script to fix TypeScript cache issues (Windows)
REM Run this script from the frontend directory

echo 🔧 Fixing TypeScript errors...
echo.

REM Step 1: Clean TypeScript cache
echo 1️⃣  Cleaning TypeScript cache...
if exist node_modules\.cache rmdir /s /q node_modules\.cache
if exist .vite rmdir /s /q .vite
if exist dist rmdir /s /q dist
if exist tsconfig.tsbuildinfo del /f /q tsconfig.tsbuildinfo

REM Step 2: Reinstall dependencies
echo 2️⃣  Reinstalling dependencies...
if exist node_modules rmdir /s /q node_modules
if exist package-lock.json del /f /q package-lock.json
call npm install

REM Step 3: Run TypeScript check
echo 3️⃣  Running TypeScript check...
call npx tsc --noEmit

echo.
echo ✅ Done! If you still see errors in VS Code:
echo    1. Press Ctrl+Shift+P
echo    2. Type 'TypeScript: Restart TS Server'
echo    3. Select it and wait for the server to restart
echo.
echo    Then reload VS Code window:
echo    1. Press Ctrl+Shift+P
echo    2. Type 'Developer: Reload Window'
echo    3. Select it
