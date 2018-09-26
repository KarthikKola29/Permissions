package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.Securable
import org.piercecountywa.pac.security.ConfigurableConstraints as CC

class Config implements Securable, Cachable {

  String name
  String value
  String description
  String tenantType

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static constraints = {
    name        CC.configurableConstraints
    value       CC.configurableConstraints
    description CC.configurableConstraints
    tenantType  CC.configurableConstraints
  }

  static mapping = {
    table 'pac_configuration'
    cache true
    sort 'name'
  }

}
