/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.nirvanix.sdn;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;

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
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides access to Nirvanix SDN resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @author Adrian Cole
 */
@Endpoint(SDN.class)
@RequestFilters(AddSessionTokenToRequest.class)
@SkipEncoding( { '/', ':' })
@QueryParams(keys = SDNQueryParams.OUTPUT, values = "json")
public interface SDNClient {

   public Blob newBlob();
   
   /**
    * The GetStorageNode method is used to determine which storage node a file should be uploaded
    * to. It returns the host to upload to and an Upload Token that will be used to authenticate.
    */
   @GET
   @Path("/ws/IMFS/GetStorageNode.ashx")
   @ResponseParser(ParseUploadInfoFromJsonResponse.class)
   UploadInfo getStorageNode(@QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @QueryParam(SDNQueryParams.SIZEBYTES) long size);

   @POST
   @Path("/Upload.ashx")
   Future<Void> upload(@Endpoint URI endpoint,
            @QueryParam(SDNQueryParams.UPLOADTOKEN) String uploadToken,
            @QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @BinderParam(BindBlobToMultipartForm.class) Blob blob);

   /**
    * The SetMetadata method is used to set specified metadata for a file or folder.
    */
   @GET
   @Path("/ws/Metadata/SetMetadata.ashx")
   @QueryParams(keys = SDNQueryParams.PATH, values = "{path}")
   Future<Void> setMetadata(@PathParam("path") String path,
            @BinderParam(BindMetadataToQueryParams.class) Map<String, String> metadata);

   /**
    * The GetMetadata method is used to retrieve all metadata from a file or folder.
    */
   @GET
   @Path("/ws/Metadata/GetMetadata.ashx")
   @QueryParams(keys = SDNQueryParams.PATH, values = "{path}")
   Future<String> getMetadata(@PathParam("path") String path);

   /**
    * Get the contents of a file
    */
   @GET
   @Path("/{path}")
   @OverrideRequestFilters
   @RequestFilters(InsertUserContextIntoPath.class)
   Future<String> getFile(@PathParam("path") String path);

}
