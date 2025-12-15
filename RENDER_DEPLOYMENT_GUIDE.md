# 🚀 Render Deployment Guide - Task Management API

This guide provides step-by-step instructions for deploying the Task Management API to Render with enterprise-grade configuration.

---

## 📋 Prerequisites

Before deploying to Render, ensure you have:

- ✅ A Render account (https://render.com)
- ✅ Your code in a Git repository (GitHub, GitLab, or Bitbucket)
- ✅ A domain name (optional, for custom domains)
- ✅ Access to configure environment variables

---

## 🏗️ Deployment Architecture

### Infrastructure Components

```
┌─────────────────────────────────────────────────┐
│              Load Balancer (Render)             │
└────────────────────┬────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐      ┌────────▼────────┐
│  App Instance  │      │  App Instance   │
│   (Primary)    │      │  (Redundant)    │
└───────┬────────┘      └────────┬────────┘
        │                        │
        └────────────┬───────────┘
                     │
        ┌────────────▼────────────┐
        │  PostgreSQL Database    │
        │    (Render Managed)     │
        └─────────────────────────┘
                     │
        ┌────────────▼────────────┐
        │  Persistent Disk        │
        │  (File Uploads - 10GB)  │
        └─────────────────────────┘
```

---

## 🎯 Method 1: Deploy Using render.yaml (Recommended)

### Step 1: Prepare Your Repository

1. **Push your code to Git:**
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

2. **Ensure render.yaml is in root directory:**
   - The `render.yaml` file is already created in your project root
   - This file contains all infrastructure configuration

### Step 2: Create Render Account and Connect Repository

1. Go to https://render.com and sign up/login
2. Click **"New +"** → **"Blueprint"**
3. Connect your Git repository:
   - **GitHub**: Authorize Render to access your repository
   - **GitLab/Bitbucket**: Follow the connection prompts
4. Select the repository containing your project

### Step 3: Configure Blueprint

1. Render will automatically detect `render.yaml`
2. Review the services to be created:
   - ✅ PostgreSQL Database: `task-management-db`
   - ✅ Web Service: `task-management-api` (2 instances)
   - ✅ Persistent Disk: 10GB for file uploads

3. **Update Environment Variables:**
   - Click on each service to review settings
   - **Critical**: Update `CORS_ALLOWED_ORIGINS` with your frontend URL
   - **Critical**: Render will auto-generate `JWT_SECRET` (64 characters)

### Step 4: Deploy

1. Click **"Apply"** to create all services
2. Render will:
   - Create PostgreSQL database
   - Build your application using Maven
   - Deploy 2 instances for high availability
   - Attach persistent disk for uploads
   - Configure health checks

3. **Wait for deployment** (5-10 minutes):
   - Monitor build logs
   - Watch for "Build successful" message
   - Wait for health checks to pass

### Step 5: Verify Deployment

1. **Get your application URL:**
   - Format: `https://task-management-api.onrender.com`
   - Or your custom domain

2. **Test health endpoint:**
```bash
curl https://your-app-url.onrender.com/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

3. **Test Swagger UI:**
```
https://your-app-url.onrender.com/swagger-ui.html
```

4. **Test user registration:**
```bash
curl -X POST https://your-app-url.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "tenantName": "Test Corp"
  }'
```

---

## 🎯 Method 2: Manual Setup (Alternative)

### Step 1: Create PostgreSQL Database

1. In Render Dashboard, click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `task-management-db`
   - **Database**: `task_management_db`
   - **User**: `task_admin`
   - **Region**: Choose closest to your users
   - **Plan**: Standard (recommended for production)
3. Click **"Create Database"**
4. **Save connection details** (you'll need these)

### Step 2: Create Web Service

1. Click **"New +"** → **"Web Service"**
2. Connect your Git repository
3. Configure:
   - **Name**: `task-management-api`
   - **Region**: Same as database
   - **Branch**: `main`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -jar target/task-management-api-0.0.1-SNAPSHOT.jar`
   - **Plan**: Standard (for production)
   - **Instances**: 2 (for high availability)

