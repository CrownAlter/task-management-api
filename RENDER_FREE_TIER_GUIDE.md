# 🆓 Render FREE Tier Deployment Guide

## ✅ This Configuration is 100% FREE!

Your `render.yaml` is now configured to use Render's completely free tier with **no payment required**.

---

## 📊 What You Get (FREE)

### Web Service (Free Plan)
- **Cost**: $0/month
- **Resources**: 512 MB RAM, 0.1 CPU
- **Hours**: 750 hours/month (shared across all free services)
- **Behavior**: Spins down after 15 minutes of inactivity
- **Cold Start**: 30-60 seconds when service wakes up
- **Storage**: Ephemeral only (no persistent disk)

### PostgreSQL Database (Free Plan)
- **Cost**: $0/month for **90 days only**
- **Resources**: 256 MB RAM, 1 GB storage
- **Connections**: Max 97 connections
- **Backups**: None (manual backup recommended)
- **After 90 days**: Must upgrade to paid plan or lose data

---

## ⚠️ FREE Tier Limitations

### 1. **Service Spin Down**
- Your app automatically sleeps after 15 minutes of no activity
- First request after sleep takes 30-60 seconds (cold start)
- Subsequent requests are fast
- **Workaround**: Use a service like [UptimeRobot](https://uptimerobot.com/) to ping your app every 14 minutes

### 2. **No Persistent File Storage**
- Files uploaded are stored in `/tmp` (temporary)
- **Files are LOST when service restarts or spins down**
- **Solutions**:
  - Upgrade to paid plan ($7/month) for persistent disk
  - Use external service: AWS S3, Cloudinary, Imgur, etc.
  - Disable file upload feature for now

### 3. **Database Expiry**
- Free PostgreSQL database expires after **90 days**
- You'll receive email warnings before expiry
- **Options after 90 days**:
  - Upgrade to Starter plan ($7/month)
  - Export data and migrate to another free service
  - Create a new free database (fresh 90 days)

### 4. **750 Hours/Month Limit**
- Shared across ALL your free Render services
- ~31 days × 24 hours = 744 hours per month
- If you run multiple free services, they share this limit
- Exceeding limit pauses all free services

### 5. **Performance**
- Limited to 512 MB RAM and 0.1 CPU
- Slower than paid tiers but sufficient for demos/testing
- Not recommended for production with high traffic

---

## 🚀 Deployment Steps (100% Free)

### 1. Prerequisites
- GitHub account
- Render account (free signup at [render.com](https://render.com))

### 2. Push Code to GitHub
```bash
git add .
git commit -m "Configure for Render free tier"
git push origin main
```

### 3. Deploy on Render

#### Option A: Blueprint Deploy (Recommended)
1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New"** → **"Blueprint"**
3. Connect your GitHub repository
4. Select the repository
5. Render will read `render.yaml` and create:
   - Web Service (FREE)
   - PostgreSQL Database (FREE for 90 days)
6. Click **"Apply"**

#### Option B: Manual Deploy
1. **Create Database First**:
   - Click **"New"** → **"PostgreSQL"**
   - Name: `task-management-db`
   - Plan: **Free**
   - Click **"Create Database"**

2. **Create Web Service**:
   - Click **"New"** → **"Web Service"**
   - Connect your repository
   - Plan: **Free**
   - Environment: **Docker**
   - Add environment variables (see below)

### 4. Set Environment Variables
In Render Dashboard → Your Web Service → Environment:

**Required:**
```bash
JWT_SECRET=<generate-with-command-below>
```

**Generate JWT_SECRET:**
```bash
openssl rand -base64 64
```

**Optional (update as needed):**
```bash
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

### 5. Monitor Deployment
- Watch the build logs in Render Dashboard
- First deployment takes 5-10 minutes
- Once deployed, service will be available at: `https://your-app-name.onrender.com`

### 6. Test Your API
```bash
# Health check
curl https://your-app-name.onrender.com/actuator/health

# API documentation (Swagger)
# Visit: https://your-app-name.onrender.com/swagger-ui.html
```

---

## 🔧 Configuration Summary

### Files Updated for Free Tier:
- ✅ `render.yaml` - Set to `plan: free` for both web and database
- ✅ `render.yaml` - Persistent disk commented out (not available on free)
- ✅ `render.yaml` - FILE_UPLOAD_DIR changed to `/tmp/uploads` (ephemeral)
- ✅ `application-prod.yml` - Updated file upload path with warnings
- ✅ HikariCP pool reduced to 3 max connections (conservative for free tier)

---

## 💡 Tips for Free Tier Usage

### 1. Keep Service Awake
Use [UptimeRobot](https://uptimerobot.com/) (also free):
- Create HTTP monitor
- URL: `https://your-app-name.onrender.com/actuator/health`
- Interval: 14 minutes
- Prevents spin down during business hours

### 2. File Upload Strategy
Since persistent storage isn't available on free tier:
- **Option 1**: Disable file uploads temporarily
- **Option 2**: Use [Cloudinary](https://cloudinary.com/) free tier (10GB storage)
- **Option 3**: Use [AWS S3](https://aws.amazon.com/s3/) free tier (5GB for 12 months)

### 3. Database Management
- **Export regularly**: Download backups before 90-day expiry
- **Use pgAdmin** or `pg_dump` to backup data
- Set calendar reminder for day 80 to plan migration/upgrade

### 4. Monitor Usage
- Check Render Dashboard for hours used
- If close to 750 hours, consider paid plan or pausing services

---

## 📈 When to Upgrade?

Consider upgrading to paid plans if:
- You need persistent file storage → **Starter Plan ($7/month)**
- You need 24/7 uptime (no spin down) → **Starter Plan ($7/month)**
- You need more resources/better performance → **Standard Plan ($25/month)**
- Database free trial expired → **Database Starter ($7/month)**

---

## 🆘 Troubleshooting

### Service Won't Start
1. Check build logs in Render Dashboard
2. Verify `JWT_SECRET` is set in environment variables
3. Ensure database is created and linked

### Cold Start Too Slow
- Expected on free tier (30-60 seconds)
- Use UptimeRobot to prevent spin down
- Or upgrade to paid plan for always-on service

### File Uploads Not Working
- Remember: Files in `/tmp` are ephemeral on free tier
- Use external storage service or upgrade to paid plan

### Database Connection Issues
1. Verify database is in same region as web service
2. Check database connection strings in environment variables
3. Review connection pool settings (reduced to 3 for free tier)

---

## 📚 Additional Resources

- [Render Free Tier Docs](https://render.com/docs/free)
- [Render Pricing](https://render.com/pricing)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)
- [PostgreSQL on Render](https://render.com/docs/databases)

---

## ✅ Summary

Your application is now configured for **100% free deployment** on Render with:
- ✅ No credit card required
- ✅ No automatic charges
- ✅ Free web service (with limitations)
- ✅ Free database (90 days)
- ✅ Perfect for learning, demos, and portfolios

**Total Cost: $0/month** 🎉

---

*Last Updated: 2024 - Configured for Render Free Tier*
