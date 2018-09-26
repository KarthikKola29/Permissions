package org.piercecountywa.pac.core

class Audit {
  String action
  String user
  String domainName
  Long domainId
  String data
  Date dateStamp
  Date lastUpdated

  String getEtag() {
    "${id}/${version}"
  }

  static transients = ['canUserPut', 'putProperties']

  static constraints = {
    action     (blank: false, nullable: false)
    user       (blank: false, nullable: false)
    domainName (blank: false, nullable: false)
    domainId   (blank: false, nullable: false)
    data       (blank: false, nullable: false)
    dateStamp  (blank: false, nullable: false)
  }

  static mapping = {
    table: "pac_audit"
    sort "dateStamp"
    cache true
    data type: 'text'
    action index: 'audit_action'
    dateStamp index: 'audit_dateStamp'
    user index: 'audit_user'
    domainName index: 'audit_domainName'
    domainId index: 'audit_domainId'
  }
}