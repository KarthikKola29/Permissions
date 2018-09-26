package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.ConfigurableConstraints as CC
import org.piercecountywa.pac.security.Securable

class Expression implements Securable, Cachable {

    String name
    String description
    String expression
    Domain domain

    static transients = ['canUserDelete', 'canUserPut', 'putProperties']

    static belongsTo = [domain: Domain]

    static constraints = {
        name            blank: false, nullable: false
        description     CC.configurableConstraints
        expression      blank: false, nullable: false
        domain          blank: false, nullable: false
    }

    static mapping = {
        table 'pac_expression'
        cache true
        sort 'name'
    }
}