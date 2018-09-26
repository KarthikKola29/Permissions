package org.piercecountywa.pac.security

import org.piercecountywa.commons.exception.ServiceValidationException

/**
 * This is an exception class allowing for on-the-fly creation of a 400 error with
 * custom 'type' (aliased in constructor as 'name' param).  Throwing this exception
 * turns off the front end global error handling, so this exception must be handled
 * manually in the triggering local UI code.
 */
class ConfigurableSecurityServiceValidationException extends ServiceValidationException {

  private List<Map> errors = []
  private String exceptionName;

  /**
   * Creates an exception with custom error 'type' property.
   *
   * @param name the error 'type' assigned to exception
   * @param errorMessages list of error messages to be thrown
   */
  ConfigurableSecurityServiceValidationException(String name, List<String> errorMessages) {
    super(errorMessages.toString())
    this.exceptionName = name
    errorMessages.each {
      msg ->
        addErrorMessage(msg)
    }
  }

  public returnMap = {
    localize ->
      def map = [:]
      map.headers = ['X-Status-Reason':'Validation failed']
      map.message = this.exceptionName
      map.errors = errors
      map
  }

  private void addErrorMessage(String message) {
    // note here that the below statement differs from implementation of super-class
    //  1. the 'type' is assigned the value passed in 'name' constructor param
    //  2. there is a 'handleLocally' which is used to trigger override in frontend global error handling
    errors << [type: this.exceptionName, errorMessage: message, handleLocally: true]
  }

}
