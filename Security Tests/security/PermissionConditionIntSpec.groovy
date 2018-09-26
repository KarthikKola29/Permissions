package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec
import org.piercecountywa.pac.core.Condition
import org.piercecountywa.pac.core.Domain

@Integration
@Rollback
class PermissionConditionIntSpec extends AbstractIntegrationSpec {
    def setup () {
        if (!DOMAIN_TYPE) {
            DOMAIN_TYPE = PermissionCondition
            Domain domain = Domain.build()
            Role role = new Role(name: java.util.UUID.randomUUID().toString()).save(flush: true, failOnError: true)
            Permission permission = new Permission(
                    domain: domain,
                    httpMethod: "GET",
                    role: role
            ).save(flush: true, failOnError: true)
            Condition condition = new Condition(
                    domain: domain,
                    name: 'A Counties',
                    description: "All Counties starting with A",
                    query: "name.startsWith('A')"
            )
            UPDATE_PROPERTIES = [
                    permission: permission,
                    condition : condition,
                    version   : 1
            ]
        }
    }
}
