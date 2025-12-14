package com.adewunmi.task_management_api.multitenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader("X-Tenant-ID");

        try {
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setCurrentTenant(Long.parseLong(tenantId));
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
