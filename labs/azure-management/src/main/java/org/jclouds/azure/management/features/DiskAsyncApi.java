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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azure.management.domain.Disk;
import org.jclouds.azure.management.functions.ParseRequestIdHeader;
import org.jclouds.azure.management.xml.ListDisksHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes operations for managing the virtual machines Disk in your
 * subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
 * @see DiskApi
 * @author Gerald Pereira
 */
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface DiskAsyncApi {

   /**
    * @see DiskApi#list()
    */
   @Named("ListDisks")
   @GET
   @Path("/services/disks")
   @XMLResponseParser(ListDisksHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Set<Disk>> list();

   /**
    * @see DiskApi#delete
    */
   @Named("DeleteDisk")
   @DELETE
   @Path("/services/disks/{diskName}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   ListenableFuture<String> delete(@PathParam("diskName") String imageName);
}
