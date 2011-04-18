package org.jclouds.openstack.nova;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: VGalkin
 * Date: 4/14/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyHelper {

   public static void overridePropertyFromSystemProperty(final Properties properties, String propertyName) {
      if ((System.getProperty(propertyName) != null) && !System.getProperty(propertyName).equals("${" + propertyName + "}"))
         properties.setProperty(propertyName, System.getProperty(propertyName));
   }


}
