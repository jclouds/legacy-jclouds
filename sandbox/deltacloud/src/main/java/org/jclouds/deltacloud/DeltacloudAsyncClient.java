/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.deltacloud.reference.DeltacloudCollection;
import org.jclouds.deltacloud.xml.LinksHandler;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to deltacloud via their REST API.
 * <p/>
 * 
 * @see DeltacloudClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.APPLICATION_XML)
public interface DeltacloudAsyncClient {

   /**
    * @see DeltacloudClient#getCollections
    */
   @GET
   @Path("")
   @XMLResponseParser(LinksHandler.class)
   ListenableFuture<Map<DeltacloudCollection, URI>> getCollections();

   /**
    * @see DeltacloudClient#createInstance
    */
   @POST
   @Path("")
   ListenableFuture<String> createInstance(URI instanceCollection, @FormParam("image_id") String imageId,
         CreateInstanceOptions... options);

}
