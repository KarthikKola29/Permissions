package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class IdentityProviderMetadataIntSpec extends AbstractIntegrationSpec {
    def setup () {
        DOMAIN_TYPE = IdentityProviderMetadata
        DEFAULT_PROPERTIES = [
            name: "test",
            value: "true",
            identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            version: 1
        ]
        UPDATE_PROPERTIES = [
            name: "trust",
            value: "false",
            identityProvider: IdentityProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            version: 1
        ]
    }
}
