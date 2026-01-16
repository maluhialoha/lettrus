#!/bin/bash

# validator.sh - Pre-push hook for Lettrus
# Runs build and tests before allowing push

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Running pre-push validation...${NC}"

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo -e "${RED}Error: gradlew not found. Run from project root.${NC}"
    exit 1
fi

# Get modified Kotlin files
MODIFIED_FILES=$(git diff --cached --name-only --diff-filter=ACMR | grep -E '\.(kt|kts)$' || true)

if [ -z "$MODIFIED_FILES" ]; then
    echo -e "${GREEN}No Kotlin files modified, skipping validation.${NC}"
    exit 0
fi

echo -e "${YELLOW}Modified files:${NC}"
echo "$MODIFIED_FILES"
echo ""

# Determine which modules to test
MODULES_TO_TEST=""

if echo "$MODIFIED_FILES" | grep -q "^shared/"; then
    MODULES_TO_TEST="$MODULES_TO_TEST :shared:allTests"
fi

if echo "$MODIFIED_FILES" | grep -q "^androidApp/"; then
    MODULES_TO_TEST="$MODULES_TO_TEST :androidApp:assembleDebug"
fi

# Always build shared if any changes
if [ -z "$MODULES_TO_TEST" ]; then
    MODULES_TO_TEST=":shared:allTests"
fi

echo -e "${YELLOW}Running: ./gradlew $MODULES_TO_TEST${NC}"
echo ""

if ./gradlew $MODULES_TO_TEST --quiet; then
    echo ""
    echo -e "${GREEN}Validation passed!${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}Validation failed! Fix errors before pushing.${NC}"
    exit 1
fi
