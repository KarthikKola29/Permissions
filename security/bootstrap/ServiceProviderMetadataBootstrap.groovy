package org.piercecountywa.pac.security.bootstrap

import org.piercecountywa.pac.bootstrap.Bootstrap
import org.piercecountywa.pac.bootstrap.BootstrapUtils
import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.ServiceProvider
import org.piercecountywa.pac.security.ServiceProviderMetadata

class ServiceProviderMetadataBootstrap implements Bootstrap {

  @Override
  Class getDomain() {
    ServiceProviderMetadata
  }

  private static Map defaults = [
      "domain"            : ["canPost": null, "canDelete": null, "codeTable": false, "form": false, "workflow": false],
      "name"              : ["canGet": null, "canPut": null],
      "value"             : ["canGet": null, "canPut": null],
      "serviceProvider"   : ["canGet": null, "canPut": null]
  ]

  @Override
  void generateDomain() {
    new Domain(
        "name": "ServiceProviderMetadata",
        "description": "Metadata for the Service Provider",
        "displayName": "Service Provider Metadata",
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

    Domain metadataDomain = Domain.findByName("ServiceProviderMetadata")

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

    def serviceProvider = new DomainProperty(
        "name": 'serviceProvider',
        "groupName": "gp",
        "description": "The Service Provider",
        "columnLabel": "Service Provider",
        "fieldLabel": "Service Provider",
        "type": Domain.findByName("ServiceProvider"),
        "canGet": defaults.serviceProvider.canGet,
        "canPut": defaults.serviceProvider.canPut
    )
    metadataDomain.addToDomainProperties(serviceProvider).save(flush: true, failOnError: true)

  }

  @Override
  void generateData() {
    new ServiceProviderMetadata(
        name: "KEY",
        value: "VALUE",
        serviceProvider: ServiceProvider.findByName("vm-desktop.co.pierce.wa.us")
    ).save()
  }

  @Override
  void generatePermissions() {
    BootstrapUtils.addDomainPermissions(["POST", "DELETE"], 'admin', 'ServiceProviderMetadata')
    BootstrapUtils.addDomainPropertyPermissions(['GET','PUT'], 'admin', 'ServiceProviderMetadata')
  }

  @Override
  void generateConstraints() {
    Domain serviceProviderDomain = Domain.findByName("ServiceProviderMetadata")

    DomainProperty name = DomainProperty.findByDomainAndName(serviceProviderDomain, "name")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    name.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")

    DomainProperty value = DomainProperty.findByDomainAndName(serviceProviderDomain, "value")
    value.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("blank"), value: "false")
    value.addToConfigurableConstraints(configurableConstraint: ConfigurableConstraint.findByName("nullable"), value: "false")
  }

  @Override
  List<Class> getDependencies() {
    [Domain, ServiceProvider]
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
