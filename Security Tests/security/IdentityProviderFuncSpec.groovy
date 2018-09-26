package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class IdentityProviderFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = IdentityProvider

        POST_VALUES = [
                name: "adfsIdentityProvider",
                xml: "<security></security>"
        ]
        EXPECTED_POST_VALUES = [
                name: "adfsIdentityProvider",
                xml: "<security></security>",
                version: 0
        ]

        PUT_VALUES  = [
                name: "adfsIdentityProviders",
                xml: "<security></security>",
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "adfsIdentityProviders",
                xml: "<security></security>",
                version: 1
        ]
    }
}
