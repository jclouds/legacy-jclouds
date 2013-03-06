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
package org.jclouds.openstack.glance.v1_0;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.glance.v1_0.features.ImageAsyncApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to Glance via their REST API.
 * <p/>
 * 
 * @see GlanceApi
 * @see <a href="http://glance.openstack.org/glanceapi.html">api doc</a>
 * @author Adrian Cole
 */
public interface GlanceAsyncApi {
   /**
    * 
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides asynchronous access to Extension features.
    */
   @Delegate
   ExtensionAsyncApi getExtensionApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Image features.
    */
   @Delegate
   ImageAsyncApi getImageApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

}
