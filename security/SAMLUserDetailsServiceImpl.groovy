package org.piercecountywa.pac.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.piercecountywa.pac.core.PacUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.saml.SAMLCredential
import org.springframework.security.saml.userdetails.SAMLUserDetailsService
import org.springframework.stereotype.Service

@Slf4j
@Service
class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {
  static final String USE_DATABASE_CREDENTIAL_CONFIG_KEY = "grails.plugin.springsecurity.saml.useDatabaseCredentials"
  static final String DATABASE_CREDENTIAL_SYSTEM_PROPERTY_PASSWORD_CONFIG_KEY = "grails.plugin.springsecurity.saml.databaseCredentialSystemPropertyPasswordKey"
  static final String MISCONFIGURED_DATABASE_CREDENTIAL_WARNING_MESSAGE = "Either one of the following configs is missing or falsy:\n" +
      "'grails.plugin.springsecurity.saml.useDatabaseCredentials'\n" +
      "'grails.plugin.springsecurity.saml.databaseCredentialSystemPropertyPasswordKey'\n" +
      "Database credentials will NOT be set."

  SAMLUserDetailsServiceImpl() {
    log.info("Creating a SAMLUserDetailsServiceImpl...")
  }

  Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    log.info("SAMLCredential = " + credential)
    String userID = credential.getNameID().getValue()

    log.info(userID + " is logged in")
    PacUser.withTransaction {
      PacUser pacUser = PacUser.findByUsername(userID)
      if (pacUser) {
        log.info("PacUser = ${pacUser}")
        Set<Role> roles = PacUserRole.findAllByPacUser(pacUser).collect { it.role }
        log.info("Roles=${roles}")
        List<GrantedAuthority> grantedAuthorities = []
        roles.each { Role role ->
          grantedAuthorities.add(new SimpleGrantedAuthority(role.authority))
        }

        def grailsApplication = Holders.getGrailsApplication()
        Boolean useDatabaseCredentials = grailsApplication.getConfig().getProperty(USE_DATABASE_CREDENTIAL_CONFIG_KEY)
        String passwordKey = grailsApplication.getConfig().getProperty(DATABASE_CREDENTIAL_SYSTEM_PROPERTY_PASSWORD_CONFIG_KEY)
        Boolean canApplyDbCredentials = useDatabaseCredentials && passwordKey //both must be defined
        log.info("Apply database credentials? ${canApplyDbCredentials}")

        if(
          (
            grailsApplication.getConfig().hasProperty(USE_DATABASE_CREDENTIAL_CONFIG_KEY) ||
            grailsApplication.getConfig().hasProperty(DATABASE_CREDENTIAL_SYSTEM_PROPERTY_PASSWORD_CONFIG_KEY)
          ) && !canApplyDbCredentials
        ) {
          log.warn(MISCONFIGURED_DATABASE_CREDENTIAL_WARNING_MESSAGE)
        }

        new GrailsUser(
            pacUser.username,
            canApplyDbCredentials ? System.properties[passwordKey] as String : UUID.randomUUID().toString(),
            true, true, true, true,
            grantedAuthorities, pacUser.id)
      } else {
        log.info("Can't find PacUser: ${userID}!")
        throw new UsernameNotFoundException("Can't find user named: ${userID}!")
      }
    }

  }
}
