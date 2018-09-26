package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class PermissionDomainPropertyFuncSpec extends AbstractFunctionalSpec {
  def setupSpec() {

    DOMAIN = PermissionDomainProperty

    POST_VALUES = [
        permission: 1,
        domainProperty: 1
    ]
    EXPECTED_POST_VALUES = [
        permission: [
            id: 1
        ],
        domainProperty: [
            id: 1
        ],
        version : 0
    ]

    PUT_VALUES = [
        permission: 1,
        domainProperty: 2,
        version: 0
    ]
    EXPECTED_PUT_VALUES = [
        permission: [
            id: 1
        ],
        domainProperty: [
            id: 2
        ],
        version: 1
    ]
  }

}
