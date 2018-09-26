package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.AbstractFunctionalSpec

@Integration
class PermissionConditionFuncSpec extends AbstractFunctionalSpec {
  def setupSpec() {

    DOMAIN = PermissionCondition

    ADD_BEFORE_LIST = true

    POST_VALUES = [
        permission: 1,
        condition: 1
    ]
    EXPECTED_POST_VALUES = [
        permission: [
            id: 1
        ],
        condition: [
            id: 1
        ],
        version : 0
    ]

    PUT_VALUES = [
        permission: 2,
        condition: 1,
        version: 0
    ]
    EXPECTED_PUT_VALUES = [
        permission: [
            id: 2
        ],
        condition: [
            id: 1
        ],
        version: 1
    ]
  }
}