### Step 3: Configure Environment Variables

Add these environment variables in Render dashboard:

```
# Database (from PostgreSQL service)
DB_HOST=<your-db-host>
DB_PORT=5432
DB_NAME=task_management_db
DB_USER=task_admin
DB_PASSWORD=<your-db-password>

# JWT (generate secure values)
JWT_SECRET=<generate-64-char-secret>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# File Upload
FILE_UPLOAD_DIR=/opt/render/project/src/uploads
FILE_MAX_SIZE=10485760

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database Pool
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_ADEWUNMI=INFO

# Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics

# JVM Options
JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Step 4: Add Persistent Disk

1. In your web service settings, go to **"Disks"**
2. Click **"Add Disk"**
3. Configure:
   - **Name**: `uploads`
   - **Mount Path**: `/opt/render/project/src/uploads`
   - **Size**: 10GB
4. Click **"Save"**

### Step 5: Configure Health Check

1. In web service settings, go to **"Health Check"**
2. Set:
   - **Path**: `/actuator/health`
   - **Interval**: 30 seconds
   - **Timeout**: 5 seconds
   - **Threshold**: 3 failures

### Step 6: Deploy

1. Click **"Create Web Service"**
2. Render will build and deploy your application
3. Monitor logs for any errors

---

## 🔐 Security Configuration

### 1. Generate Secure JWT Secret

```bash
# Generate 64-character random secret
openssl rand -base64 64 | tr -d '\n'
```

Add this to `JWT_SECRET` environment variable in Render.

### 2. Update CORS Origins

In Render dashboard, update `CORS_ALLOWED_ORIGINS`:
```
https://your-frontend-domain.com,https://www.your-frontend-domain.com
```

### 3. Configure Database SSL

Render PostgreSQL uses SSL by default. No additional configuration needed.

### 4. Enable HTTPS

- Render provides automatic HTTPS
- SSL certificates are managed by Render
- All traffic is encrypted by default

---

## 🔧 Post-Deployment Configuration

### 1. Verify Database Migrations

Check that Flyway migrations ran successfully:
```bash
# SSH into your service (if available) or check logs
grep "Flyway" logs
```

Expected: All 8 migrations should complete successfully.

### 2. Create Initial Admin User

```bash
curl -X POST https://your-app-url.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@yourcompany.com",
    "password": "SecureAdminPass123!",
    "tenantName": "Your Company"
  }'
```

### 3. Configure Custom Domain (Optional)

1. In Render dashboard, go to your web service
2. Click **"Settings"** → **"Custom Domain"**
3. Add your domain (e.g., `api.yourcompany.com`)
4. Update DNS records as instructed by Render:
   - Type: `CNAME`
   - Host: `api`
   - Value: `your-app-name.onrender.com`
5. Wait for DNS propagation (5-60 minutes)
6. Render will automatically provision SSL certificate

### 4. Set Up Monitoring

1. **Enable Render Metrics:**
   - CPU usage
   - Memory usage
   - Request latency
   - Error rates

2. **Configure Alerts:**
   - High error rates
   - Service downtime
   - Database connection issues

3. **External Monitoring (Optional):**
   - UptimeRobot: https://uptimerobot.com
   - Pingdom: https://www.pingdom.com
   - DataDog: https://www.datadoghq.com

---

## 📊 Scaling Configuration

### Horizontal Scaling

**Current Setup**: 2 instances (configured in render.yaml)

**To scale up:**
1. Go to web service settings
2. Update **"Instances"** count
3. Click **"Save"**

**Recommended scaling:**
- **Low traffic** (< 1000 users): 2 instances
- **Medium traffic** (1000-10,000 users): 3-5 instances
- **High traffic** (> 10,000 users): 5+ instances

### Vertical Scaling

**Current Plan**: Standard

**To upgrade:**
1. Go to service settings
2. Change **"Plan"**
3. Options:
   - **Starter**: 0.5 GB RAM, 0.5 CPU
   - **Standard**: 2 GB RAM, 1 CPU (current)
   - **Pro**: 4 GB RAM, 2 CPU
   - **Pro Plus**: 8 GB RAM, 4 CPU

### Database Scaling

1. Go to PostgreSQL service
2. Upgrade plan:
   - **Starter**: 1 GB RAM
   - **Standard**: 4 GB RAM (current)
   - **Pro**: 8 GB RAM
   - **Pro Plus**: 16 GB RAM

---

## 🔍 Monitoring and Debugging

### View Application Logs

1. Go to your web service in Render
2. Click **"Logs"** tab
3. View real-time logs

**Filter logs:**
```bash
# Error logs only
grep "ERROR" logs

