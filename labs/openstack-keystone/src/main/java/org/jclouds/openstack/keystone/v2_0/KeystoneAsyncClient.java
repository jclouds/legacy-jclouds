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
package org.jclouds.openstack.keystone.v2_0;

import java.util.Set;

import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.keystone.v2_0.functions.RegionToAdminEndpointURI;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to Openstack keystone resources via their REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see <a href="http://keystone.openstack.org/" />
 * @see KeystoneClient
 */
@Path("/v{" + Constants.PROPERTY_API_VERSION + "}")
public interface KeystoneAsyncClient {
   /**
    * @return the Region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();

   /** Provides asynchronous access to Identity user-accessible features */
   @Delegate
   ServiceAsyncClient getServiceClientForRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region);

   /** Provides asynchronous access to the KeyStone Admin API */
   @Delegate
   AdminAsyncClient getAdminClientForRegion(@EndpointParam(parser = RegionToAdminEndpointURI.class) @Nullable String region);
}
