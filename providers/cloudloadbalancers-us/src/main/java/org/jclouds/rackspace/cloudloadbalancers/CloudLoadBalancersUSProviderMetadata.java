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

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersProviderMetadata;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud LoadBalancers in US.
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersUSProviderMetadata extends CloudLoadBalancersProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "cloudloadbalancers-us";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Rackspace Cloud Load Balancers US";
   }
   
   /**
	 * {@inheritDoc}
	 */
	@Override
	public URI getHomepage() {
		return URI.create("http://www.rackspace.com/cloud/cloud_hosting_products/loadbalancers");
	}

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://manage.rackspacecloud.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("cloudfiles-us", "cloudservers-us", "cloudloadbalancers-us");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-IL","US-TX");
   }

}