package org.piercecountywa.pac.core

import net.hedtech.restfulapi.UnsupportedMethodException
import org.piercecountywa.pac.security.Securable

/**
 * To allow PAC to have configurable users this domain is created based on a view.  This view is established
 * in the importing apps BootStrap.groovy file.  The view is to be the "Person" view for things
 * like Workflow and Forms that have the concept of ownership.  The id of the view needs to be the id that is also
 * used for your "PersonRole" table.  example (in this case the Roles are paired with User):
 *
 * void createConfigurableUserView() {
 *   println "------------------------------------------------------------------"
 *   println "Creating Configurable User View"
 *   def sql = new Sql(dataSource)
 *   sql.execute("DROP TABLE IF EXISTS pac_user")
 *   String createSql = """
 * CREATE OR REPLACE VIEW pac_user
 * AS SELECT u.id, u.username, d.name as 'resource_name'
 * FROM user u
 * LEFT JOIN pac_domain d on name = 'User'
 * """
 *   println createSql
 *   sql.execute(createSql)
 *   println "------------------------------------------------------------------"
 * }
 *
 */
class PacUser implements Securable{

  Long id //unique reference to the desired user reference
  String username //the logged in users username.  Some areas might use a lookup by username
  String displayName //If perchance you don't have a resource you could use this as the display
  String resourceName //To allow for getting the display name through domain metadata 'display_template' field

  static transients = ['canUserDelete', 'canUserPut', 'putProperties']

  static mapping = {
    table 'pac_user'
    version false
    cache true
  }

  /*READ ONLY */
  def beforeInsert(){
    log.warn("Insert not allowed")
    throw new UnsupportedMethodException()
  }
  def beforeUpdate(){
    log.warn("Update not allowed")
    throw new UnsupportedMethodException()
  }
  def beforeDelete(){
    log.warn("Delete not allowed")
    throw new UnsupportedMethodException()
  }
}
