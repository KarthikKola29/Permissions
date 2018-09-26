package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class RoleIntSpec extends AbstractIntegrationSpec {
    def setupSpec () {
        DOMAIN_TYPE = Role
        UPDATE_PROPERTIES = [name: "Admin", version : 1]
    }
}
