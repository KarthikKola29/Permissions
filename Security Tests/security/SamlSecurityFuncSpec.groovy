package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class SamlSecurityFuncSpec extends AbstractFunctionalSpec {
    def setupSpec(){

        DOMAIN = SamlSecurity

        POST_VALUES = [
                name: "SAML XXX",
                identityProvider: 1,
                serviceProvider: 1,
                tenantType: "PRODUCTION"
        ]
        EXPECTED_POST_VALUES = [
                name: "SAML XXX",
                identityProvider: [id: 1],
                serviceProvider: [id: 1],
                tenantType: [enumType:"org.piercecountywa.pac.core.TenantType", name:"PRODUCTION"],
                version: 0
        ]

        PUT_VALUES  = [
                name: "SAML XXX",
                identityProvider: 1,
                serviceProvider: 1,
                tenantType: "PRODUCTION",
                version: 0
        ]
        EXPECTED_PUT_VALUES = [
                name: "SAML XXX",
                identityProvider: [id: 1],
                serviceProvider: [id: 1],
                tenantType: [enumType:"org.piercecountywa.pac.core.TenantType", name:"PRODUCTION"],
                version: 1
        ]
    }
}
