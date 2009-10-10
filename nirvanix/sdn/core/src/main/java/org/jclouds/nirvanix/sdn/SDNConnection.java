/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.blobstore.decorators.AddBlobEntityAsMultipartForm;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.nirvanix.sdn.decorators.AddMetadataAsQueryParams;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;
import org.jclouds.nirvanix.sdn.filters.AddSessionTokenToRequest;
import org.jclouds.nirvanix.sdn.functions.ParseUploadInfoFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;
import org.jclouds.rest.annotations.DecoratorParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.Multimap;

/**
 * Provides access to Nirvanix SDN resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @author Adrian Cole
 */
@Endpoint(SDN.class)
@QueryParams(keys = SDNQueryParams.OUTPUT, values = "json")
@RequestFilters(AddSessionTokenToRequest.class)
@SkipEncoding( { '/', ':' })
public interface SDNConnection {

   /**
    * The GetStorageNode method is used to determine which storage node a file should be uploaded
    * to. It returns the host to upload to and an Upload Token that will be used to authenticate.
    */
   @GET
   @Path("/IMFS/GetStorageNode.ashx")
   @ResponseParser(ParseUploadInfoFromJsonResponse.class)
   UploadInfo getStorageNode(@QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @QueryParam(SDNQueryParams.SIZEBYTES) long size);

   @POST
   @Path("/Upload.ashx")
   Future<Void> upload(@Endpoint URI endpoint,
            @QueryParam(SDNQueryParams.UPLOADTOKEN) String uploadToken,
            @QueryParam(SDNQueryParams.DESTFOLDERPATH) String folderPath,
            @DecoratorParam(AddBlobEntityAsMultipartForm.class) Blob<BlobMetadata> blob);

   @PUT
   @Path("/Metadata/SetMetadata.ashx")
   @QueryParams(keys = SDNQueryParams.PATH, values = "{container}/{key}")
   Future<Void> setMetadata(@PathParam("container") String container, @PathParam("key") String key,
            @DecoratorParam(AddMetadataAsQueryParams.class) Multimap<String, String> metadata);

}
