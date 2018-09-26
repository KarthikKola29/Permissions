package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.Securable

class DomainProperty implements Securable, Cachable {

  String name
  String groupName
  String description
  String columnLabel
  String fieldLabel
  Domain type

  Boolean canGet
  Boolean canPut

  Boolean collection = false

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static hasMany = [
    configurableConstraints: DomainPropertyConstraint
  ]

  static belongsTo = [domain: Domain]
  
  static constraints = {
    name           blank: false, nullable: false
    groupName      blank: false, nullable: false
    description    blank: false, nullable: true
    type           blank: false, nullable: false
    columnLabel    blank: false, nullable: false
    fieldLabel     blank: false, nullable: false
    canGet         blank: false, nullable: true
    canPut         blank: false, nullable: true
    collection     blank: false, nullable: true
  }

  static mapping = {
    table 'pac_domain_property'
    sort 'name'
  }
}
