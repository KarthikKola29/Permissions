package org.piercecountywa.pac.security

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * Filter that ensures the credentials on the current thread are set to the current spring
 * user, provided the current user is NOT the application user (i.e. PARAM2 value), for
 * all requests when:
 *
 * grails.plugin.springsecurity.saml.useDatabaseCredentials = true
 *
 * This happens before application code is triggered, so if a grails service later sets
 * the current thread credentials to a "super user", it can still do so.
 */
@Slf4j
class DatabaseCredentialThreadFilter extends GenericFilterBean {
  @Autowired
  GrailsApplication grailsApplication

  @Autowired
  SpringSecurityService springSecurityService

  @Override
  void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String username = springSecurityService?.principal?.username

    // Only set database credentials if configured to do so, a "user" is "logged in", and
    // the current user is NOT the PARAMS2 value
    Boolean useDatabaseCredentials =
        grailsApplication
            .getConfig()
            .getProperty(SAMLUserDetailsServiceImpl.USE_DATABASE_CREDENTIAL_CONFIG_KEY) &&
        (username && username != System.properties["PARAM2"])
    log.debug("Set database credentials on current thread = $useDatabaseCredentials")

    if(useDatabaseCredentials) {
      def userCredsDataSource = grailsApplication.getMainContext().getBean("userCredsDataSource")

      // Get the password from the system
      String passwordKey = grailsApplication.getConfig().getProperty(SAMLUserDetailsServiceImpl.DATABASE_CREDENTIAL_SYSTEM_PROPERTY_PASSWORD_CONFIG_KEY)
      String password = System.properties[passwordKey].toString()

      // If both username and password are defined, and the username is not the PARAMS2
      // value, set the credentials on the current thread
      if(username && username != System.properties["PARAM2"] && password) {
        log.debug("Setting database credentials on current thread for user: $username")
        userCredsDataSource.setCredentialsForCurrentThread(username, password)
      }
    }

    // Continue processing the rest of the filter chain (whether credentials set or not)
    chain.doFilter(request, response)
  }
}
