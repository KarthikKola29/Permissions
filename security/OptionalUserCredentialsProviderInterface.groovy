package org.piercecountywa.pac.security

/**
 * This interface needs to be implemented if you plan on using configurable security but currently your
 * datasource uses the logged in user to access the database.  You will need to swap your database credentials
 * for a purposed account super user for configurable security to work.
 *
 * example of how linx implemented methods:
 * void setSuperUserOnThread(){
 *    userCredsDataSource.setCredentialsForCurrentThread(System.properties.superUser, System.properties.superUserPassword)
 * }
 * void setSuperUserOnThread(){
 *    userCredsDataSource.setCredentialsForCurrentThread(springSecurityService.principal.username, springSecurityService.principal.password)
 * }
 * Created by lgrass1 on 4/24/18.
 */
interface OptionalUserCredentialsProviderInterface {
  void setSuperUserOnThread()

  void setOriginalUserOnThread()
}