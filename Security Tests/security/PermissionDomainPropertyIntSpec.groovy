package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty

@Integration
@Rollback
class PermissionDomainPropertyIntSpec extends AbstractIntegrationSpec {
    def setup () {
        if (!DOMAIN_TYPE) {
            DOMAIN_TYPE = PermissionDomainProperty
            Domain domain = Domain.build()
            DomainProperty nameProperty = DomainProperty.build()
            Role role = new Role(name: java.util.UUID.randomUUID().toString()).save(flush: true, failOnError: true)
            Permission permission = new Permission(
                    domain: domain,
                    httpMethod: "GET",
                    role: role
            ).save(flush: true, failOnError: true)
            UPDATE_PROPERTIES = [
                    permission    : permission,
                    domainProperty: nameProperty,
                    version       : 1
            ]
        }
    }
}
