package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class PacUserRoleIntSpec extends AbstractIntegrationSpec {
    def setupSpec () {
        DOMAIN_TYPE = PacUserRole
        UPDATE_PROPERTIES = [version : 1]
    }
}
