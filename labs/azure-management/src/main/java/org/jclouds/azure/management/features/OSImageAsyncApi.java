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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.azure.management.binders.BindOSImageParamsToXmlPayload;
import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.azure.management.domain.OSImageParams;
import org.jclouds.azure.management.functions.OSImageParamsName;
import org.jclouds.azure.management.xml.ListOSImagesHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes operations for managing the OS images in your subscription.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj5775">docs</a>
 * @see OSImageApi
 * @author Gerald Pereira, Adrian Cole
 */
@SkipEncoding('/')
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface OSImageAsyncApi {

   /**
    * @see OSImageApi#list()
    */
   @GET
   @Path("/services/images")
   @XMLResponseParser(ListOSImagesHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Set<OSImage>> list();

   /**
    * @see OSImageApi#add(String)
    */
   @POST
   @Path("/services/images")
   @Produces(MediaType.APPLICATION_XML)
   ListenableFuture<Void> add(@BinderParam(BindOSImageParamsToXmlPayload.class) OSImageParams params);

   /**
    * @see OSImageApi#update(String)
    */
   @PUT
   @Path("/services/images/{imageName}")
   @Produces(MediaType.APPLICATION_XML)
   ListenableFuture<Void> update(
            @PathParam("imageName") @ParamParser(OSImageParamsName.class) @BinderParam(BindOSImageParamsToXmlPayload.class) OSImageParams params);

   /**
    * @see OSImageApi#delete(String)
    */
   @DELETE
   @Path("/services/images/{imageName}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("imageName") String imageName);

}
