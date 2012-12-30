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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rds.features.InstanceApi;
import org.jclouds.rds.features.SecurityGroupApi;
import org.jclouds.rds.features.SubnetGroupApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.annotations.Beta;
import com.google.inject.Provides;

/**
 * Provides access to EC2 Elastic Load Balancer via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 * @see RDSAsyncApi
 */
@Beta
public interface RDSApi {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides synchronous access to Instance features.
    */
   @Delegate
   InstanceApi getInstanceApi();

   @Delegate
   InstanceApi getInstanceApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to SecurityGroup features.
    */
   @Delegate
   SecurityGroupApi getSecurityGroupApi();

   @Delegate
   SecurityGroupApi getSecurityGroupApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * Provides synchronous access to SubnetGroup features.
    */
   @Delegate
   SubnetGroupApi getSubnetGroupApi();

   @Delegate
   SubnetGroupApi getSubnetGroupApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

}
