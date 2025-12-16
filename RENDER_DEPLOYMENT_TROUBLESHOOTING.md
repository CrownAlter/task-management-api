# 🔧 Render Deployment Troubleshooting Guide

## Error: Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory'

This error indicates that Spring Boot cannot create the JPA EntityManagerFactory, which means **it cannot connect to the database**.

---

## 🔍 Root Causes & Solutions

### 1. ⚠️ **Database Not Created Yet** (Most Common)

**Problem**: The web service tries to start before the PostgreSQL database is ready.

**Solution**:
1. **Check Database Status in Render Dashboard**:
   - Go to your Render Dashboard
   - Look for `task-management-db` in your databases
   - Ensure status is **"Available"** (not "Creating" or "Failed")

2. **If Database is Still Creating**:
   - Wait 5-10 minutes for database to finish provisioning
   - Render will automatically restart your web service once database is ready

3. **If Database Creation Failed**:
   - Delete the failed database
   - Redeploy using Blueprint (render.yaml)
   - Or manually create database first, then web service

---

### 2. ⚠️ **Missing JWT_SECRET Environment Variable**

**Problem**: The application requires JWT_SECRET but it's not set in Render Dashboard.

**Solution**:
1. **Generate JWT Secret**:
   ```bash
   openssl rand -base64 64
   ```

2. **Add to Render Dashboard**:
   - Go to your web service in Render Dashboard
   - Click **"Environment"** tab
   - Add new variable:
     - Key: `JWT_SECRET`
     - Value: (paste the generated secret from above)
   - Click **"Save Changes"**
   - Service will automatically redeploy

---

### 3. ⚠️ **Database Connection Variables Not Set**

**Problem**: Database environment variables (DB_HOST, DB_PASSWORD, etc.) are not properly linked.

**Solution**:

**If using Blueprint (render.yaml)** - ✅ Recommended:
- These variables are automatically set via `fromDatabase` property
- Ensure database name in render.yaml matches actual database name
- Database should be named: `task-management-db`

**If manual deployment**:
1. Go to your database in Render Dashboard
2. Copy the **Internal Connection String**
3. In your web service → Environment tab, set:
   ```
   DATABASE_URL=<internal-connection-string>
   ```

Or set individual variables:
```
DB_HOST=<from database dashboard>
DB_PORT=5432
DB_NAME=task_management_db
DB_USER=task_admin
DB_PASSWORD=<from database dashboard>
```

---

### 4. ⚠️ **SSL Connection Issues**

**Problem**: PostgreSQL SSL connection failing on Render.

**Solution**: Already fixed in the updated configuration:
- Connection URL includes: `sslmode=require`
- Timeouts configured: `connectTimeout=30&socketTimeout=30`

If still failing, try:
1. In Render Dashboard → Web Service → Environment
2. Add variable:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://DB_HOST:5432/DB_NAME?sslmode=require&sslrootcert=/etc/ssl/certs/ca-certificates.crt
   ```

---

### 5. ⚠️ **Flyway Migration Failures**

**Problem**: Flyway trying to run migrations on database that's not ready.

**Solution**: Already fixed with:
- `connect-retries: 10` - Retries connection 10 times
- `connect-retries-interval: 10` - 10 seconds between retries
- This gives database ~100 seconds to become ready

---

## 📋 Step-by-Step Deployment Checklist

### Before Deploying:

- [ ] Code pushed to GitHub repository
- [ ] `render.yaml` is in repository root
- [ ] All deployment files updated (from previous fixes)

### During Blueprint Deployment:

1. [ ] **Create Blueprint Deployment**:
   - Render Dashboard → New → Blueprint
   - Connect GitHub repository
   - Click "Apply"

2. [ ] **Wait for Database Creation** (5-10 minutes):
   - Monitor database status in dashboard
   - Should show "Available" when ready

3. [ ] **Add JWT_SECRET**:
   ```bash
   # Generate secret
   openssl rand -base64 64
   ```
   - Go to web service → Environment
   - Add `JWT_SECRET` with generated value
   - Click "Save Changes"

4. [ ] **Monitor Deployment Logs**:
   - Click on web service
   - Click "Logs" tab
   - Watch for successful startup

5. [ ] **Verify Deployment**:
   - Visit: `https://your-app-name.onrender.com/actuator/health`
   - Should return: `{"status":"UP"}`

