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
package org.jclouds.aws.simpledb;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.domain.Region.AP_NORTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.US_EAST_1;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.util.Properties;

import org.jclouds.aws.domain.Region;

/**
 * Builds properties used in SimpleDB Clients
 * 
 * @author Adrian Cole
 */
public class AWSSimpleDBPropertiesBuilder extends org.jclouds.simpledb.SimpleDBPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.putAll(Region.regionProperties());
      
      properties.setProperty(PROPERTY_ENDPOINT, "https://sdb.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_EAST_1 + ".endpoint", "https://sdb.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_1 + ".endpoint",
            "https://sdb.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + EU_WEST_1 + ".endpoint",
            "https://sdb.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_1 + ".endpoint",
            "https://sdb.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_NORTHEAST_1 + ".endpoint",
      "https://sdb.ap-northeast-1.amazonaws.com");
      return properties;
   }

   public AWSSimpleDBPropertiesBuilder() {
      super();
   }

   public AWSSimpleDBPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
