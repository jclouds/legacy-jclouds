package org.jclouds.openstack.nova;

import java.util.Properties;

/**
 * @author Victor Galkin
 */

public class PropertyHelper {

   public static void overridePropertyFromSystemProperty(final Properties properties, String propertyName) {
      if ((System.getProperty(propertyName) != null) && !System.getProperty(propertyName).equals("${" + propertyName + "}"))
         properties.setProperty(propertyName, System.getProperty(propertyName));
   }


}
