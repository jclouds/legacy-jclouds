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
package org.jclouds.ec2;

import java.util.Set;

import org.jclouds.ec2.features.TagAsyncApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Refer to javadoc for {@link EC2Api}, as this interface is the same, except
 * features provide asynchronous return values.
 * 
 * @author Adrian Cole
 */
public interface EC2AsyncApi {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides asynchronous access to Windows features.
    */
   @Delegate
   Optional<? extends WindowsAsyncApi> getWindowsApi();

   @Delegate
   Optional<? extends WindowsAsyncApi> getWindowsApiForRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * Provides asynchronous access to Tag features.
    */
   @Delegate
   Optional<? extends TagAsyncApi> getTagApi();

   @Delegate
   Optional<? extends TagAsyncApi> getTagApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
}
