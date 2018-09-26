package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.Permission
import org.piercecountywa.pac.security.Role

class RoleBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    Role
  }

  @Override
  List<Class> getDependencies() {
    []
  }

  @Override
  List<Class> getNonHardDependencies() {
    [Permission]
  }

  @Override
  boolean isRequired() {
    true
  }

  private Map defaults = [
      "domain"      : ["canPost" : null, "canDelete" : null, "codeTable" : false, "form": false, "workflow": false],
      "name"        : ["canGet"  : null, "canPut" : null],
      "authority"   : ["canGet"  : null, "canPut" : null],
      "permissions" : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "Role",
        "description": "A property within a domain",
        "displayName":"Role",
        "displayTemplate": '${domainDisplayName} ${id}',
        "codeTable": defaults.domain.codeTable,
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save(flush: true)
  }

  @Override
  void generateDomainProperties() {
    Domain roleDomain = Domain.findByName("Role")

    def name = new DomainProperty(
        "name": 'name',
        "groupName": "gp",
        "description": "The role name",
        "columnLabel": "Role Name",
        "fieldLabel": "Role Name",
        "type": Domain.findByName("String"),
        "canGet": defaults.name.canGet,
        "canPut": defaults.name.canPut,
    )
    roleDomain.addToDomainProperties(name).save(flush: true)
    name.save(flush: true)

    def authority = new DomainProperty(
        "name": 'authority',
        "groupName": "gp",
        "description": "The role authority",
        "columnLabel": "Authority",
        "fieldLabel": "Authority",
        "type": Domain.findByName("String"),
        "canGet": defaults.authority.canGet,
        "canPut": defaults.authority.canPut,
    )
    roleDomain.addToDomainProperties(authority).save(flush: true)
    authority.save(flush: true)

    def permission = new DomainProperty(
        "name": 'permissions',
        "groupName": "gp",
        "description": "The permissions assigned to role",
        "columnLabel": "Permissions",
        "fieldLabel": "Permissions",
        "type": Domain.findByName("Permission"),
        "canGet": defaults.permissions.canGet,
        "canPut": defaults.permissions.canPut,
        "collection": true
    )
    roleDomain.addToDomainProperties(permission).save(flush: true)
    permission.save(flush: true)
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'Role')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'Role')
  }

  @Override
  void generateConstraints() {
  }

  @Override
  void generateData() {
  }
}