package org.piercecountywa.pac.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SamlDatabaseConfiguration {

  @Bean
  DatabaseCredentialThreadFilter databaseCredentialThreadFilter() {
    return new DatabaseCredentialThreadFilter()
  }
}
