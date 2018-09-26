package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.ConfigurableConstraints as CC
import org.piercecountywa.pac.security.Securable

class Condition implements Securable, Cachable {

  Domain domain
  String name
  String description
  String query

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static belongsTo = [domain: Domain]

  static constraints = {
    name          CC.configurableConstraints
    domain        CC.configurableConstraints
    description   CC.configurableConstraints
    query         CC.configurableConstraints
  }

  static mapping = {
    table 'pac_condition_query'
    cache true
    sort 'name'
  }
}
