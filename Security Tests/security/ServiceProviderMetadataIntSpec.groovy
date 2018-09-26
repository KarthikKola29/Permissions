package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class ServiceProviderMetadataIntSpec extends AbstractIntegrationSpec {
    def setup () {
        DOMAIN_TYPE = ServiceProviderMetadata
        DEFAULT_PROPERTIES = [
            name: "test",
            value: "true",
            serviceProvider: ServiceProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            version: 1
        ]
        UPDATE_PROPERTIES = [
            name: "trust",
            value: "false",
            serviceProvider: ServiceProvider.build([name: "idp", xml: "<security></security>", version: 1]),
            version: 1
        ]
    }
}
