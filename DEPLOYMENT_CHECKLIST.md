# 📋 Render Deployment Checklist

Use this checklist to ensure a smooth deployment to Render.

---

## Pre-Deployment Checklist

### Code Preparation
- [ ] All features tested locally
- [ ] All tests passing (`mvn test`)
- [ ] Build successful (`mvn clean package`)
- [ ] No compilation errors
- [ ] Application runs locally with prod profile
- [ ] Database migrations tested
- [ ] File upload tested locally

### Repository Preparation
- [ ] Code committed to Git
- [ ] Pushed to main branch
- [ ] `render.yaml` present in root
- [ ] `Dockerfile` present in root
- [ ] `.dockerignore` configured
- [ ] Build scripts (`build.sh`, `start.sh`) executable

### Configuration Files
- [ ] `application-prod.yml` updated with environment variables
- [ ] `.env.example` documented with all variables
- [ ] CORS origins configured
- [ ] Logging levels appropriate for production
- [ ] Health check endpoint enabled

### Documentation
- [ ] `RENDER_DEPLOYMENT_GUIDE.md` reviewed
- [ ] API documentation up to date
- [ ] Environment variables documented
- [ ] Deployment procedures documented

---

## Render Setup Checklist

### Account Setup
- [ ] Render account created
- [ ] Git repository connected
- [ ] Payment method added (if using paid plans)

### Database Setup
- [ ] PostgreSQL service created
- [ ] Database name: `task_management_db`
- [ ] Region selected (closest to users)
- [ ] Plan selected (Standard recommended)
- [ ] Connection details saved

### Web Service Setup
- [ ] Web service created
- [ ] Correct repository selected
- [ ] Branch set to `main`
- [ ] Build command configured
- [ ] Start command configured
- [ ] Health check path set: `/actuator/health`
- [ ] Number of instances set (2 recommended)

### Environment Variables
- [ ] `DB_HOST` set from database
- [ ] `DB_PORT` set (5432)
- [ ] `DB_NAME` set (task_management_db)
- [ ] `DB_USER` set from database
- [ ] `DB_PASSWORD` set from database
- [ ] `JWT_SECRET` generated (64 characters)
- [ ] `JWT_EXPIRATION` set (86400000)
- [ ] `JWT_REFRESH_EXPIRATION` set (604800000)
- [ ] `FILE_UPLOAD_DIR` set (/opt/render/project/src/uploads)
- [ ] `FILE_MAX_SIZE` set (10485760)
- [ ] `CORS_ALLOWED_ORIGINS` set with frontend URLs
- [ ] `SPRING_PROFILES_ACTIVE` set to `prod`
- [ ] `SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE` set (10)
- [ ] `SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE` set (5)
- [ ] `LOGGING_LEVEL_ROOT` set (INFO)
- [ ] `LOGGING_LEVEL_COM_ADEWUNMI` set (INFO)
- [ ] `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` set (health,info,metrics)
- [ ] `SERVER_COMPRESSION_ENABLED` set (true)
- [ ] `JAVA_OPTS` set for JVM tuning

### Persistent Storage
- [ ] Disk created (uploads)
- [ ] Mount path: `/opt/render/project/src/uploads`
- [ ] Size: 10GB (adjust as needed)
- [ ] Disk attached to web service

---

## Deployment Checklist

### Initial Deployment
- [ ] Service deployed (click "Apply" or "Create")
- [ ] Build logs monitored
- [ ] Build completed successfully
- [ ] Health checks passing
- [ ] All instances running

### Verification
- [ ] Application URL accessible
- [ ] Health endpoint responding: `/actuator/health`
- [ ] Swagger UI accessible: `/swagger-ui.html`
- [ ] API endpoints responding
- [ ] Database connection working
- [ ] File upload working

### First User Setup
- [ ] Admin user registered via API
- [ ] Login successful
- [ ] JWT tokens working
- [ ] Task creation working
- [ ] Dashboard accessible

---

## Post-Deployment Checklist

### Security
- [ ] HTTPS enabled (automatic on Render)
- [ ] SSL certificate valid
- [ ] CORS properly configured
- [ ] JWT secret is secure (64+ characters)
- [ ] Database password is strong
- [ ] No sensitive data in logs
- [ ] Environment variables not exposed

