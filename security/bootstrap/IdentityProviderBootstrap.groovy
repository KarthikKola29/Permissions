package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.IdentityProvider
import org.piercecountywa.pac.security.PermissionCondition
import org.piercecountywa.pac.security.PermissionDomainProperty

class IdentityProviderBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    IdentityProvider
  }

  private static Map defaults = [
      "domain"             : ["canPost" : null, "canDelete" : null, "codeTable": false, "form": false, "workflow": false],
      "xml"                : ["canGet"  : null, "canPut" : null],
      "name"               : ["canGet"  : null, "canPut" : null],
      "securityMetadata"   : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "IdentityProvider",
        "description": "",
        "displayName":"Identity Provider",
        "displayTemplate": '${name}',
        "codeTable": defaults.domain.codeTable,
        "canPost": defaults.domain.canPost,
        "canDelete": defaults.domain.canDelete,
        "form": defaults.domain.form,
        "workflow": defaults.domain.workflow
    ).save(flush: true, failOnError: true)
  }

  @Override
  void generateDomainProperties() {

    Domain identityProviderDomain = Domain.findByName("IdentityProvider")

    def xml = new DomainProperty(
        "name": "xml",
        "groupName": "gp",
        "description": "The xml blob.",
        "columnLabel": "XML",
        "fieldLabel": "XML",
        "type": Domain.findByName("String"),
        "canGet": defaults.xml.canGet,
        "canPut": defaults.xml.canPut
    )

    identityProviderDomain.addToDomainProperties(xml).save(flush: true, failOnError: true)

    def name = new DomainProperty(
        "name": "name",
        "groupName": "gp",
        "description": "The name",
        "columnLabel": "Name",
        "fieldLabel": "Name",
        "type": Domain.findByName("String"),
        "canGet": defaults.name.canGet,
        "canPut": defaults.name.canPut
    )

    identityProviderDomain.addToDomainProperties(name).save(flush: true, failOnError: true)

    def securityMetadata = new DomainProperty(
        "name": "securityMetadata",
        "groupName": "gp",
        "description": "The security metadata",
        "columnLabel": "Security Metadata",
        "fieldLabel": "Security Metadata",
        "type": Domain.findByName("String"),
        "canGet": defaults.securityMetadata.canGet,
        "canPut": defaults.securityMetadata.canPut
    )
    identityProviderDomain.addToDomainProperties(securityMetadata).save(flush: true, failOnError: true)

  }

  @Override
  void generateData() {
    new IdentityProvider(name: "sas.co.pierce.wa.us", xml: "<security></security>").save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'IdentityProvider')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'IdentityProvider')
  }

  @Override
  void generateConstraints() {
    Domain identityProviderDomain = Domain.findByName("IdentityProvider")

    DomainProperty name = DomainProperty.findByDomainAndName(identityProviderDomain, "name")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty xml = DomainProperty.findByDomainAndName(identityProviderDomain, "xml")
    xml.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    xml.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")
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
    false
  }
}
