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
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides access to EC2 features, broken up by feature group. Use of the
 * {@link Optional} type allows you to check to see if the underlying
 * implementation supports a particular feature before attempting to use it.
 * This is useful in clones like OpenStack, CloudStack, or Eucalyptus, which
 * track the api, but are always behind Amazon's service. In the case of Amazon
 * ({@code aws-ec2}), you can expect all features to be present.
 * 
 * 
 * Example
 * 
 * <pre>
 * Optional&lt;? extends WindowsApi&gt; windowsOption = ec2Client.getWindowsApi();
 * checkState(windowsOption.isPresent(), &quot;windows feature required, but not present&quot;);
 * </pre>
 * 
 * @author Adrian Cole
 * @see EC2AsyncApi
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface EC2Api {
   /**
    * 
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /**
    * Provides synchronous access to Windows features.
    */
   @Delegate
   Optional<? extends WindowsApi> getWindowsApi();

   @Delegate
   Optional<? extends WindowsApi> getWindowsApiForRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * Provides synchronous access to Tag features.
    */
   @Delegate
   Optional<? extends TagApi> getTagApi();

   @Delegate
   Optional<? extends TagApi> getTagApiForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
}
