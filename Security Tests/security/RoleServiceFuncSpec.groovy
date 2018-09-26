package org.piercecountywa.pac.security

import grails.testing.mixin.integration.Integration
import org.piercecountywa.testing.PacRestSpecification
import org.springframework.http.HttpStatus
import spock.lang.Shared

@Integration
class RoleServiceFuncSpec extends PacRestSpecification {
  protected static final String RESOURCE = 'role'

  @Shared
  private def persistedId
  @Shared
  private def permissionIds

  def "Test Role POST"() {
    setup:
    def content = [
        name: "Jelly"
    ]

    when:
    def jsonObject = doPost(RESOURCE, content)
    persistedId = jsonObject.id

    then:
    response.status == HttpStatus.CREATED.value()
    jsonObject.id != null
    jsonObject.name == "Jelly"
    jsonObject.authority == "ROLE_JELLY"
  }

  def "Test Role GET"() {
    when:
    doGetPermissions(persistedId)
    def permissionList = getJsonObject()
    permissionIds = permissionList.collect { item ->
      [id: item.id, role: persistedId, httpMethod: item.httpMethod]
    }

    def jsonObject = doGet(RESOURCE, persistedId)

    then:
    response.status == HttpStatus.OK.value()
    assert jsonObject.authority == "ROLE_JELLY"
    assert jsonObject.permissions != null
    assert permissionIds.size() == 4
  }

  def "Test Role DELETE"() {
    setup:
    def content = doGet(RESOURCE, persistedId)

    when:
    doDelete(RESOURCE, content.id)

    then:
    response.status == HttpStatus.OK.value()

    when:
    doGet(RESOURCE, content.id)

    then: "Verify role is deleted"
    assert response.status == HttpStatus.NOT_FOUND.value()
  }

  def "Test Associated Permission DELETE"() {
    when:
    doGet('permission', permissionIds?.getAt(0)?.id)

    then: "Verify associated permission is deleted"
    assert response.status == HttpStatus.NOT_FOUND.value()
  }

  private doGetPermissions(def id) {
    def url = "${LOCAL_BASE}:$serverPort/api/${RESOURCE}/${id}/permission"

    return get(url, {
      auth USERNAME, PASSWORD
      contentType CONTENT_TYPE
      accept CONTENT_TYPE })
  }
}