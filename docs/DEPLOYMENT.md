# Minefest-Core Deployment Guide

## Overview

This guide covers deploying Minefest-Core from your local Windows development environment to a Linux production server. The deployment system ensures that what works locally will work in production.

## Development vs Production

### Local Development (Windows)
- **Environment**: Windows with Gradle development server
- **Java**: Oracle JDK 17 or higher
- **Location**: `run/mods/` directory
- **Purpose**: Testing and development

### Production (Linux)
- **Environment**: Linux server (Ubuntu/CentOS/etc.)
- **Java**: OpenJDK 17 or higher
- **Location**: `server/mods/` directory  
- **Purpose**: Live server for players

## Prerequisites

### Local Environment (Windows)
- ? Java 17 installed
- ? Project building successfully (`./gradlew buildAll`)
- ? SSH client (Git Bash, WSL, or OpenSSH for Windows)

### Production Server (Linux)
- ? Java 17+ installed (`sudo apt install openjdk-17-jdk`)
- ? SSH server running
- ? User with sudo privileges
- ? At least 8GB RAM (6GB for Minecraft + 2GB for OS)
- ? SSH key authentication set up

## Deployment Methods

### Method 1: Automated Deployment Script ? **Recommended**

The automated deployment script handles everything for you:

#### Windows
```batch
# Set your server details
set DEPLOY_HOST=your-server.com
set DEPLOY_USER=minecraft
set DEPLOY_PATH=/home/minecraft/server

# Deploy
deploy.bat
```

#### Linux/macOS/Git Bash
```bash
# Set your server details
export DEPLOY_HOST=your-server.com
export DEPLOY_USER=minecraft  
export DEPLOY_PATH=/home/minecraft/server

# Deploy
./deploy.sh
```

**What it does:**
1. ? Builds project locally with `./gradlew buildAll`
2. ? Tests SSH connection
3. ? Creates backup of existing server
4. ? Deploys all server files
5. ? Sets up configuration
6. ? Verifies deployment

### Method 2: Manual Deployment

For more control or troubleshooting:

1. **Build locally:**
   ```bash
   ./gradlew buildAll
   ```

2. **Copy files to server:**
   ```bash
   rsync -avz --progress server/ user@your-server:/path/to/server/
   ```

3. **Set up on server:**
   ```bash
   ssh user@your-server
   cd /path/to/server
   chmod +x run.sh
   echo "eula=true" > eula.txt
   ```

## Server Setup (First Time)

### 1. Create Server User
```bash
# On your Linux server
sudo adduser minecraft
sudo usermod -aG sudo minecraft
```

### 2. Install Java
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# Verify installation
java -version
```

### 3. Set Up SSH Key Authentication
```bash
# On your local machine
ssh-keygen -t rsa -b 4096 -C "your-email@domain.com"
ssh-copy-id minecraft@your-server.com

# Test connection
ssh minecraft@your-server.com
```

### 4. Configure Firewall
```bash
# Ubuntu (ufw)
sudo ufw allow 25565/tcp  # Minecraft port
sudo ufw enable

# CentOS (firewalld)
sudo firewall-cmd --permanent --add-port=25565/tcp
sudo firewall-cmd --reload
```

## Cloud Server Deployment

### AWS EC2
1. **Launch Instance:**
   - AMI: Ubuntu 20.04 LTS
   - Instance Type: t3.large (2 vCPU, 8GB RAM)
   - Security Group: Allow port 25565

2. **Connect and setup:**
   ```bash
   ssh -i your-key.pem ubuntu@ec2-x-x-x-x.compute-1.amazonaws.com
   sudo apt update && sudo apt install openjdk-17-jdk
   ```

### Google Cloud Platform
1. **Create VM:**
   - Machine Type: e2-standard-2 (2 vCPU, 8GB RAM)
   - OS: Ubuntu 20.04 LTS
   - Firewall: Allow port 25565

2. **Setup:**
   ```bash
   gcloud compute ssh your-instance-name
   sudo apt update && sudo apt install openjdk-17-jdk
   ```

### DigitalOcean
1. **Create Droplet:**
   - Size: 2 vCPU, 8GB RAM
   - Image: Ubuntu 20.04 LTS

2. **Setup:**
   ```bash
   ssh root@your-droplet-ip
   adduser minecraft
   usermod -aG sudo minecraft
   apt update && apt install openjdk-17-jdk
   ```

## Configuration

### Memory Allocation
The server is configured for 6GB RAM allocation in `user_jvm_args.txt`:

```
# Memory allocation
-Xms2G
-Xmx6G

