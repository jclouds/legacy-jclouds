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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.binders.BindMapAsMetadata;
import org.jclouds.vcloud.director.v1_5.binders.BindStringAsMetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.RegexValueParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see MetadataApi
 * @author Adrian Cole, danikov
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface MetadataAsyncApi {

   /**
    * @see MetadataApi.Readable#get()
    */
   @GET
   @Path("/metadata")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Metadata> get();

   /**
    * @see MetadataApi.Readable#get(String)
    */
   @GET
   @Path("/metadata/{key}")
   @Consumes
   @ResponseParser(RegexValueParser.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> get(@PathParam("key") String key);

   /**
    * @see MetadataApi.Writable#putAll
    */
   @POST
   @Path("/metadata")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA)
   @JAXBResponseParser
   ListenableFuture<Task> putAll(@BinderParam(BindMapAsMetadata.class) Map<String, String> metadata);

   /**
    * @see MetadataApi#put
    */
   @PUT
   @Path("/metadata/{key}")
   @Consumes(VCloudDirectorMediaType.TASK)
   @Produces(VCloudDirectorMediaType.METADATA_VALUE)
   @JAXBResponseParser
   ListenableFuture<Task> put(@PathParam("key") String key,
         @BinderParam(BindStringAsMetadataValue.class) String metadataValue);

   /**
    * @see MetadataApi.Writable#remove
    */
   @DELETE
   @Path("/metadata/{key}")
   @Consumes(VCloudDirectorMediaType.TASK)
   @JAXBResponseParser
   ListenableFuture<Task> remove(@PathParam("key") String key);

}
