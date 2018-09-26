package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class IdentityProviderMetadataFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = IdentityProviderMetadata

        DOMAIN_NAME = 'identityProviderMetadata'

        POST_VALUES = [
                name: "KEY",
                value: "VALUE",
                identityProvider: 1
        ]
        EXPECTED_POST_VALUES = [
                name: "KEY",
                value: "VALUE",
                identityProvider: [
                        id: 1
                ],
                version: 0
        ]

        PUT_VALUES  = [
                name: "KEY",
                value: "VALUE2",
                identityProvider: 1,
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "KEY",
                value: "VALUE2",
                identityProvider: [
                        id: 1
                ],
                version: 1
        ]
    }
}
