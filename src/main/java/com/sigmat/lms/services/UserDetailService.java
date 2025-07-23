package com.sigmat.lms.services;

import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.UserPrincipal;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username); 

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            throw new UsernameNotFoundException("User has no roles assigned");
        }

        Collection<GrantedAuthority> authorities = roles.stream()
                .flatMap(role -> {
                    Collection<GrantedAuthority> roleAuthorities = role.getAuthorities();
                    roleAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                    return roleAuthorities.stream();
                })
                .toList();

        return new UserPrincipal(user
        );
    }
}