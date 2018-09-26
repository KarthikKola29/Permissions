package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.Permission
import org.piercecountywa.pac.security.PermissionDomainProperty

class PermissionDomainPropertyBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    PermissionDomainProperty
  }

  @Override
  List<Class> getDependencies() {
    [DomainProperty, Permission]
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
      "domain"        : ["canPost": null, "canDelete": null, "form": false, "workflow": false],
      "permission"    : ["canGet": null, "canPut": null],
      "domainProperty": ["canGet": null, "canPut": null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "PermissionDomainProperty",
        "description": "A permission domain property",
        "displayName": "Permission Domain Property",
        "displayTemplate": '${domainDisplayName} ${id}',
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save()
  }

  @Override
  void generateDomainProperties() {
    Domain permissionDomainPropertyDomain = Domain.findByName("PermissionDomainProperty")

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
    permissionDomainPropertyDomain.addToDomainProperties(permission).save()
    permission.save()

    def domainProperty = new DomainProperty(
        "name": 'domainProperty',
        "groupName": "gp",
        "description": "The domain property",
        "columnLabel": "Domain Property",
        "fieldLabel": "Domain Property",
        "type": Domain.findByName("DomainProperty"),
        "canGet": defaults.domainProperty.canGet,
        "canPut": defaults.domainProperty.canPut,
    )
    permissionDomainPropertyDomain.addToDomainProperties(domainProperty).save()
    domainProperty.save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'PermissionDomainProperty')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'PermissionDomainProperty')
  }

  @Override
  void generateConstraints() {
  }

  @Override
  void generateData() {
  }
}