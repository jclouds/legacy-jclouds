/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
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
      return properties;
   }

   public FilesystemBlobStorePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
