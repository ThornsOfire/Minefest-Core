#!/bin/bash

# Minefest-Core Deployment Script
# Deploys the project from local development to a Linux production server

set -e  # Exit on any error

echo "========================================================"
echo "  Minefest-Core Deployment Script"
echo "========================================================"
echo

# Configuration - Edit these for your server
SERVER_USER="${DEPLOY_USER:-minecraft}"
SERVER_HOST="${DEPLOY_HOST:-your-server.com}"
SERVER_PATH="${DEPLOY_PATH:-/home/minecraft/server}"
LOCAL_BUILD_PATH="./server"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required environment variables are set
check_config() {
    print_status "Checking deployment configuration..."
    
    if [ -z "$DEPLOY_HOST" ]; then
        print_error "DEPLOY_HOST not set. Please set it in environment or edit this script."
        print_status "Example: export DEPLOY_HOST=your-server.com"
        exit 1
    fi
    
    print_success "Configuration OK"
    echo "  Server: $SERVER_USER@$SERVER_HOST"
    echo "  Path: $SERVER_PATH"
}

# Build the project locally
build_project() {
    print_status "Building project locally..."
    
    if command -v ./gradlew &> /dev/null; then
        ./gradlew buildAll
    elif command -v gradlew.bat &> /dev/null; then
        ./gradlew.bat buildAll
    else
        print_error "Gradle wrapper not found"
        exit 1
    fi
    
    print_success "Build completed"
}

# Test SSH connection
test_connection() {
    print_status "Testing SSH connection to $SERVER_HOST..."
    
    if ssh -o ConnectTimeout=10 -o BatchMode=yes "$SERVER_USER@$SERVER_HOST" exit 2>/dev/null; then
        print_success "SSH connection successful"
    else
        print_error "Cannot connect to $SERVER_HOST"
        print_status "Make sure:"
        print_status "1. SSH key is set up for $SERVER_USER@$SERVER_HOST"
        print_status "2. Server is reachable"
        print_status "3. User has permission to access $SERVER_PATH"
        exit 1
    fi
}

# Create backup on server
create_backup() {
    print_status "Creating backup on server..."
    
    ssh "$SERVER_USER@$SERVER_HOST" "
        if [ -d '$SERVER_PATH' ]; then
            backup_name='minefest-backup-\$(date +%Y%m%d-%H%M%S)'
            cp -r '$SERVER_PATH' '\$backup_name'
            echo 'Backup created: \$backup_name'
        else
            echo 'No existing installation found, skipping backup'
        fi
    "
    
    print_success "Backup completed"
}

# Deploy files to server
deploy_files() {
    print_status "Deploying files to server..."
    
    # Create directory structure
    ssh "$SERVER_USER@$SERVER_HOST" "mkdir -p '$SERVER_PATH'"
    
    # Sync server directory
    rsync -avz --progress \
        --exclude='world/' \
        --exclude='logs/' \
        --exclude='crash-reports/' \
        --exclude='*.log' \
        "$LOCAL_BUILD_PATH/" "$SERVER_USER@$SERVER_HOST:$SERVER_PATH/"
    
    # Make scripts executable
    ssh "$SERVER_USER@$SERVER_HOST" "
        chmod +x '$SERVER_PATH/run.sh'
        chmod +x '$SERVER_PATH/'*.sh 2>/dev/null || true
    "
    
    print_success "Files deployed successfully"
}

# Update server configuration
update_config() {
    print_status "Updating server configuration..."
    
    ssh "$SERVER_USER@$SERVER_HOST" "
        cd '$SERVER_PATH'
        
        # Ensure EULA is accepted
        echo 'eula=true' > eula.txt
        
        # Create server.properties if it doesn't exist
        if [ ! -f server.properties ]; then
            cat > server.properties << 'EOF'
# Minefest-Core Server Configuration
server-port=25565
online-mode=false
motd=Minefest-Core Production Server
max-players=100
difficulty=easy
gamemode=survival
allow-flight=true
enable-command-block=true
spawn-protection=0
view-distance=12
simulation-distance=8
max-world-size=29999984
EOF
        fi
        
        echo 'Server configuration updated'
    "
    
    print_success "Configuration updated"
}

# Verify deployment
verify_deployment() {
    print_status "Verifying deployment..."
    
    ssh "$SERVER_USER@$SERVER_HOST" "
        cd '$SERVER_PATH'
        
        # Check for required files
        if [ ! -f 'mods/minefest-essentials-'*.jar ]; then
            echo 'ERROR: Minefest mod not found'
            exit 1
        fi
        
        if [ ! -f 'run.sh' ]; then
            echo 'ERROR: run.sh not found'
            exit 1
        fi
        
        if [ ! -f 'user_jvm_args.txt' ]; then
            echo 'ERROR: user_jvm_args.txt not found'
            exit 1
        fi
        
        echo 'All required files present'
        
        # Check Java
        if command -v java &> /dev/null; then
            java_version=\$(java -version 2>&1 | head -n1 | cut -d'\"' -f2)
            echo \"Java version: \$java_version\"
        else
            echo 'WARNING: Java not found on server'
        fi
    "
    
    print_success "Deployment verification completed"
}

# Main deployment flow
main() {
    echo "Starting deployment process..."
    echo
    
    check_config
    build_project
    test_connection
    create_backup
    deploy_files
    update_config
    verify_deployment
    
    echo
    print_success "🎉 Deployment completed successfully!"
    echo
    print_status "To start the server:"
    print_status "  ssh $SERVER_USER@$SERVER_HOST"
    print_status "  cd $SERVER_PATH"
    print_status "  ./run.sh"
    echo
    print_status "To monitor the server:"
    print_status "  tail -f $SERVER_PATH/logs/latest.log"
}

# Handle command line arguments
case "${1:-}" in
    --help|-h)
        echo "Minefest-Core Deployment Script"
        echo
        echo "Usage: $0 [options]"
        echo
        echo "Environment variables:"
        echo "  DEPLOY_HOST     - Target server hostname or IP"
        echo "  DEPLOY_USER     - SSH username (default: minecraft)"
        echo "  DEPLOY_PATH     - Server installation path (default: /home/minecraft/server)"
        echo
        echo "Examples:"
        echo "  export DEPLOY_HOST=mc.example.com"
        echo "  export DEPLOY_USER=ubuntu"
        echo "  $0"
        exit 0
        ;;
    *)
        main
        ;;
esac 