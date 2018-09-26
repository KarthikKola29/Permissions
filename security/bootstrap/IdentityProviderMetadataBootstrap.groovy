package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.IdentityProvider
import org.piercecountywa.pac.security.IdentityProviderMetadata

class IdentityProviderMetadataBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    IdentityProviderMetadata
  }

  private static Map defaults = [
      "domain"            : ["canPost": null, "canDelete": null, "codeTable": false, "form": false, "workflow": false],
      "name"              : ["canGet": null, "canPut": null],
      "value"             : ["canGet": null, "canPut": null],
      "identityProvider"  : ["canGet": null, "canPut": null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "IdentityProviderMetadata",
        "description": "Metadata for the Identity Provider",
        "displayName": "Identity Provider Metadata",
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

    Domain metadataDomain = Domain.findByName("IdentityProviderMetadata")

    def name = new DomainProperty(
        "name": 'name',
        "groupName": "gp",
        "description": "The name of the key",
        "columnLabel": "Name",
        "fieldLabel": "Name",
        "type": Domain.findByName("String"),
        "canGet": defaults.name.canGet,
        "canPut": defaults.name.canPut
    )
    metadataDomain.addToDomainProperties(name).save(flush: true, failOnError: true)

    def value = new DomainProperty(
        "name": 'value',
        "groupName": "gp",
        "description": "The value",
        "columnLabel": "Value",
        "fieldLabel": "Value",
        "type": Domain.findByName("String"),
        "canGet": defaults.value.canGet,
        "canPut": defaults.value.canPut
    )
    metadataDomain.addToDomainProperties(value).save(flush: true, failOnError: true)

    def identityProvider = new DomainProperty(
        "name": 'identityProvider',
        "groupName": "gp",
        "description": "The Identity Provider",
        "columnLabel": "Identity Provider",
        "fieldLabel": "Identity Provider",
        "type": Domain.findByName("IdentityProvider"),
        "canGet": defaults.identityProvider.canGet,
        "canPut": defaults.identityProvider.canPut
    )
    metadataDomain.addToDomainProperties(identityProvider).save(flush: true, failOnError: true)

  }

  @Override
  void generateData() {
    new IdentityProviderMetadata(
        name: "KEY",
        value: "VALUE",
        identityProvider: IdentityProvider.findByName("sas.co.pierce.wa.us")
    ).save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'IdentityProviderMetadata')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'IdentityProviderMetadata')
  }

  @Override
  void generateConstraints() {
    Domain identityProviderDomain = Domain.findByName("IdentityProviderMetadata")

    DomainProperty name = DomainProperty.findByDomainAndName(identityProviderDomain, "name")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty value = DomainProperty.findByDomainAndName(identityProviderDomain, "value")
    value.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    value.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")
  }

  @Override
  List<Class> getDependencies() {
    [Domain, IdentityProvider]
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
