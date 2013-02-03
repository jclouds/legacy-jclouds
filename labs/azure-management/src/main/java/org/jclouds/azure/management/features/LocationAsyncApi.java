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
package org.jclouds.azure.management.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.azure.management.domain.Location;
import org.jclouds.azure.management.xml.ListLocationsHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes operations for listing the available data center locations
 * for a hosted service in your subscription.
 * <p/>
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299" />
 * @see LocationApi
 * @author Adrian Cole
 */
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface LocationAsyncApi {

   /**
    * @see LocationApi#list()
    */
   @Named("ListLocations")
   @GET
   @Path("/locations")
   @XMLResponseParser(ListLocationsHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Set<Location>> list();

}
