package org.piercecountywa.pac.security

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractIntegrationSpec

@Integration
@Rollback
class ServiceProviderIntSpec extends AbstractIntegrationSpec {
    def setupSpec () {
        DOMAIN_TYPE = ServiceProvider
        DEFAULT_PROPERTIES = [name: "test", xml: "<test></test>", version: 1]
        UPDATE_PROPERTIES = [name: "sp", xml: "<security></security>", version: 1]
    }
}
