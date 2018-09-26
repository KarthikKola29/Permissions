package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class RoleFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = Role

        POST_VALUES = [
                name: "can pass tests"
        ]
        EXPECTED_POST_VALUES = [
                name: "can pass tests",
                authority: "ROLE_CAN_PASS_TESTS",
                version: 0
        ]

        PUT_VALUES  = [
                name: "still passes",
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "still passes",
                authority: "ROLE_STILL_PASSES",
                version: 1
        ]
    }
}
