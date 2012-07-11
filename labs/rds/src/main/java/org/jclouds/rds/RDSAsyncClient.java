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
package org.jclouds.rds;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rds.features.InstanceAsyncClient;
import org.jclouds.rds.features.SecurityGroupAsyncClient;
import org.jclouds.rds.features.SubnetGroupAsyncClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;

import com.google.common.annotations.Beta;
import com.google.inject.Provides;

/**
 * Provides access to EC2 Elastic Load Balancer via REST API.
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/">RDS
 *      documentation</a>
 * @author Adrian Cole
 */
@Beta
@RequestFilters(FormSigner.class)
@VirtualHost
public interface RDSAsyncClient {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to Instance features.
    */
   @Delegate
   InstanceAsyncClient getInstanceClient();
   
   @Delegate
   InstanceAsyncClient getInstanceClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides asynchronous access to SecurityGroup features.
    */
   @Delegate
   SecurityGroupAsyncClient getSecurityGroupClient();

   @Delegate
   SecurityGroupAsyncClient getSecurityGroupClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides asynchronous access to SubnetGroup features.
    */
   @Delegate
   SubnetGroupAsyncClient getSubnetGroupClient();

   @Delegate
   SubnetGroupAsyncClient getSubnetGroupClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
}
