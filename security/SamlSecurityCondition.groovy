package org.piercecountywa.pac.security

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

/**
 * A Spring Condition used to turn on SAML Security only if a System Property called
 * SECURITY_TYPE is set to SAML
 */
class SamlSecurityCondition implements Condition {

  @Override
  boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String securityType = System.getProperty("SECURITY_TYPE") ?: "FORM"
    securityType.equalsIgnoreCase("SAML")
  }

}