# Garbage collection optimization
-XX:+UseG1GC
-XX:MaxGCPauseMillis=50
-XX:G1HeapRegionSize=32M

# JVM optimizations
-XX:+UnlockExperimentalVMOptions
-XX:+UseZGC
-XX:+DisableExplicitGC
```

### Server Properties
The deployment script creates a basic `server.properties`:

```properties
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
```

## Starting the Server

### Linux Production Server
```bash
ssh minecraft@your-server.com
cd /home/minecraft/server
./run.sh
```

### Monitoring
```bash
# View real-time logs
tail -f logs/latest.log

# Check server status
ps aux | grep java

# Check memory usage
free -h
```

## Environment Synchronization

The key advantage of our build system is that it keeps development and production synchronized:

| Environment | Location | Version Source |
|-------------|----------|----------------|
| Development | `run/mods/` | Built by `./gradlew buildAll` |
| Production | `server/mods/` | Built by `./gradlew buildAll` |

Both environments get the **exact same JAR file**, eliminating "it works on my machine" issues.

## Troubleshooting

### Common Issues

#### SSH Connection Fails
```bash
# Test basic connectivity
ping your-server.com

# Test SSH with verbose output
ssh -v minecraft@your-server.com

# Check SSH service on server
sudo systemctl status ssh
```

#### Java Not Found
```bash
# Install Java on server
sudo apt update
sudo apt install openjdk-17-jdk

# Check Java version
java -version
```

#### Server Won't Start
```bash
# Check logs
tail -50 logs/latest.log

# Check Java process
ps aux | grep java

# Check memory
free -h
```

#### Mod Not Loading
```bash
# Verify mod file exists
ls -la mods/minefest-essentials-*.jar

# Check forge logs
grep -i "minefest" logs/latest.log
```

### Performance Optimization

#### For Large Servers (100+ players)
```bash
# Increase memory allocation in user_jvm_args.txt
-Xms4G
-Xmx12G

# Adjust server.properties
max-players=200
view-distance=8
simulation-distance=6
```

#### For VPS/Budget Servers
```bash
# Lower memory allocation
-Xms1G
-Xmx4G

# Reduce server load
max-players=50
view-distance=6
simulation-distance=4
```

## Update Workflow

### Regular Updates
1. **Develop locally** on Windows
2. **Test** with `./gradlew runServer`
3. **Deploy** with `deploy.bat` or `deploy.sh`
4. **Verify** production server

### Emergency Hotfixes
```bash
# Quick production-only update
./gradlew copyModToServerMods

# Deploy just the mod file
scp server/mods/minefest-essentials-*.jar minecraft@server:/home/minecraft/server/mods/
```

## Backup Strategy

### Automated Backups
The deployment script automatically creates backups:
```
minefest-backup-20240522-143021/
```

### Manual Backups
```bash
# On server
cd /home/minecraft
tar -czf server-backup-$(date +%Y%m%d).tar.gz server/

# Download backup
scp minecraft@server:/home/minecraft/server-backup-*.tar.gz ./backups/
```

## Security Considerations

### Server Security
- ? Use SSH key authentication
- ? Disable password authentication
- ? Keep Java updated
- ? Use firewall rules
- ? Regular security updates

### Minecraft Security
- ? Use offline mode for development
- ? Consider online mode for production
- ? Set up proper permissions
- ? Monitor server logs

---
*Last Updated: 2025-05-22*
*Version: 1.20.4-0.4.3.0********* 