package org.piercecountywa.pac.security

/**
 * A PermissionSet is a default set of permissions for a role.
 * Uses PermissionUtil.
 */
enum PermissionSet {

    Basic("None", { String roleName ->
        PermissionUtil.addBasicPermissions(roleName)
    }),

    ReadOnly("Read Only", { String roleName ->
        PermissionUtil.addReadOnlyPermissions(roleName)
    }),

    ReadAllWriteExceptTypes("Read All / Write All (except types)", { String roleName ->
        PermissionUtil.addReadAllWriteExceptTypesPermissions(roleName)
    }),

    ReadWrite("Read / Write", { String roleName ->
        PermissionUtil.addReadWritePermissions(roleName)
    })

    final String name

    private final Closure action

    PermissionSet(String name, Closure action) {
        this.name = name
        this.action = action
    }

    void generatePermissions(String roleName) {
        action.call(roleName)
    }

}

