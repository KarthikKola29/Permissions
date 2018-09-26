package org.piercecountywa.pac.core

import org.piercecountywa.pac.core.ConfigurableConstraint
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.security.Securable

class DomainPropertyConstraint implements Securable, Cachable {
	
  DomainProperty domainProperty
  ConfigurableConstraint configurableConstraint
  String value

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static belongsTo = [
    domainProperty: DomainProperty,
    configurableConstraint: ConfigurableConstraint
  ]

  static constraints = {
    domainProperty nullable: false
    configurableConstraint nullable: false
    value nullable: false, blank: false
  }

  static mapping = {
    table 'pac_domain_property_constraint'
    cache true
  }

}

