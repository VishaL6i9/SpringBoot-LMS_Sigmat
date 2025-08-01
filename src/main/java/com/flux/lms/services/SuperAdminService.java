package com.flux.lms.services;

import com.flux.lms.models.Role;
import com.flux.lms.models.Users;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminService {

    public boolean isSuperAdmin(Users user) {
        return user != null && user.getRoles().contains(Role.SUPER_ADMIN);
    }

    public boolean hasOwnerAccess(Users user) {
        return isSuperAdmin(user);
    }

    public boolean canAccessResource(Users user, String resourceType) {
        if (isSuperAdmin(user)) {
            return true;
        }
        
        // Additional resource-specific checks can be added here
        return false;
    }

    public boolean canModifyUser(Users currentUser, Users targetUser) {
        if (!isSuperAdmin(currentUser)) {
            return false;
        }
        
        // SuperAdmin can modify anyone except other SuperAdmins (unless they are the same user)
        if (targetUser.getRoles().contains(Role.SUPER_ADMIN)) {
            return currentUser.getId().equals(targetUser.getId());
        }
        
        return true;
    }

    public boolean canDeleteUser(Users currentUser, Users targetUser) {
        if (!isSuperAdmin(currentUser)) {
            return false;
        }
        
        // SuperAdmin cannot delete other SuperAdmins
        return !targetUser.getRoles().contains(Role.SUPER_ADMIN);
    }

    public boolean canPromoteToSuperAdmin(Users currentUser) {
        return isSuperAdmin(currentUser);
    }
}