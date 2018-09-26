package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.Permission
import org.piercecountywa.pac.security.PermissionCondition
import org.piercecountywa.pac.security.PermissionDomainProperty

class PermissionBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    Permission
  }

  @Override
  List<Class> getDependencies() {
    [Domain, PermissionDomainProperty, PermissionCondition]
  }

  @Override
  List<Class> getNonHardDependencies() {
    []
  }

  @Override
  boolean isRequired() {
    true
  }

  private Map defaults = [
      "domain"           : ["canPost" : null, "canDelete" : null, "codeTable" : false, "form": false, "workflow": false],
      "domainAssc"       : ["canGet"  : null, "canPut" : null],
      "httpMethod"       : ["canGet"  : null, "canPut" : null],
      "domainProperties" : ["canGet"  : null, "canPut" : null],
      "role"             : ["canGet"  : null, "canPut" : null],
      "conditions"       : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "Permission",
        "description": "A permission",
        "displayName":"Permission",
        "displayTemplate": '${domainDisplayName} ${id}',
        "codeTable": defaults.domain.codeTable,
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save()
  }

  @Override
  void generateDomainProperties() {
    Domain permissionDomain = Domain.findByName("Permission")

    def domainAssc = new DomainProperty(
        "name": 'domain',
        "groupName": "gp",
        "description": "The domain",
        "columnLabel": "Domain",
        "fieldLabel": "Domain",
        "type": Domain.findByName("Domain"),
        "canGet": defaults.domainAssc.canGet,
        "canPut": defaults.domainAssc.canPut,
    )
    permissionDomain.addToDomainProperties(domainAssc).save()
    domainAssc.save()

    def domainProperties = new DomainProperty(
        "name": 'domainProperties',
        "groupName": "gp",
        "description": "The domain properties",
        "columnLabel": "Domain Properties",
        "fieldLabel": "Domain Properties",
        "type": Domain.findByName("PermissionDomainProperty"),
        "canGet": defaults.domainProperties.canGet,
        "canPut": defaults.domainProperties.canPut,
        "collection": true
    )
    permissionDomain.addToDomainProperties(domainProperties).save()
    domainProperties.save()

    def role = new DomainProperty(
        "name": 'role',
        "groupName": "gp",
        "description": "The role",
        "columnLabel": "Role",
        "fieldLabel": "Role",
        "type": Domain.findByName("String"),
        "canGet": defaults.role.canGet,
        "canPut": defaults.role.canPut,
    )
    permissionDomain.addToDomainProperties(role).save()
    role.save()

    def conditions = new DomainProperty(
        "name": 'conditions',
        "groupName": "gp",
        "description": "The conditions",
        "columnLabel": "Conditions",
        "fieldLabel": "Conditions",
        "type": Domain.findByName("PermissionCondition"),
        "canGet": defaults.conditions.canGet,
        "canPut": defaults.conditions.canPut,
        "collection": true
    )
    permissionDomain.addToDomainProperties(conditions).save()
    conditions.save()

    def httpMethod = new DomainProperty(
        "name": 'httpMethod',
        "groupName": "gp",
        "description": "The http method",
        "columnLabel": "Http Method",
        "fieldLabel": "Http Method",
        "type": Domain.findByName("String"),
        "canGet": defaults.httpMethod.canGet,
        "canPut": defaults.httpMethod.canPut,
    )
    permissionDomain.addToDomainProperties(httpMethod).save()
    httpMethod.save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'Permission')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'Permission')
  }

  @Override
  void generateConstraints() {
  }

  @Override
  void generateData() {
  }
}