package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class PermissionFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = Permission

        POST_VALUES = [
                domain: 1,
                httpMethod: "GET",
                role: 1
        ]
        EXPECTED_POST_VALUES = [
                domain: [
                        id: 1
                ],
                httpMethod: "GET",
                role: [
                        id: 1
                ],
                version: 0
        ]

        PUT_VALUES  = [
                domain: 1,
                httpMethod: "GET",
                role: 1,
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "Work",
                domain: [
                        id: 1
                ],
                httpMethod: "GET",
                role: [
                        id: 1
                ],
                version: 1
        ]
    }
}
