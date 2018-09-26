package org.piercecountywa.pac.core

import org.piercecountywa.pac.security.Securable
import org.piercecountywa.pac.security.ConfigurableConstraints as CC

class ReportTemplate implements Securable, Cachable {

  String name
  String description
  String content

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static constraints = {
    name          CC.configurableConstraints
    description   CC.configurableConstraints
    content       CC.configurableConstraints
  }

  static mapping = {
    table 'pac_report_template'
    cache true
    content type: "text"
  }
}
