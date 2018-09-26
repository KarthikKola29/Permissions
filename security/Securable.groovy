package org.piercecountywa.pac.security

/**
 * The Securable trait include transient properties for row level configurable security.
 * All Domains that participate in Configurable Security should implement this trait.
 */
trait Securable {

  boolean canUserDelete = false
  boolean canUserPut = false
  List putProperties = []

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

}