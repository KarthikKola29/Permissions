package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.Condition
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.Permission
import org.piercecountywa.pac.security.PermissionCondition

class PermissionConditionBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    PermissionCondition
  }

  @Override
  List<Class> getDependencies() {
    [Permission, Condition]
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
      "domain"      : ["canPost" : null, "canDelete" : null, "form": false, "workflow": false],
      "permission"  : ["canGet"  : null, "canPut" : null],
      "condition"   : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "PermissionCondition",
        "description": "A permission condition",
        "displayName":"Permission Condition",
        "displayTemplate": '${domainDisplayName} ${id}',
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save()
  }

  @Override
  void generateDomainProperties() {
    Domain permissionConditionDomain = Domain.findByName("PermissionCondition")

    def permission = new DomainProperty(
        "name": 'permission',
        "groupName": "gp",
        "description": "The permission",
        "columnLabel": "Permission",
        "fieldLabel": "Permission",
        "type": Domain.findByName("Permission"),
        "canGet": defaults.permission.canGet,
        "canPut": defaults.permission.canPut,
    )
    permissionConditionDomain.addToDomainProperties(permission).save()
    permission.save()

    def condition = new DomainProperty(
        "name": 'condition',
        "groupName": "gp",
        "description": "The condition",
        "columnLabel": "Condition",
        "fieldLabel": "Condition",
        "type": Domain.findByName("Condition"),
        "canGet": defaults.condition.canGet,
        "canPut": defaults.condition.canPut,
    )
    permissionConditionDomain.addToDomainProperties(condition).save()
    condition.save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'PermissionCondition')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'PermissionCondition')
  }

  @Override
  void generateConstraints() {
  }

  @Override
  void generateData() {
  }
}