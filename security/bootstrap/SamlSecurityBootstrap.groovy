package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.core.Settings
import org.piercecountywa.pac.security.IdentityProvider
import org.piercecountywa.pac.security.SamlSecurity
import org.piercecountywa.pac.security.ServiceProvider

class SamlSecurityBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    SamlSecurity
  }

  private static Map defaults = [
      "domain"           : ["canPost" : null, "canDelete" : null, "codeTable": false, "form": false, "workflow": false],
      "name"             : ["canGet"  : null, "canPut" : null],
      "identityProvider" : ["canGet"  : null, "canPut" : null],
      "serviceProvider"  : ["canGet"  : null, "canPut" : null],
      "tenantType"       : ["canGet"  : null, "canPut" : null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "SamlSecurity",
        "description": "",
        "displayName":"SAML Authentication",
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

    Domain samlSecurityDomain = Domain.findByName("SamlSecurity")

    def name = new DomainProperty(
        "name": "name",
        "groupName": "gp",
        "description": "The name of the Saml Security",
        "columnLabel": "Name",
        "fieldLabel": "Name",
        "type": Domain.findByName("String"),
        "canGet": defaults.name.canGet,
        "canPut": defaults.name.canPut
    )
    samlSecurityDomain.addToDomainProperties(name).save(flush: true, failOnError: true)

    def identityProvider = new DomainProperty(
        "name": "identityProvider",
        "groupName": "gp",
        "description": "The identity provider for the Saml Security",
        "columnLabel": "Identity Provider",
        "fieldLabel": "Identity Provider",
        "type": Domain.findByName("IdentityProvider"),
        "canGet": defaults.identityProvider.canGet,
        "canPut": defaults.identityProvider.canPut
    )
    samlSecurityDomain.addToDomainProperties(identityProvider).save(flush: true, failOnError: true)

    def serviceProvider = new DomainProperty(
        "name": "serviceProvider",
        "groupName": "gp",
        "description": "The service provider for the Saml Security",
        "columnLabel": "Service Provider",
        "fieldLabel": "Service Provider",
        "type": Domain.findByName("ServiceProvider"),
        "canGet": defaults.serviceProvider.canGet,
        "canPut": defaults.serviceProvider.canPut
    )
    samlSecurityDomain.addToDomainProperties(serviceProvider).save(flush: true, failOnError: true)

    def tenantType = new DomainProperty(
        "name": "tenantType",
        "groupName": "gp",
        "description": "The tenant type for the Saml Security",
        "columnLabel": "Tenant Type",
        "fieldLabel": "Tenant Type",
        "type": Domain.findByName("String"),
        "canGet": defaults.tenantType.canGet,
        "canPut": defaults.tenantType.canPut
    )
    samlSecurityDomain.addToDomainProperties(tenantType).save(flush: true, failOnError: true)

  }

  @Override
  void generateData() {
    new SamlSecurity(
        name: "Pierce County SAML",
        identityProvider: IdentityProvider.findByName("sas.co.pierce.wa.us"),
        serviceProvider: ServiceProvider.findByName("vm-desktop.co.pierce.wa.us"),
        tenantType: Settings.getTenantType()
    ).save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'SamlSecurity')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'SamlSecurity')
  }

  @Override
  void generateConstraints() {
    Domain identityProviderDomain = Domain.findByName("SamlSecurity")

    DomainProperty name = DomainProperty.findByDomainAndName(identityProviderDomain, "name")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty identityProvider = DomainProperty.findByDomainAndName(identityProviderDomain, "identityProvider")
    identityProvider.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    identityProvider.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty serviceProvider = DomainProperty.findByDomainAndName(identityProviderDomain, "serviceProvider")
    serviceProvider.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    serviceProvider.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty tenantType = DomainProperty.findByDomainAndName(identityProviderDomain, "tenantType")
    tenantType.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    tenantType.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")
  }

  @Override
  List<Class> getDependencies() {
    [Domain, IdentityProvider, ServiceProvider]
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
