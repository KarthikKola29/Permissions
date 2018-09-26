package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.core.PacUser
import org.piercecountywa.pac.security.PacUserRole
import org.piercecountywa.pac.security.Role

class PacUserRoleBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    PacUserRole
  }

  @Override
  List<Class> getDependencies() {
    [Domain, PacUser, Role]
  }

  @Override
  List<Class> getNonHardDependencies() {
    []
  }

  @Override
  boolean isRequired() {
    false
  }

  private Map defaults = [
      "domain"  : ["canPost" : null, "canDelete" : null, "codeTable": false, "form": false, "workflow": false],
      "pacUser" : ["canGet"  : null, "canPut" : null],
      "role"    : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "PacUserRole",
        "description": "Join table between PacUser and Role",
        "displayName":"PacUserRole",
        "displayTemplate": '${domainDisplayName}',
        "codeTable": defaults.domain.codeTable,
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save()
  }

  @Override
  void generateDomainProperties() {
    Domain pacUserRoleDomain = Domain.findByName("PacUserRole")

    def pacUser = new DomainProperty(
        "name": 'pacUser',
        "groupName": "gp",
        "description": "PacUser",
        "columnLabel": "PacUser",
        "fieldLabel": "PacUser",
        "type": Domain.findByName("PacUser"),
        "canGet": defaults.pacUser.canGet,
        "canPut": defaults.pacUser.canPut
    )
    pacUserRoleDomain.addToDomainProperties(pacUser).save()
    pacUser.save()

    def role = new DomainProperty(
        "name": 'role',
        "groupName": "gp",
        "description": "Role of PacUser",
        "columnLabel": "Role",
        "fieldLabel": "Role",
        "type": Domain.findByName("Role"),
        "canGet": defaults.role.canGet,
        "canPut": defaults.role.canPut
    )
    pacUserRoleDomain.addToDomainProperties(role).save()
    role.save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'PacUserRole')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'PacUserRole')
  }

  @Override
  void generateConstraints() {
  }

  @Override
  void generateData() {
  }
}