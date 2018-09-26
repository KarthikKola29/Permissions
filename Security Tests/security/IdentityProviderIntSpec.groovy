package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class IdentityProviderIntSpec extends AbstractIntegrationSpec {
    def setupSpec () {
        DOMAIN_TYPE = IdentityProvider
        DEFAULT_PROPERTIES = [name: "test", xml: "<test></test>", version: 1]
        UPDATE_PROPERTIES = [name: "idp", xml: "<security></security>", version: 1]
    }
}
