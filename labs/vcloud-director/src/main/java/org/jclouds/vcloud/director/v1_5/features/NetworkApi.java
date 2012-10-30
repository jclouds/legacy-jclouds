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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.functions.href.NetworkURNToHref;

/**
 * Provides synchronous access to {@link Network}.
 * 
 * @see NetworkAsyncApi
 * @author danikov, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface NetworkApi {

   /**
    * Retrieves a network.
    * 
    * @return the network or null if not found
    */
   Network get(String networkUrn);

   Network get(URI networkHref);
   
   /**
    * @return synchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam(parser = NetworkURNToHref.class) String networkUrn);

   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam URI networkHref);
}
