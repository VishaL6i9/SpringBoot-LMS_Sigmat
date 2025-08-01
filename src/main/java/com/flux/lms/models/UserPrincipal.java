package com.flux.lms.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Users user;

    public UserPrincipal(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles(); 
        if (roles == null || roles.isEmpty()) {
            return List.of(); 
        }

        return roles.stream()
                .flatMap(role -> {
                    Collection<GrantedAuthority> roleAuthorities = role.getAuthorities();
                    roleAuthorities.add(new SimpleGrantedAuthority(role.getRoleName())); 
                    return roleAuthorities.stream();
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); 
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }

    public Users getUser() {
        return user;
    }

    public Long getId() { return user.getId();} 
}