### Monitoring
- [ ] Render metrics enabled
- [ ] Health checks configured
- [ ] Log streaming working
- [ ] Alert notifications set up
- [ ] External monitoring configured (optional)

### Performance
- [ ] Application response time < 200ms
- [ ] Database queries optimized
- [ ] Connection pool working
- [ ] File uploads fast
- [ ] No memory leaks

### Custom Domain (Optional)
- [ ] Custom domain added in Render
- [ ] DNS CNAME record created
- [ ] SSL certificate provisioned
- [ ] Domain accessible
- [ ] HTTPS redirect working

### Documentation
- [ ] API URL documented
- [ ] Admin credentials saved securely
- [ ] Database credentials saved securely
- [ ] Deployment date recorded
- [ ] Team notified of deployment

---

## Testing Checklist

### Smoke Tests
- [ ] User registration working
- [ ] User login working
- [ ] Token refresh working
- [ ] Task creation working
- [ ] Task retrieval working
- [ ] Task update working
- [ ] Task deletion working
- [ ] Comment creation working
- [ ] File upload working
- [ ] File download working
- [ ] Dashboard stats working
- [ ] User profile working
- [ ] Password change working

### Integration Tests
- [ ] Frontend integration working
- [ ] Mobile app integration working (if applicable)
- [ ] Third-party integrations working (if applicable)

### Load Tests (Optional)
- [ ] Application handles expected load
- [ ] Database performs well under load
- [ ] No bottlenecks identified
- [ ] Auto-scaling tested (if configured)

---

## Rollback Plan

### If Deployment Fails
- [ ] Rollback procedure documented
- [ ] Previous version available
- [ ] Database backup available
- [ ] Rollback tested in staging

### Rollback Steps
1. [ ] In Render dashboard, go to Deploys
2. [ ] Find previous successful deployment
3. [ ] Click "Rollback to this version"
4. [ ] Verify rollback successful
5. [ ] Notify team

---

## Maintenance Checklist

### Weekly
- [ ] Check application logs for errors
- [ ] Monitor CPU and memory usage
- [ ] Check database performance
- [ ] Review disk space usage
- [ ] Check for security updates

### Monthly
- [ ] Review and optimize slow queries
- [ ] Clean up old log files
- [ ] Review and update dependencies
- [ ] Test backup restoration
- [ ] Review scaling needs

### Quarterly
- [ ] Security audit
- [ ] Performance review
- [ ] Cost optimization review
- [ ] Documentation update
- [ ] Disaster recovery drill

---

## Emergency Contacts

### Render Support
- **Dashboard**: https://dashboard.render.com
- **Docs**: https://render.com/docs
- **Community**: https://community.render.com
- **Status**: https://status.render.com

### Application Support
- **GitHub Issues**: [Your repo]/issues
- **Email**: your-support-email@example.com
- **On-Call**: [Phone number]

---

## Quick Commands Reference

### View Logs
```bash
# Via Render dashboard
Dashboard → Service → Logs

# Via Render CLI (if installed)
render logs
```

### Restart Service
```bash
# Via Render dashboard
Dashboard → Service → Manual Deploy → Clear build cache & deploy
```

### Manual Deployment
```bash
# Push to main branch
git push origin main

# Or via Render dashboard
Dashboard → Service → Manual Deploy
```

### Check Health
```bash
curl https://your-app-url.onrender.com/actuator/health
```

### Generate JWT Secret
```bash
openssl rand -base64 64 | tr -d '\n'
```

---

## Notes and Observations

### Deployment Date:
_________________

### Deployment Duration:
_________________

### Issues Encountered:
_________________________________________________________________________________
_________________________________________________________________________________

### Resolution Steps:
_________________________________________________________________________________
_________________________________________________________________________________

### Team Members Involved:
_________________________________________________________________________________

### Next Steps:
_________________________________________________________________________________
_________________________________________________________________________________

---

## Sign-Off

- [ ] Deployment completed successfully
- [ ] All checklist items verified
- [ ] Team notified
- [ ] Documentation updated
- [ ] Support team briefed

**Deployed By**: _______________________  
**Date**: _______________________  
**Time**: _______________________  
**Version**: _______________________  

---

**✅ Deployment checklist completed!**
