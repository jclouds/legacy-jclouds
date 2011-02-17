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

package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.US_EAST_1;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;
import java.util.Set;

import org.jclouds.aws.domain.Region;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Builds properties used in S3 Clients
 * 
 * @author Adrian Cole
 */
public class AWSS3PropertiesBuilder extends org.jclouds.s3.S3PropertiesBuilder {
   public static Set<String> DEFAULT_REGIONS = ImmutableSet.of(EU_WEST_1, US_EAST_1, US_WEST_1, AP_SOUTHEAST_1);

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US,US-CA,IE,SG");
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(Region.US_STANDARD, Region.US_WEST_1, "EU",
               Region.AP_SOUTHEAST_1));
      properties.setProperty(PROPERTY_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_STANDARD + "." + ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_STANDARD + "." + ISO3166_CODES, "US");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_1 + "." + ENDPOINT,
               "https://s3-us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_1 + "." + ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_REGION + "." + "EU" + "." + ENDPOINT, "https://s3-eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + "EU" + "." + ISO3166_CODES, "IE");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_1 + "." + ENDPOINT,
               "https://s3-ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_1 + "." + ISO3166_CODES, "SG");
      return properties;
   }

   public AWSS3PropertiesBuilder() {
      super();
   }

   public AWSS3PropertiesBuilder(Properties properties) {
      super(properties);
   }

}
