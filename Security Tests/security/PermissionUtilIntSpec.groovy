package org.piercecountywa.pac.security

import grails.core.DefaultGrailsApplication
import grails.transaction.Rollback
import groovy.util.logging.Log
import org.piercecountywa.testing.AbstractIntegrationSpec
import org.piercecountywa.pac.core.Domain

@Log
class PermissionUtilIntSpec extends AbstractIntegrationSpec {

    def setupSpec () {
        DOMAIN_TYPE = Permission
        UPDATE_PROPERTIES = ["httpMethod": "PUT", "version" : 1]
        grailsApplication = new DefaultGrailsApplication()
    }

    @Rollback
    def "remove all permissions from a role"() {
        setup:
        String roleName = "tester"
        Domain domain = Domain.get(1)

        Role role = new Role(name: roleName).save(flush: true, failOnError: true)
        role.addToPermissions(new Permission(role: role, domain: domain, httpMethod: "GET"   )).save(flush: true, failOnError: true)
        role.addToPermissions(new Permission(role: role, domain: domain, httpMethod: "POST"  )).save(flush: true, failOnError: true)
        role.addToPermissions(new Permission(role: role, domain: domain, httpMethod: "DELETE")).save(flush: true, failOnError: true)

        when:
        assert Permission.findAllByRole(role).size() == 3
        PermissionUtil.removeAllPermissions(roleName)

        then:
        assert Permission.findAllByRole(role).size() == 0
    }

    @Rollback
    def "add basic permissions to a role"() {
        setup:
        String roleName = "tester"
        Domain domain = Domain.get(1)

        Role role = new Role(name: roleName).save(flush: true, failOnError: true)

        when:
        assert Permission.findAllByRole(role).size() == 0
        PermissionUtil.addBasicPermissions(roleName)

        then:
        assert Permission.findAllByRole(role).size() == 2
    }

    @Rollback
    def "add read write permissions to a role"() {
        setup:
        String roleName = "tester"
        Role role = new Role(name: roleName).save(flush: true, failOnError: true)

        when:
        assert Permission.findAllByRole(role).size() == 0
        PermissionUtil.addReadWritePermissions(roleName)
        List<Permission> permissions = Permission.findAllByRole(role)

        then:
        assert permissions.size() > 100
    }

    @Rollback
    def "add read only permissions to a role"() {
        setup:
        String roleName = "tester"
        Role role = new Role(name: roleName).save(flush: true, failOnError: true)

        when:
        assert Permission.findAllByRole(role).size() == 0
        PermissionUtil.addReadWritePermissions(roleName)
        List<Permission> permissions = Permission.findAllByRole(role)

        then:
        assert permissions.size() > 30
    }

    @Rollback
    def "add read all write all except types permissions to a role"() {
        setup:
        String roleName = "tester"
        Role role = new Role(name: roleName).save(flush: true, failOnError: true)

        when:
        assert Permission.findAllByRole(role).size() == 0
        PermissionUtil.addReadAllWriteExceptTypesPermissions(roleName)
        List<Permission> permissions = Permission.findAllByRole(role)

        then:
        assert permissions.size() > 100
    }

}