# Application logs
grep "com.adewunmi" logs

# Database logs
grep "Hibernate" logs
```

### Check Health Status

```bash
# Health check
curl https://your-app-url.onrender.com/actuator/health

# Detailed health
curl https://your-app-url.onrender.com/actuator/health \
  -H "Authorization: Bearer YOUR_TOKEN"

# Metrics
curl https://your-app-url.onrender.com/actuator/metrics
```

### Common Issues and Solutions

#### Issue 1: Build Fails

**Symptoms**: Build exits with error during Maven build

**Solutions:**
```bash
# Check Java version
java -version  # Should be 17

# Verify pom.xml
mvn validate

# Test build locally
mvn clean package -DskipTests
```

#### Issue 2: Database Connection Fails

**Symptoms**: Application starts but can't connect to database

**Solutions:**
1. Verify environment variables:
   - `DB_HOST`
   - `DB_PORT`
   - `DB_NAME`
   - `DB_USER`
   - `DB_PASSWORD`

2. Check database is running:
   - Go to PostgreSQL service
   - Verify status is "Available"

3. Check IP allowlist in database settings

#### Issue 3: Health Check Fails

**Symptoms**: Service shows "Unhealthy"

**Solutions:**
1. Check if application is listening on correct port:
   - Must use `$PORT` environment variable
   - Render sets this automatically

2. Verify health endpoint:
```bash
curl https://your-app-url.onrender.com/actuator/health
```

3. Check application logs for startup errors

#### Issue 4: File Upload Fails

**Symptoms**: 500 error when uploading files

**Solutions:**
1. Verify persistent disk is attached:
   - Mount path: `/opt/render/project/src/uploads`
   - Size: 10GB

2. Check disk space:
   - Go to service → Disks
   - Monitor usage

3. Verify environment variable:
   - `FILE_UPLOAD_DIR=/opt/render/project/src/uploads`

---

## 💰 Cost Estimation

### Monthly Costs (USD)

| Service | Plan | Cost |
|---------|------|------|
| Web Service (2 instances) | Standard | $14 × 2 = $28 |
| PostgreSQL Database | Standard | $20 |
| Persistent Disk (10GB) | Standard | $0.25/GB = $2.50 |
| **Total** | | **$50.50/month** |

### Cost Optimization Tips

1. **Development environment**: Use Starter plan ($7/month)
2. **Reduce instances**: Scale down during low traffic
3. **Database**: Use shared database for dev/staging
4. **Disk space**: Monitor and adjust as needed

---

## 🚀 CI/CD Configuration

### Automatic Deployments

Render automatically deploys when you push to your connected branch.

**Workflow:**
```
git push origin main
    ↓
Render detects changes
    ↓
Builds application
    ↓
Runs health checks
    ↓
Deploys to production
    ↓
