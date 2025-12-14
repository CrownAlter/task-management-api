package com.adewunmi.task_management_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adewunmi.task_management_api.dto.request.LoginRequest;
import com.adewunmi.task_management_api.dto.request.RefreshTokenRequest;
import com.adewunmi.task_management_api.dto.request.RegisterRequest;
import com.adewunmi.task_management_api.dto.response.AuthResponse;
import com.adewunmi.task_management_api.dto.response.UserResponse;
import com.adewunmi.task_management_api.entity.Role;
import com.adewunmi.task_management_api.entity.Tenant;
import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.enums.RoleType;
import com.adewunmi.task_management_api.exception.BadRequestException;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.exception.TenantNotFoundException;
import com.adewunmi.task_management_api.repository.RoleRepository;
import com.adewunmi.task_management_api.repository.TenantRepository;
import com.adewunmi.task_management_api.repository.UserRepository;
import com.adewunmi.task_management_api.security.CustomUserDetails;
import com.adewunmi.task_management_api.security.CustomUserDetailsService;
import com.adewunmi.task_management_api.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if tenant with name already exists
        if (tenantRepository.existsByName(request.getOrganizationName())) {
            throw new BadRequestException("Organization name already exists");
        }

        // Create tenant
        String slug = generateSlug(request.getOrganizationName());
        Tenant tenant = Tenant.builder()
                .name(request.getOrganizationName())
                .slug(slug)
                .active(true)
                .build();
        tenant = tenantRepository.save(tenant);

        // Check if user already exists in this tenant
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenant.getId())) {
            throw new BadRequestException("Email already registered in this organization");
        }

        // Get or create ADMIN role
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(RoleType.ADMIN)
                            .description("Administrator role with full access")
                            .build();
                    return roleRepository.save(role);
                });

        // Create user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .tenant(tenant)
                .active(true)
                .emailVerified(false)
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        // Generate tokens
        CustomUserDetails userDetails = CustomUserDetails.create(user);
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // Build response
        UserResponse userResponse = buildUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find tenant
        Tenant tenant = tenantRepository.findBySlug(request.getTenantSlug())
                .orElseThrow(() -> new TenantNotFoundException("Organization not found"));

        if (!tenant.getActive()) {
            throw new BadRequestException("Organization is inactive");
        }

        // Find user
        User user = userRepository.findActiveByEmailAndTenantId(request.getEmail(), tenant.getId())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getActive()) {
            throw new BadRequestException("User account is inactive");
        }

        // Authenticate
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService
                .loadUserByUsernameAndTenantId(request.getEmail(), tenant.getId());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail() + ":" + tenant.getId(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        // Build response
        UserResponse userResponse = buildUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(userId);

        String newAccessToken = tokenProvider.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));

        return buildUserResponse(user);
    }

    private String generateSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        String slug = baseSlug;
        int counter = 1;

        while (tenantRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    private UserResponse buildUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .roles(roleNames)
                .tenantId(user.getTenant().getId())
                .tenantName(user.getTenant().getName())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

}
