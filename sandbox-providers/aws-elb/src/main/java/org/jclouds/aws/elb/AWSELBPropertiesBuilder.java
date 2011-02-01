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

package org.jclouds.aws.elb;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.US_EAST_1;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_ZONECLIENT_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.elb.ELBAsyncClient;
import org.jclouds.elb.ELBPropertiesBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Builds properties used in ELB Clients
 * 
 * @author Adrian Cole
 */
public class AWSELBPropertiesBuilder extends ELBPropertiesBuilder {
   public static Set<String> DEFAULT_REGIONS = ImmutableSet.of(EU_WEST_1, US_EAST_1, US_WEST_1, AP_SOUTHEAST_1);

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(DEFAULT_REGIONS));
      properties.setProperty(PROPERTY_API_VERSION, ELBAsyncClient.VERSION);
      properties.setProperty(PROPERTY_ENDPOINT, "https://elasticloadbalancing.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_EAST_1 + ".endpoint",
            "https://elasticloadbalancing.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_1 + ".endpoint",
            "https://elasticloadbalancing.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.EU_WEST_1 + ".endpoint",
            "https://elasticloadbalancing.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_1 + ".endpoint",
            "https://elasticloadbalancing.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_ZONECLIENT_ENDPOINT, "https://ec2.us-east-1.amazonaws.com");
      return properties;
   }

   public AWSELBPropertiesBuilder() {
      super();
   }

   public AWSELBPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
