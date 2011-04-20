package org.jclouds.openstack.nova;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.jclouds.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Victor Galkin
 */

public class PropertyHelper {

   public static String provider = "nova";

   public static void overridePropertyFromSystemProperty(final Properties properties, String propertyName) {
      if ((System.getProperty(propertyName) != null) && !System.getProperty(propertyName).equals("${" + propertyName + "}"))
         properties.setProperty(propertyName, System.getProperty(propertyName));
   }

   public static Map<String, String> setupKeyPair(Properties properties) throws FileNotFoundException, IOException {
      return ImmutableMap.<String, String>of(
            "private", Files.toString(new File(properties.getProperty("test.ssh.keyfile.private")), Charsets.UTF_8),
            "public", Files.toString(new File(properties.getProperty("test.ssh.keyfile.public")), Charsets.UTF_8));
   }

   public static Properties setupProperties(Class clazz) throws IOException {
      Properties properties = new Properties();

      properties.load(clazz.getResourceAsStream("/test.properties"));
      overridePropertyFromSystemProperty(properties, "test." + provider + ".endpoint");
      overridePropertyFromSystemProperty(properties, "test." + provider + ".apiversion");
      overridePropertyFromSystemProperty(properties, "test." + provider + ".identity");
      overridePropertyFromSystemProperty(properties, "test." + provider + ".credential");
      overridePropertyFromSystemProperty(properties, "test.initializer");
      return properties;
   }

   public static Properties setupOverrides(final Properties properties) {
      properties.setProperty(provider + ".identity", properties.getProperty("test." + provider + ".identity"));
      properties.setProperty(provider + ".credential", properties.getProperty("test." + provider + ".credential"));
      properties.setProperty(provider + ".endpoint", properties.getProperty("test." + provider + ".endpoint"));
      properties.setProperty(provider + ".apiversion", "test." + provider + ".apiversion");
      properties.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      return properties;
   }


}
