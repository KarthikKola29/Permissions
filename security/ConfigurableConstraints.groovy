package org.piercecountywa.pac.security
/**
 * Class that holds the configurable constraint defaults and custom validator.
 *
 * Usage:  Import class and apply map to property as constraint.
 * Ex:  static constraints = {name ConfigurableConstraint.configurableConstraints}
 */
class ConfigurableConstraints {
  
  /**
   *  Default Constraints & Validator
   */
  static Map configurableConstraints = [blank: true, nullable: true, validator: { val, obj, errors -> ConfigurableConstraintUtil.validate(obj, propertyName, val, errors)}]
  static Map configurableConstraintsUnique = [blank: false, nullable: false, unique: true, validator: { val, obj, errors -> ConfigurableConstraintUtil.validate(obj, propertyName, val, errors)}]

}
