package org.piercecountywa.pac.core

import org.hibernate.sql.JoinType
import org.piercecountywa.pac.security.Securable

class Domain implements Securable, Cachable {

  String name
  String description
  String displayName
  String displayTemplate
  Boolean codeTable = false
  Boolean canPost
  Boolean canDelete
  Boolean form = false
  Boolean workflow = false

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static hasMany = [
    domainProperties : DomainProperty,
    conditions: Condition,
    expressions: Expression
  ]

  static constraints = {
    name             blank: false, nullable: false
    description      blank: false, nullable: true
    displayName      blank: false, nullable: false
    displayTemplate  blank: false, nullable: true
    codeTable        blank: false, nullable: true
    canPost          blank: false, nullable: true
    canDelete        blank: false, nullable: true
    form             blank: false, nullable: true
    workflow         blank: false, nullable: true
  }

  static mapping = {
    table 'pac_domain'
    cache true
    // domainProperties cache: true // Can't cache domainProperties because it causes n+1 query problems
    conditions       cache: true
    expressions      cache: true
    sort 'name'
  }

  static mappedBy = [domainProperties: 'domain']

  /**
   * Get a Domain by name with all DomainProperties to avoid the n+1 query problem.
   * @param name The Domain name
   * @return A Domain
   */
  static Domain getWithDomainProperties(String name) {
    Domain.createCriteria().get {
      createAlias("domainProperties", "dp", JoinType.LEFT_OUTER_JOIN)
      'eq'('name', name)
      cache true
    }
  }
}