Old instances shut down
```

### Manual Deployments

1. Go to your web service
2. Click **"Manual Deploy"**
3. Select branch
4. Click **"Deploy"**

### Deployment Notifications

Configure webhook notifications:
1. Go to service settings
2. **Notifications** → **Webhooks**
3. Add webhook URL (Slack, Discord, etc.)

---

## 🔒 Environment Variables Reference

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | Database hostname | `abc123.oregon-postgres.render.com` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `task_management_db` |
| `DB_USER` | Database username | `task_admin` |
| `DB_PASSWORD` | Database password | `generated-by-render` |
| `JWT_SECRET` | JWT signing secret | `64-char-random-string` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_EXPIRATION` | Access token expiry (ms) | `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiry (ms) | `604800000` (7d) |
| `FILE_MAX_SIZE` | Max upload size (bytes) | `10485760` (10MB) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `*` |
| `LOGGING_LEVEL_ROOT` | Root log level | `INFO` |

---

## 📈 Performance Tuning

### JVM Options

Already configured in render.yaml:
```
JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

**For higher traffic, increase heap:**
```
JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### Database Connection Pool

Configured in application-prod.yml:
- Maximum pool size: 10
- Minimum idle: 5

**For high concurrency:**
```
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=10
```

### Enable Response Compression

Already enabled in application-prod.yml:
- Compresses JSON, XML, HTML, CSS, JS
- Reduces bandwidth by 60-80%

---

## 🧪 Testing Production Deployment

### Smoke Tests

```bash
# Set your API URL
API_URL="https://your-app-url.onrender.com"

# 1. Health check
curl $API_URL/actuator/health

# 2. Register user
curl -X POST $API_URL/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "TestPass123!",
    "tenantName": "Test Corp"
  }'

# 3. Login
TOKEN=$(curl -X POST $API_URL/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }' | jq -r '.data.accessToken')

# 4. Create task
curl -X POST $API_URL/api/v1/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Task",
    "description": "Testing production deployment",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59"
  }'

# 5. Get dashboard stats
curl -X GET $API_URL/api/v1/dashboard/stats \
  -H "Authorization: Bearer $TOKEN"
```

### Load Testing (Optional)

Use Apache Bench or Artillery:
```bash
# Install artillery
npm install -g artillery

# Create test scenario
artillery quick --duration 60 --rate 10 $API_URL/actuator/health
```

---

## 📋 Deployment Checklist

### Pre-Deployment
- ✅ Code pushed to Git repository
- ✅ All tests passing locally
- ✅ Environment variables configured
- ✅ Database backup strategy in place
- ✅ CORS origins updated
- ✅ Custom domain DNS configured (if using)

### During Deployment
- ✅ Monitor build logs
- ✅ Verify health checks pass
- ✅ Check database migrations
- ✅ Test API endpoints
- ✅ Verify file uploads work

### Post-Deployment
- ✅ Create initial admin user
- ✅ Test authentication flow
- ✅ Verify all features working
- ✅ Set up monitoring alerts
- ✅ Document API URL
- ✅ Update frontend configuration

---

## 🆘 Support and Resources

### Render Documentation
- **Main Docs**: https://render.com/docs
- **Java/Spring Boot**: https://render.com/docs/deploy-spring-boot
- **PostgreSQL**: https://render.com/docs/databases
- **Disks**: https://render.com/docs/disks

### Community Support
- **Render Community**: https://community.render.com
- **Discord**: https://render.com/discord
- **Status Page**: https://status.render.com

### Application Support
- **API Documentation**: `https://your-app-url/swagger-ui.html`
- **GitHub Issues**: Your repository issues page
- **Email**: Your support email

---

## 🎉 Deployment Complete!

Your Task Management API is now deployed on Render with:
- ✅ High availability (2 instances)
- ✅ Managed PostgreSQL database
- ✅ Persistent file storage (10GB)
- ✅ Automatic SSL/HTTPS
- ✅ Health monitoring
- ✅ Auto-scaling capability
- ✅ Zero-downtime deployments

**Next Steps:**
1. Share API URL with frontend team
2. Configure monitoring alerts
3. Set up backup strategy
4. Plan scaling based on usage

---

**🚀 Happy Deploying!**