---

## 🔍 How to Check Logs on Render

### View Real-time Logs:
1. Go to Render Dashboard
2. Click your web service
3. Click **"Logs"** tab
4. Logs update in real-time

### Look For These Success Messages:
```
✓ HikariPool-1 - Start completed
✓ Flyway Community Edition by Redgate
✓ Successfully validated X migrations
✓ Tomcat started on port 8080
✓ Started TaskManagementApiApplication
```

### Common Error Messages:

**"Connection refused"**:
- Database not ready yet
- Wait and check database status

**"Authentication failed"**:
- DB_PASSWORD incorrect
- Check database credentials in dashboard

**"Database does not exist"**:
- Database creation failed
- Recreate database manually

**"JWT_SECRET not set"**:
- Add JWT_SECRET environment variable
- Use `openssl rand -base64 64` to generate

---

## 🐛 Current Issues Fixed

The configuration has been updated with these fixes:

### ✅ Database Connection Improvements:
- Added connection timeouts to datasource URL
- Increased Flyway retry attempts: 3 → 10
- Added HikariCP initialization timeout: 60 seconds
- Fixed PostgreSQL LOB handling issue

### ✅ Enhanced Logging:
- HikariCP logging: DEBUG (see connection pool issues)
- Flyway logging: DEBUG (see migration issues)
- JDBC logging: DEBUG (see SQL connection issues)
- Application logging: DEBUG (see startup issues)

### ✅ Better Error Handling:
- Graceful degradation if database is slow to start
- Retry mechanisms for transient connection failures
- Detailed logging for troubleshooting

---

## 🚀 Quick Fix Commands

### 1. Redeploy Web Service:
- Render Dashboard → Web Service → Manual Deploy → "Deploy latest commit"

### 2. Force Clear Cache and Rebuild:
- Render Dashboard → Web Service → Settings → "Clear build cache & deploy"

### 3. View Environment Variables:
- Render Dashboard → Web Service → Environment
- Verify all required variables are set:
  - `JWT_SECRET` ✓
  - `SPRING_PROFILES_ACTIVE=prod` ✓
  - Database variables (auto-set via Blueprint) ✓

### 4. Check Database Connection:
- Render Dashboard → Database → Connect → "External Connection"
- Use provided command to test connection locally

---

## 📞 Still Having Issues?

### Check These:

1. **Database Status**: Must be "Available"
2. **JWT_SECRET**: Must be set (required)
3. **Logs**: Check for specific error messages
4. **Region**: Web service and database should be in same region (oregon)
5. **Plan**: Both should be on "free" plan

### Debug Mode:

To see more detailed logs, add these environment variables:
```
LOGGING_LEVEL_ROOT=DEBUG
LOGGING_LEVEL_COM_ADEWUNMI=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
```

---

## ✅ Verification Steps

Once deployed successfully:

### 1. Health Check:
```bash
curl https://your-app-name.onrender.com/actuator/health
```
Expected response:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 2. API Documentation:
Visit: `https://your-app-name.onrender.com/swagger-ui.html`

### 3. Test Registration:
```bash
curl -X POST https://your-app-name.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "tenantId": "test-tenant"
  }'
```

---

## 📚 Additional Resources

- [Render Web Services Docs](https://render.com/docs/web-services)
- [Render PostgreSQL Docs](https://render.com/docs/databases)
- [Render Blueprint Spec](https://render.com/docs/blueprint-spec)
- [Spring Boot Common Application Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)

---

*Last Updated: 2024 - Comprehensive troubleshooting for Render deployment*
