package org.piercecountywa.pac.security

import grails.util.Holders
import groovy.util.logging.Log
import org.hibernate.Session
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty

/**
 * A set of Permission utilities.
 * Used by the PermissionSet enum and the RoleService
 */
@Log
class PermissionUtil {

    static void removeAllPermissions(String roleName) {
        log.info "Removing all permissions for ${roleName}."
        Role role = Role.findByName(roleName)
        Set<Permission> permissions = role.permissions
        log.info "   Found ${permissions.size()} permissions!"
        log.info "   Removing permission conditions and permission domain properties..."
        permissions.each { Permission permission ->
            PermissionCondition.where {
                permission == permission
            }.deleteAll()
            PermissionDomainProperty.where {
                permission == permission
            }.deleteAll()
        }
        log.info "   Removing permissions..."
        Permission.where {
            role == role
        }.deleteAll()
        log.info "   Permissions removed!"
    }

    static void addBasicPermissions(String role) {
        log.info "Adding basic permissions for ${role}"
        long start = System.currentTimeMillis()
        BootstrapUtils.addDomainPropertyPermissions(["GET"], role, 'Domain')
        BootstrapUtils.addDomainPropertyPermissions(["GET"], role, 'DomainProperty')
        long end = System.currentTimeMillis()
        log.info "Added basic permissions in ${(end-start)/1000.0} seconds"
    }

    static void addReadWritePermissions(String role){
        log.info "Adding read write permissions for ${role}"
        long start = System.currentTimeMillis()
        BootstrapUtils.addFullDomainPropertyPermissions(role)
        long end = System.currentTimeMillis()
        log.info "Added read write permissions in ${(end-start)/1000.0} seconds"
    }

    static void addReadOnlyPermissions(String roleName){
        log.info "Adding read only permissions for ${roleName}"
        long start = System.currentTimeMillis()

        Role role = Role.findByName(roleName)

        //build domain permissions
        def domains = Domain.getAll()
        List<Permission> permissionBatch = []
        domains.each { Domain domain ->
            log.fine "   ${domain.name}"
            permissionBatch.add(new Permission("httpMethod": "GET", "domain": domain, "role": role))
            if(permissionBatch.size() > 100) {
                Permission.withTransaction {
                    permissionBatch.each { Permission p ->
                        p.save(failOnError: true)
                    }
                }
                permissionBatch.clear()
            }
        }
        if(permissionBatch.size() > 0) {
            Permission.withTransaction {
                permissionBatch.each { Permission p ->
                    p.save(failOnError: true)
                }
            }
            permissionBatch.clear()
        }

        //add each domain property to the permission
        def domainProperties = DomainProperty.getAll()
        List<PermissionDomainProperty> permissionDomainPropertyBatch = []
        domains.each { Domain domain ->
            def get = Permission.findByHttpMethodAndDomainAndRole("GET", domain, role)
            List<DomainProperty> propertiesForDomain = domainProperties.findAll { it.domain.name == domain.name }
            propertiesForDomain.each { DomainProperty domainProperty ->
                log.fine "   ${domainProperty.name}"
                permissionDomainPropertyBatch.add(new PermissionDomainProperty(permission: get, domainProperty: domainProperty))
                if(permissionDomainPropertyBatch.size() > 100) {
                    PermissionDomainProperty.withTransaction {
                        permissionDomainPropertyBatch.each { PermissionDomainProperty pdp ->
                            pdp.save(failOnError: true)
                        }
                    }
                    permissionDomainPropertyBatch.clear()
                }
            }
        }
        if(permissionDomainPropertyBatch.size() > 0) {
            PermissionDomainProperty.withTransaction {
                permissionDomainPropertyBatch.each { PermissionDomainProperty pdp ->
                    pdp.save(failOnError: true)
                }
            }
            permissionDomainPropertyBatch.clear()
        }
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) Holders.getGrailsApplication().getMainContext().getBean("sessionFactory")
        Session session = sessionFactory.getCurrentSession()
        session.clear()
        long end = System.currentTimeMillis()
        log.info "Added read only permissions in ${(end-start)/1000.0} seconds"
    }

    static void addReadAllWriteExceptTypesPermissions(String roleName){
        log.info "Adding read all write except types permissions for ${roleName}"
        long start = System.currentTimeMillis()
        def domains = Domain.getAll()

        Role role = Role.findByName(roleName)
        List<Permission> permissionBatch = []
        domains.each { Domain domain ->
            log.fine "   ${domain.name} "
            log.fine "      GET"
            // Read
            permissionBatch.add(new Permission("httpMethod": "GET", "domain": domain, "role": role))
            // Write only if not a code table and not a basic typedomainProperty.domain
            if (!domain.codeTable && !BasicType.isBasicType(domain.name)) {
                log.fine "      POST, PUT, DELETE"
                permissionBatch.add(new Permission("httpMethod": "POST",   "domain": domain, "role": role))
                permissionBatch.add(new Permission("httpMethod": "PUT",    "domain": domain, "role": role))
                permissionBatch.add(new Permission("httpMethod": "DELETE", "domain": domain, "role": role))
            }
            if(permissionBatch.size() > 100) {
                Permission.withTransaction {
                    permissionBatch.each { Permission p ->
                        p.save(failOnError: true)
                    }
                }
                permissionBatch.clear()
            }
        }
        if(permissionBatch.size() > 0) {
            Permission.withTransaction {
                permissionBatch.each { Permission p ->
                    p.save(failOnError: true)
                }
            }
            permissionBatch.clear()
        }

        def domainProperties = DomainProperty.getAll()
        List<PermissionDomainProperty> permissionDomainPropertyBatch = []
        domains.each { Domain domain ->
            def get = Permission.findByHttpMethodAndDomainAndRole("GET", domain, role)
            def put = null
            // Write only if not a code table and not a basic typedomainProperty.domain
            if(!domain.codeTable && !BasicType.isBasicType(domain.name)) {
                put = Permission.findByHttpMethodAndDomainAndRole("PUT", domain, role)
            }
            List<DomainProperty> propertiesForDomain = domainProperties.findAll { it.domain.name == domain.name }
            propertiesForDomain.each { DomainProperty domainProperty ->
                log.fine "   ${domainProperty.name} "
                log.fine "      GET"
                permissionDomainPropertyBatch.add(new PermissionDomainProperty(permission: get, domainProperty: domainProperty))
                //if not a code table and not a basic type
                if(put) {
                    log.fine "      PUT"
                    permissionDomainPropertyBatch.add(new PermissionDomainProperty(permission: put, domainProperty: domainProperty))
                }
                if(permissionDomainPropertyBatch.size() > 100) {
                    PermissionDomainProperty.withTransaction {
                        permissionDomainPropertyBatch.each { PermissionDomainProperty pdp ->
                            pdp.save(failOnError: true)
                        }
                    }
                    permissionDomainPropertyBatch.clear()
                }
            }
        }
        if(permissionDomainPropertyBatch.size() > 0) {
            PermissionDomainProperty.withTransaction {
                permissionDomainPropertyBatch.each { PermissionDomainProperty pdp ->
                    pdp.save(failOnError: true)
                }
            }
            permissionDomainPropertyBatch.clear()
        }
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) Holders.getGrailsApplication().getMainContext().getBean("sessionFactory")
        Session session = sessionFactory.getCurrentSession()
        session.clear()
        long end = System.currentTimeMillis()
        log.info "Added read all write except types permissions in ${(end-start)/1000.0} seconds"
    }

}
