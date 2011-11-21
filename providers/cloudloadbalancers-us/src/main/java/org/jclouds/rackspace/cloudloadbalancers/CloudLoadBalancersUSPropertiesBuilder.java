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
package org.jclouds.rackspace.cloudloadbalancers;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.cloudloadbalancers.reference.RackspaceConstants.PROPERTY_ACCOUNT_ID;
import static org.jclouds.cloudloadbalancers.reference.Region.DFW;
import static org.jclouds.cloudloadbalancers.reference.Region.ORD;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersPropertiesBuilder;

import com.google.common.base.Joiner;

/**
 * Builds properties used inRackspace Cloud Load Balancers Clients
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersUSPropertiesBuilder extends CloudLoadBalancersPropertiesBuilder {
   public static final String[] REGIONS = {ORD, DFW};

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "https://auth.api.rackspacecloud.com");
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(REGIONS));
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-IL,US-TX");
      
      properties.setProperty(PROPERTY_REGION + "." + ORD + "." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_REGION + "." + ORD + "." + ENDPOINT, String
               .format("https://ord.loadbalancers.api.rackspacecloud.com/v{%s}/{%s}", PROPERTY_API_VERSION,
                        PROPERTY_ACCOUNT_ID));
      
      properties.setProperty(PROPERTY_REGION + "." + DFW + "." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_REGION + "." + DFW + "." + ENDPOINT, String
               .format("https://dfw.loadbalancers.api.rackspacecloud.com/v{%s}/{%s}", PROPERTY_API_VERSION,
                        PROPERTY_ACCOUNT_ID));
      return properties;
   }

   public CloudLoadBalancersUSPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
