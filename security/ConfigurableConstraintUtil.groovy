package org.piercecountywa.pac.security

import groovy.util.logging.Log4j
import org.grails.datastore.gorm.validation.constraints.*
import org.piercecountywa.pac.core.Domain
import org.piercecountywa.pac.core.DomainProperty
import org.piercecountywa.pac.core.DomainPropertyConstraint
import org.springframework.validation.Errors

/**
 * A Utility class for validating configurable constraints.  It can be applied to a 
 * Domain class like this:
 * <pre>
 * import org.piercecountywa.fdis.constraint.ConfigurableConstraintUtil as CCU
 *
 * name  validator: { val, obj, errors -> CCU.validate(obj, propertyName, val, errors) }* </pre>
 */
@Log4j
class ConfigurableConstraintUtil {

  static void validate(Object obj, String propertyName, Object value, Errors errors) {
    //log.info "Applying configurable constraints for ${obj.class.simpleName}.${propertyName}"
    Domain domain = Domain.findByName(obj.class.simpleName, [cache: true])
    DomainProperty domainProperty = DomainProperty.findByDomainAndName(domain, propertyName, [cache: true])
    List constraints = DomainPropertyConstraint.findAllByDomainProperty(domainProperty, [cache: true])
    constraints.each { DomainPropertyConstraint dpc ->
      if (dpc.configurableConstraint.name.equalsIgnoreCase("blank")) {
        def parameter = Boolean.parseBoolean(dpc.value)
        def constraint = new BlankConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("creditCard")) {
        def parameter = Boolean.parseBoolean(dpc.value)
        def constraint = new CreditCardConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("email")) {
        def parameter = Boolean.parseBoolean(dpc.value)
        def constraint = new EmailConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value ?: "", errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("inList")) {
        def parameter = Eval.me(dpc.value)
        def constraint = new InListConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("matches")) {
        def parameter = dpc.value
        def constraint = new MatchesConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("max")) {
        def parameter = Eval.me(dpc.value)
        def constraint = new MaxConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("maxSize")) {
        def parameter = Integer.parseInt(dpc.value)
        def constraint = new MaxSizeConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("min")) {
        def parameter = Eval.me(dpc.value)
        def constraint = new MinConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("minSize")) {
        def parameter = Integer.parseInt(dpc.value)
        def constraint = new MinSizeConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("notEqual")) {
        def parameter = dpc.value
        def constraint = new NotEqualConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("nullable")) {
        def parameter = Boolean.parseBoolean(dpc.value)
        def constraint = new NullableConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("range")) {
        def parameter = Eval.me(dpc.value)
        def constraint = new RangeConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("scale")) {
        def parameter = Integer.parseInt(dpc.value)
        def constraint = new ScaleConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("size")) {
        def parameter = Eval.me(dpc.value)
        def constraint = new SizeConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else if (dpc.configurableConstraint.name.equalsIgnoreCase("url")) {
        def parameter = Boolean.parseBoolean(dpc.value)
        def constraint = new UrlConstraint(obj.class, propertyName, parameter, null)
        constraint.validate(obj, value, errors)
      } else {
        log.error "UNSUPPORTED CONSTRAINT! ${dpc.name}!!!"
      }
    }
  }

}

