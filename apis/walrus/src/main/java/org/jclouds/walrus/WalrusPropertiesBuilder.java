/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.walrus;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.util.Properties;

import org.jclouds.s3.S3PropertiesBuilder;

/**
 * Builds properties used in Walrus Clients
 * 
 * @author Adrian Cole
 */
public class WalrusPropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "Walrus-1.6");
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/services/Walrus");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return properties;
   }

   public WalrusPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
