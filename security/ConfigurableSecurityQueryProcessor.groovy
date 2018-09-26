package org.piercecountywa.pac.security

import grails.util.Holders
import org.piercecountywa.commons.grails.criteria.QueryProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConfigurableSecurityQueryProcessor<T> implements QueryProcessor<T> {
  private final static Logger log = LoggerFactory.getLogger(ConfigurableSecurityQueryProcessor.class)

  @Override
  def <T> Object preprocess(Object convertedJsonCriteria, Object convertedJsonProjections, Class<T> resource) {
    def configurableSecurityService = Holders.getGrailsApplication().getMainContext().getBean("configurableSecurityService")
    // Default to a query that'll never return a result
    //TODO: Modify the Dynamic Query DSL to accept a specific config string that'll
    //TODO: always return no results (since 'id' isn't guaranteed to be a domain Failure:  Test can get Bootstraps(org.piercecountywa.pac.bootstrap.PackageBootstrapLocatorSpec) property)
    Object preprocessedCriteria = ["criteria":[["and":[["eq":["id",0]],["eq":["id",1]]]]]]
    log.trace "Original Criteria for '$resource' (${resource.getSimpleName()}):\n${convertedJsonCriteria}"
    log.trace "Projections for '$resource' (${resource.getSimpleName()}):\n${convertedJsonProjections}"
    //TODO: Apply conditions on associated properties in criteria and projections
    if(configurableSecurityService.canRead(resource.getSimpleName())) {
      preprocessedCriteria = configurableSecurityService.createCombinedCriteria(resource.getSimpleName(), convertedJsonCriteria)
      log.trace "Original condition criteria for '$resource' (${resource.getSimpleName()}): $convertedJsonCriteria"
      log.trace "Security decorated condition criteria for '$resource' (${resource.getSimpleName()}): $preprocessedCriteria"
    } else {
      def springSecurityService = Holders.getGrailsApplication().getMainContext().getBean("springSecurityService")
      def user = springSecurityService.principal
      log.trace "User '${user?.username}' cannot perform 'GET' query on '${resource.getSimpleName()}' ($resource). Using critera that returns no results."
    }
    return preprocessedCriteria
  }

  @Override
  def <T> List<T> postprocess(List<T> results, Object convertedJsonProjections, Class<T> resource) {
    def configurableSecurityService = Holders.getGrailsApplication().getMainContext().getBean("configurableSecurityService")

    // null out results properties for which current user lacks permission
    results = configurableSecurityService.sanitizeQueryResults(resource, convertedJsonProjections, results)

    log.trace "Projections for '$resource' (${resource.getSimpleName()}):\n${convertedJsonProjections}"
    //TODO: When coming from dynamic query, the result is a list of maps (assuming caused
    //TODO: by having projections), so with the following call stack
    //TODO: filterListData -> permissionGetListFilter -> new DefaultGrailsDomainClass(dm.class)
    //TODO: dm.class is not a valid Domain class (it's a HashMap), causing a runtime exception
//    def filteredResults = configurableSecurityService.filterListData(resource.getSimpleName(),results)
    return results //will be filtered in FDISRestfulService, but NOT from DynamicQuery
  }

  @Override
  List getAdditionalJoinDefinitions() {
    return null
  }
}
