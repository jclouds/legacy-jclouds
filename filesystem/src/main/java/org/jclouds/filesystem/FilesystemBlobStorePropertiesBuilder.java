/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jclouds.filesystem;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;
import org.jclouds.PropertiesBuilder;

/**
 *
 * @author rainbowbreeze
 */
public class FilesystemBlobStorePropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "http://localhost/transient");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      properties.setProperty(PROPERTY_IDENTITY, System.getProperty("user.name"));
      properties.setProperty(PROPERTY_USER_THREADS, "0");
      properties.setProperty(PROPERTY_IO_WORKER_THREADS, "0");

      System.out.println("Properties:"+properties );

      return properties;
   }

   public FilesystemBlobStorePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
