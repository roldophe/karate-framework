#!/bin/bash

# Script to install Git hooks for code quality and security checks
# Run this script from the project root directory

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Installing Git Hooks${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo ""

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}⚠${NC} Not a git repository. Please run this script from the project root."
    exit 1
fi

# Create .git/hooks directory if it doesn't exist
mkdir -p .git/hooks

# Install pre-commit hook
echo -e "${BLUE}ℹ${NC} Installing pre-commit hook..."

if [ -f "hooks/pre-commit" ]; then
    cp hooks/pre-commit .git/hooks/pre-commit
    chmod +x .git/hooks/pre-commit
    echo -e "${GREEN}✓${NC} Pre-commit hook installed successfully!"
else
    echo -e "${YELLOW}⚠${NC} hooks/pre-commit not found. Please ensure you're in the project root."
    exit 1
fi

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  Git Hooks Installed Successfully!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════${NC}"
echo ""

echo "The following hooks are now active:"
echo ""
echo "  📋 Pre-commit Hook:"
echo "     • Automatic code formatting (Spotless)"
echo "     • Static code analysis (PMD)"
echo "     • Security analysis (SpotBugs)"
echo "     • Hardcoded secrets detection"
echo "     • Common issue detection"
echo ""

echo "Configuration options:"
echo ""
echo "  • To skip checks (not recommended):"
echo "    git commit --no-verify"
echo ""
echo "  • To enable CVE scanning on commit:"
echo "    Edit .git/hooks/pre-commit and uncomment the CVE scan section"
echo ""
echo "  • To run full security check manually:"
echo "    ./gradlew fullSecurityCheck"
echo ""

echo -e "${BLUE}Happy coding with security and quality built-in! 🔒${NC}"
echo ""
