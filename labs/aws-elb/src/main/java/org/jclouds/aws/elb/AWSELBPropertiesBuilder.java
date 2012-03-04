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
package org.jclouds.aws.elb;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.domain.Region.AP_NORTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.SA_EAST_1;
import static org.jclouds.aws.domain.Region.US_EAST_1;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.aws.domain.Region.US_WEST_2;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_ZONECLIENT_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.elb.ELBAsyncClient;
import org.jclouds.elb.ELBPropertiesBuilder;

/**
 * Builds properties used in ELB Clients
 * 
 * @author Adrian Cole
 */
public class AWSELBPropertiesBuilder extends ELBPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.putAll(Region.regionProperties());
      properties.setProperty(PROPERTY_API_VERSION, ELBAsyncClient.VERSION);
      properties.setProperty(PROPERTY_ENDPOINT, "https://elasticloadbalancing.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_EAST_1 + ".endpoint",
            "https://elasticloadbalancing.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_1 + ".endpoint",
            "https://elasticloadbalancing.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_2 + ".endpoint",
            "https://elasticloadbalancing.us-west-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + SA_EAST_1 + ".endpoint",
            "https://elasticloadbalancing.sa-east-1.amazonaws.com");      
      properties.setProperty(PROPERTY_REGION + "." + EU_WEST_1 + ".endpoint",
            "https://elasticloadbalancing.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_1 + ".endpoint",
            "https://elasticloadbalancing.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_NORTHEAST_1 + ".endpoint",
            "https://elasticloadbalancing.ap-northeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_ZONECLIENT_ENDPOINT, "https://ec2.us-east-1.amazonaws.com");
      return properties;
   }

   public AWSELBPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
