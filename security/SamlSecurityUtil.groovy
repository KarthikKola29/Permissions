package org.piercecountywa.pac.security

import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.opensaml.saml2.metadata.provider.MetadataProvider
import org.opensaml.xml.parse.ParserPool
import org.piercecountywa.pac.core.Settings
import org.piercecountywa.pac.core.TenantType
import org.springframework.core.io.Resource
import org.springframework.security.saml.metadata.ExtendedMetadata
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate
import org.springframework.security.saml.metadata.MetadataManager
import org.springframework.util.StreamUtils

import javax.sql.DataSource
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.nio.charset.Charset
import org.w3c.dom.Document
import org.w3c.dom.Element

@Slf4j
class SamlSecurityUtil {

  /**
   * Create a SamlSecurity Domain with IdentityProvider and ServiceProvider from Spring Resources
   * @param identityProviderName The identity provider name
   * @param identityProviderResource The identity provider Resource: grailsApplication.mainContext.getResource("classpath:resources/ip.xml")
   * @param serviceProviderName The service provider name
   * @param serviceProviderResource The service provider Resource: grailsApplication.mainContext.getResource("classpath:resources/" + spFile)
   */
  static void createSamlSecurity(String identityProviderName, Resource identityProviderResource, String serviceProviderName, Resource serviceProviderResource) {

    log.info "Creating SAML Security Domains from Resources: ${identityProviderName} @ ${identityProviderResource} and ${serviceProviderName} @ ${serviceProviderResource}"

    String ipXmlStr = StreamUtils.copyToString(identityProviderResource.inputStream, Charset.defaultCharset())
    log.trace "${ipXmlStr}"

    IdentityProvider ip = new IdentityProvider(
        xml: ipXmlStr,
        name: identityProviderName
    ).save()
    log.info "Identity Provider = ${ip}"
    [
        metadataTrustCheck: false
    ].each { String name, Object value ->
      IdentityProviderMetadata metadata = new IdentityProviderMetadata(name: name, value: value, identityProvider: ip).save()
      log.info "Identity Provider Metadata = ${metadata}"
    }
    ip.refresh()

    String spXmlStr = StreamUtils.copyToString(serviceProviderResource.inputStream, Charset.defaultCharset())
    log.trace "${spXmlStr}"

    ServiceProvider sp = new ServiceProvider(
        xml: spXmlStr,
        name: serviceProviderName
    ).save()
    log.info "Service Provider = ${sp}"
    [
        local: true,
        requireArtifactResolveSigned: false,
        requireLogoutRequestSigned: false,
        requireLogoutResponseSigned: false,
        idpDiscoveryEnabled: false,
        metadataTrustCheck: false
    ].each { String name, Object value ->
      ServiceProviderMetadata metadata = new ServiceProviderMetadata(name: name, value: value, serviceProvider: sp).save()
      log.info "Service Provider Metadata = ${metadata}"
    }
    sp.refresh()

    SamlSecurity saml = new SamlSecurity(
        name: "Pierce County SAML",
        identityProvider: ip,
        serviceProvider: sp,
        tenantType: Settings.getTenantType()
    ).save()
    log.info "SamlSecurity = ${saml}"

  }

  /**
   *
   * @param tenantType The TenantType: Settings.getTenantType()
   * @param parserPool:  grailsApplication.mainContext.getBean('parserPool')
   * @param metadataManager: grailsApplication.mainContext.getBean('metadata')
   */
  static void configureSamlSecurity(TenantType tenantType, ParserPool parserPool, MetadataManager metadataManager) {

    // Find the SamlSecurity Domain instance by TenantType
    SamlSecurity security = SamlSecurity.findByTenantType(tenantType)
    if(!security) {
      throw new Exception("Invalid SAML Security configuration for tenant: ${Settings.getTenantType()}")
    }

    // provider is either an IdentityProvider or a ServiceProvider with xml and securityMetadata properties
    Closure createSamlSecurityBean = { Object provider, String alias ->

      DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder()
      Document doc = builder.parse(new ByteArrayInputStream(provider.xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
      Element element = doc.documentElement

      List delegateMetadata = ["metadataTrustCheck"]

      ExtendedMetadata extendedMetadata = new ExtendedMetadata()
      provider.securityMetadata.each { def md ->
        if (!(md.name in delegateMetadata)) {
          def value = md.value
          if (value.equals("true") || value.equals("false")) {
            value = Boolean.parseBoolean(md.value)
          }
          extendedMetadata."${md.name}" = value
        }
      }
      if (provider instanceof IdentityProvider) {
        extendedMetadata.alias = alias
      }

      MetadataProvider metadataProvider = new org.opensaml.saml2.metadata.provider.DOMMetadataProvider(element)
      metadataProvider.setParserPool(parserPool)
      ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(
          metadataProvider,
          extendedMetadata
      )
      provider.securityMetadata.each { def md ->
        if (md.name in delegateMetadata) {
          def value = md.value
          if (value.equals("true") || value.equals("false")) {
            value = Boolean.parseBoolean(md.value)
          }
          extendedMetadataDelegate."${md.name}" = value
        }
      }

      extendedMetadataDelegate
    }

    def ipBean = createSamlSecurityBean(security.identityProvider, security.name)
    def spBean = createSamlSecurityBean(security.serviceProvider,  security.name)

    //grab the entityID from the xml in the database
    String spName = new XmlSlurper().parseText(security.serviceProvider.xml).@entityID.toString()

    log.info "-----------------------------------------------------------------"
    log.info "Configuring SAML Security (${security.tenantType})"
    log.info "Removing all providers..."
    metadataManager.providers.each { metadataManager.removeMetadataProvider(it) }
    log.info "Adding providers from database..."
    metadataManager.addMetadataProvider(ipBean)
    metadataManager.addMetadataProvider(spBean)
    metadataManager.setHostedSPName(spName) //override sp name from the sp.xml file
    log.info "Refreshing metadata..."
    metadataManager.refreshMetadata()
    log.info "-----------------------------------------------------------------"

  }

  static void deleteSamlConfiguration(DataSource dataSource) {
    Sql sql = new Sql(dataSource)
    try {
      [
          "delete from pac_identity_provider_metadata",
          "delete from pac_saml_security",
          "delete from pac_identity_provider",
          "delete from pac_service_provider_metadata",
          "delete from pac_service_provider",
      ].each { String query ->
        sql.execute(query);
      }
    } finally {
      sql.close()
    }
  }

}
