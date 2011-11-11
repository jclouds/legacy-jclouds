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
package org.jclouds.cloudloadbalancers;

import static org.jclouds.Constants.PROPERTY_API_VERSION;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used inRackspace Cloud Load Balancers Clients
 * 
 * @author Dan Lo Bianco
 */
public class CloudLoadBalancersPropertiesBuilder extends PropertiesBuilder {
	   @Override
	   protected Properties defaultProperties() {
	      Properties properties = super.defaultProperties();
	      properties.setProperty(PROPERTY_API_VERSION, "1.0");
	      
	      return properties;
	   }

	   public CloudLoadBalancersPropertiesBuilder(Properties properties) {
	      super(properties);
	   }

	}
