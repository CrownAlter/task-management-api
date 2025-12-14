package com.adewunmi.task_management_api.security;

import com.adewunmi.task_management_api.entity.User;
import com.adewunmi.task_management_api.exception.ResourceNotFoundException;
import com.adewunmi.task_management_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This method is not used directly in our implementation
        // We use loadUserByUsernameAndTenantId instead
        throw new UnsupportedOperationException("Use loadUserByUsernameAndTenantId instead");
    }

    @Transactional
    public UserDetails loadUserByUsernameAndTenantId(String email, Long tenantId) {
        User user = userRepository.findActiveByEmailAndTenantId(email, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return CustomUserDetails.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return CustomUserDetails.create(user);
    }

}
