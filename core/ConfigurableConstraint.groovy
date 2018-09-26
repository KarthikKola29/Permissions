package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.Securable

class ConfigurableConstraint implements Securable, Cachable {
  
  /**
   * The display name
   */
  String name

  /**
   * The name of the built in constraint or potentially the Groovy script
   * for a custom constraint
   */
  String value

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static constraints = {
    name  nullable: false, blank: false
    value nullable: true, blank: true
  }

  static mapping = {
    table 'pac_configurable_constraint'
    cache true
    sort 'name'
  }

}

