package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec
import org.piercecountywa.testing.util.IntegrationUtils
import org.piercecountywa.pac.core.TenantType
import spock.lang.Shared
import spock.lang.Specification

import static org.piercecountywa.testing.util.IntegrationUtils.deleteDomain
import static org.piercecountywa.testing.util.IntegrationUtils.generateDomain
import static org.piercecountywa.testing.util.IntegrationUtils.generateDomain
import static org.piercecountywa.testing.util.IntegrationUtils.getDomain
import static org.piercecountywa.testing.util.IntegrationUtils.getDomainCount
import static org.piercecountywa.testing.util.IntegrationUtils.getDomainCount
import static org.piercecountywa.testing.util.IntegrationUtils.getDomainWithTransaction
import static org.piercecountywa.testing.util.IntegrationUtils.updateDomain
import static org.piercecountywa.testing.util.IntegrationUtils.validatePersistedDomain

@Integration
@Rollback
class SamlSecurityIntSpec extends Specification {

    //used to grab datasource config properties such as username
    @Shared grailsApplication
    @Shared String USERNAME

    /** REQUIRED FOR EACH DOMAIN TEST **/
    @Shared Object DOMAIN_TYPE
    @Shared Map UPDATE_PROPERTIES
    @Shared Map DEFAULT_PROPERTIES = [:]

    def setup () {
        DOMAIN_TYPE = SamlSecurity
        USERNAME = grailsApplication.config.dataSource.username
        DEFAULT_PROPERTIES = [
            name: "test",
            identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            serviceProvider: ServiceProvider.build([name: "sp", xml: "<vm-desktop></vm-desktop>", version: 1]),
            tenantType: TenantType.PREVIEW,
            version: 1
        ]
        UPDATE_PROPERTIES = [
            name: "trust",
            identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            serviceProvider: ServiceProvider.build([name: "sp", xml: "<vm-desktop></vm-desktop>", version: 1]),
            tenantType: TenantType.PREVIEW,
            version: 1
        ]
    }

    def "INSERT domain"() {
        setup: "Create a Domain with test data"
        Object domainInstance = IntegrationUtils.generateDomain(DOMAIN_TYPE, DEFAULT_PROPERTIES)


        expect: "valid persited Domain"
        validatePersistedDomain(domainInstance, getDomain(DOMAIN_TYPE, domainInstance.id))
    }

    def "INSERT multiple domains"() {
        setup: "Create Multiple Domains (unique)"
        List<Object> domainInstances = [
            IntegrationUtils.generateDomain(DOMAIN_TYPE, [
                name: "trust",
                identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
                serviceProvider: ServiceProvider.build([name: "sp", xml: "<vm-desktop></vm-desktop>", version: 1]),
                tenantType: TenantType.PREVIEW,
                version: 1
            ]),
            IntegrationUtils.generateDomain(DOMAIN_TYPE, [
                name: "trust",
                identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
                serviceProvider: ServiceProvider.build([name: "sp", xml: "<vm-desktop></vm-desktop>", version: 1]),
                tenantType: TenantType.PRODUCTION,
                version: 1
            ])
        ]

        expect: "Validate persisted Domains"
        validatePersistedDomain(domainInstances[0], getDomain(DOMAIN_TYPE, domainInstances[0].id))
        validatePersistedDomain(domainInstances[1], getDomain(DOMAIN_TYPE, domainInstances[1].id))
        assert domainInstances[0].id != domainInstances[1].id
    }

    def "DELETE domain"() {
        setup: "Create a Domain with test data"
        Object domainInstance = generateDomain(DOMAIN_TYPE, DEFAULT_PROPERTIES)
        int count = getDomainCount(DOMAIN_TYPE)

        when: "When deleting the domain"
        deleteDomain(DOMAIN_TYPE, domainInstance.id)

        then: "The count should be decremented by 1"
        (count - 1) == getDomainCount(DOMAIN_TYPE)
    }

    def "UPDATE domain"() {
        setup: "Create a Domain with test data"
        Object domainInstance = generateDomain(DOMAIN_TYPE, DEFAULT_PROPERTIES)
        when: "Update the domain properties and persist"
        updateDomain(DOMAIN_TYPE, domainInstance.id, UPDATE_PROPERTIES)

        then:"Validate updated domain properties were persisted"
        Object updatedDomainInstance = getDomainWithTransaction(DOMAIN_TYPE, domainInstance.id)
        UPDATE_PROPERTIES.each {key, prop -> prop == updatedDomainInstance[key] }
    }

}
