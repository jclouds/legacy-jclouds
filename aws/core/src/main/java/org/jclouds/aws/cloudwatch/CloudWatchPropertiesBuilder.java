/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.cloudwatch;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.aws.domain.Region;

import com.google.common.base.Joiner;

/**
 * Builds properties used in Cloud Watch Clients
 * 
 * @author Adrian Cole
 */
public class CloudWatchPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      properties.setProperty(PROPERTY_API_VERSION, CloudWatchAsyncClient.VERSION);
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(Region.US_EAST_1,
               Region.US_WEST_1, Region.EU_WEST_1, Region.AP_SOUTHEAST_1));
      properties.setProperty(PROPERTY_ENDPOINT,
               "https://monitoring.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_EAST_1 + ".endpoint",
               "https://monitoring.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_1 + ".endpoint",
               "https://monitoring.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.EU_WEST_1 + ".endpoint",
               "https://monitoring.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_1 + ".endpoint",
               "https://monitoring.ap-southeast-1.amazonaws.com");
      return properties;
   }

   public CloudWatchPropertiesBuilder() {
      super();
   }

   public CloudWatchPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
