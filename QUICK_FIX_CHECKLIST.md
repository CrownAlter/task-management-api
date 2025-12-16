# ⚡ Quick Fix Checklist - Database Connection Error

## Error You're Seeing:
```
Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory'
```

## ✅ 3-Step Fix Process:

### Step 1: Check Database Status (1 minute)
1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Find your database: `task-management-db`
3. Check status:
   - ✅ **"Available"** → Good, proceed to Step 2
   - ⏳ **"Creating"** → Wait 5-10 minutes, database is still provisioning
   - ❌ **"Failed"** → Delete and recreate database

---

### Step 2: Add JWT_SECRET (2 minutes)

**Generate Secret:**
```bash
openssl rand -base64 64
```

**Add to Render:**
1. Go to your web service in Render Dashboard
2. Click **"Environment"** tab
3. Click **"Add Environment Variable"**
4. Enter:
   - **Key**: `JWT_SECRET`
   - **Value**: (paste the generated secret)
5. Click **"Save Changes"**
6. Service will automatically redeploy

---

### Step 3: Deploy Fixed Configuration (2 minutes)

**Push the fixes:**
```bash
git add .
git commit -m "Fix database connection issues for Render deployment"
git push origin main
```

Render will automatically detect the push and redeploy with:
- ✅ Better connection retry logic (10 attempts)
- ✅ Longer timeout (100 seconds for database)
- ✅ Enhanced logging for debugging
- ✅ Fixed PostgreSQL configuration

---

## 🔍 Monitor Deployment

After pushing, watch the logs in Render Dashboard:

**Success indicators:**
```
✓ Flyway Community Edition by Redgate
✓ Successfully validated X migrations
✓ HikariPool-1 - Start completed
✓ Tomcat started on port 8080
✓ Started TaskManagementApiApplication
```

**If still failing:**
- Check logs for specific error message
- Verify database is "Available"
- Ensure JWT_SECRET is set
- Read: `RENDER_DEPLOYMENT_TROUBLESHOOTING.md`

---

## 📞 Common Issues

### "Database still creating"
**Wait**: First deployment takes 5-10 minutes for database provisioning

### "Authentication failed"
**Check**: JWT_SECRET is properly set in Environment variables

### "Connection refused"
**Check**: Database and web service are in same region (oregon)

---

## ✅ Verification

Once deployed, test your API:
```bash
curl https://your-app-name.onrender.com/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

---

**Total Time**: ~5-15 minutes (depending on database creation)
