package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class ServiceProviderMetadataFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = ServiceProviderMetadata

        DOMAIN_NAME = 'serviceProviderMetadata'

        POST_VALUES = [
                name: "KEY",
                value: "VALUE",
                serviceProvider: 1
        ]
        EXPECTED_POST_VALUES = [
                name: "KEY",
                value: "VALUE",
                serviceProvider: [
                        id: 1
                ],
                version: 0
        ]

        PUT_VALUES  = [
                name: "KEY",
                value: "VALUE2",
                serviceProvider: 1,
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "KEY",
                value: "VALUE2",
                serviceProvider: [
                        id: 1
                ],
                version: 1
        ]
    }
}
