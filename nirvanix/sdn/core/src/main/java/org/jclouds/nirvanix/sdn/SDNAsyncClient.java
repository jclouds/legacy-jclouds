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

package org.jclouds.nirvanix.sdn;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.blobstore.binders.BindBlobToMultipartForm;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.nirvanix.sdn.binders.BindMetadataToQueryParams;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;
import org.jclouds.nirvanix.sdn.filters.AddSessionTokenToRequest;
import org.jclouds.nirvanix.sdn.filters.InsertUserContextIntoPath;
import org.jclouds.nirvanix.sdn.functions.ParseMetadataFromJsonResponse;
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Nirvanix SDN resources via their REST API.
 * <p/>
 * 
 * @see SDNClient
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @author Adrian Cole
 */
@RequestFilters(AddSessionTokenToRequest.class)
@SkipEncoding( { '/', ':' })
@QueryParams(keys = SDNQueryParams.OUTPUT, values = "json")
public interface SDNAsyncClient {

   public Blob newBlob();

   /**
    * @see SDNClient#getStorageNode
    */
   @GET
   @Path("/ws/IMFS/GetStorageNode.ashx")
   @ResponseParser(ParseUploadInfoFromJsonResponse.class)
   ListenableFuture<UploadInfo> getStorageNode(
            @QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @QueryParam(SDNQueryParams.SIZEBYTES) long size);

   /**
    * @see SDNClient#upload
    */
   @POST
   @Path("/Upload.ashx")
   ListenableFuture<Void> upload(@EndpointParam URI endpoint,
            @QueryParam(SDNQueryParams.UPLOADTOKEN) String uploadToken,
            @QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @BinderParam(BindBlobToMultipartForm.class) Blob blob);

   /**
    * @see SDNClient#setMetadata
    */
   @GET
   @Path("/ws/Metadata/SetMetadata.ashx")
   @QueryParams(keys = SDNQueryParams.PATH, values = "{path}")
   ListenableFuture<Void> setMetadata(@PathParam("path") String path,
            @BinderParam(BindMetadataToQueryParams.class) Map<String, String> metadata);

   /**
    * @see SDNClient#getMetadata
    */
   @GET
   @Path("/ws/Metadata/GetMetadata.ashx")
   @ResponseParser(ParseMetadataFromJsonResponse.class)
   @QueryParams(keys = SDNQueryParams.PATH, values = "{path}")
   ListenableFuture<Map<String, String>> getMetadata(@PathParam("path") String path);

   /**
    * @see SDNClient#getFile
    */
   @GET
   @Path("/{path}")
   @OverrideRequestFilters
   @RequestFilters(InsertUserContextIntoPath.class)
   ListenableFuture<String> getFile(@PathParam("path") String path);

}
