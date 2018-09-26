package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec
import org.piercecountywa.pac.core.Condition
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty

@Integration
@Rollback
class PermissionIntSpec extends AbstractIntegrationSpec {
  def setupSpec () {
    DOMAIN_TYPE = Permission
    UPDATE_PROPERTIES = ["httpMethod": "PUT", "version" : 1]
  }

  def "Permission should have many condition"() {
    setup: "Create a Permission with test data and multiple conditions"
    Domain domain = new Domain(name: 'Test', displayName: 'Test', displayTemplate: '${domainDisplayName} ${id}', description: 'Test', canPost: null, canDelete: null).save(flush: true, failOnError: true)
    Role role = new Role(name: "SUPER").save(flush: true, failOnError: true)
    Permission permission = new Permission(role: role, domain: domain, httpMethod: 'GET').save(flush: true, failOnError: true)
    permission.addToConditions(new Condition(domain: domain, name: 'Query', description: 'Simple Query', query: 'id > 0'))

    expect: "valid domain relationship"
    permission.conditions.size() == 1
  }

  def "Permission should have many domainProperties"() {
    setup: "Create a Permission with test data and multiple DomainProperties"
    Domain stringDomain = new Domain(name: 'String', displayName: 'String', displayTemplate: '${domainDisplayName} ${id}',description: 'String', canPost: null, canDelete: null).save(flush: true, failOnError: true)
    Domain domain = new Domain(name: 'Test', displayName: 'Test', displayTemplate: '${domainDisplayName} ${id}',description: 'Test', canPost: null, canDelete: null).save(flush: true, failOnError: true)
    DomainProperty domainProperty = new DomainProperty(domain: domain, name: 'property', groupName: 'test', description: 'test property',
        columnLabel: 'Property', fieldLabel: 'Property', type: stringDomain).save(flush: true, failOnError: true)
    domain.addToDomainProperties(domainProperty)
    Role role = new Role(name: "SUPER").save(flush: true, failOnError: true)
    Permission permission = new Permission(role: role, domain: domain, httpMethod: 'GET').save(flush: true, failOnError: true)
    permission.addToDomainProperties(domainProperty)

    expect: "valid domain relationship"
    permission.domainProperties.size() == 1
  }

  def "Permission should belongTo a role"() {
    setup: "Create a Permission and a add it to a role"
    Domain domain = new Domain(name: 'Test', displayName: 'Test', displayTemplate: '${domainDisplayName} ${id}',description: 'Test', canPost: null, canDelete: null).save(flush: true, failOnError: true)
    Role role = new Role(name: "SUPER").save(flush: true, failOnError: true)
    Permission permission = new Permission(role: role, domain: domain, httpMethod: 'GET').save(flush: true, failOnError: true)
    role.addToPermissions(permission)

    expect: "valid domain relationship"
    permission.role != null
    permission.role.permissions.size() > 0
  }
}
