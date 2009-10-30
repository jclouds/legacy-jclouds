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
package org.jclouds.rackspace.cloudfiles;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.CloudFilesCDN;
import org.jclouds.rackspace.cloudfiles.binders.BindCFObjectToEntity;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.functions.ObjectName;
import org.jclouds.rackspace.cloudfiles.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseCdnUriFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerCDNMetadataListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseContainerListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoFromHeaders;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.functions.ReturnTrueOn404FalseOn409;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(CloudFiles.class)
public interface CloudFilesClient {

   CFObject newCFObject();

   /**
    * HEAD operations against an account are performed to retrieve the number of Containers and the
    * total bytes stored in Cloud Files for the account.
    * <p/>
    * Determine the number of Containers within the account and the total bytes stored. Since the
    * storage system is designed to store large amounts of data, care should be taken when
    * representing the total bytes response as an “integer”; when possible, convert it to a 64-bit
    * unsigned integer if your platform supports that primitive type.
    */
   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   AccountMetadata getAccountStatistics();

   /**
    * GET operations against the X-Storage-Url for an account are performed to retrieve a list of
    * existing storage
    * <p/>
    * Containers ordered by name. The following list describes the optional query parameters that
    * are supported with this request.
    * <ul>
    * <li>limit - For an integer value N, limits the number of results to at most N values.</li>
    * <li>marker - Given a string value X, return Object names greater in value than the speciﬁed
    * marker.</li>
    * <li>format - Specify either json or xml to return the respective serialized response.</li>
    * </ul>
    * <p/>
    * At this time, a “preﬁx” query parameter is not supported at the Account level.
    * 
    *<h4>Large Container Lists</h4>
    * The system will return a maximum of 10,000 Container names per request. To retrieve subsequent
    * container names, another request must be made with a ‘marker’ parameter. The marker indicates
    * where the last list left off and the system will return container names greater than this
    * marker, up to 10,000 again. Note that the ‘marker’ value should be URL encoded prior to
    * sending the HTTP request.
    * <p/>
    * If 10,000 is larger than desired, a ‘limit’ parameter may be given.
    * <p/>
    * If the number of container names returned equals the limit given (or 10,000 if no limit is
    * given), it can be assumed there are more container names to be listed. If the container name
    * list is exactly divisible by the limit, the last request will simply have no content.
    */
   @GET
   @ResponseParser(ParseContainerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   Future<? extends SortedSet<ContainerMetadata>> listContainers(ListContainerOptions... options);

   @POST
   @Path("{container}/{name}")
   boolean setObjectInfo(@PathParam("container") String container, @PathParam("name") String name,
            @BinderParam(BindMapToHeadersWithPrefix.class) Map<String, String> userMetadata);

   @GET
   @ResponseParser(ParseContainerCDNMetadataListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/")
   @Endpoint(CloudFilesCDN.class)
   SortedSet<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options);

   // TODO: Container name is not included in CDN HEAD response headers, so we cannot populate it
   // here.
   @HEAD
   @ResponseParser(ParseContainerCDNMetadataFromHeaders.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Path("{container}")
   @Endpoint(CloudFilesCDN.class)
   ContainerCDNMetadata getCDNMetadata(@PathParam("container") String container);

   @PUT
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   URI enableCDN(@PathParam("container") String container,
            @HeaderParam(CloudFilesHeaders.CDN_TTL) Long ttl);

   @PUT
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   URI enableCDN(@PathParam("container") String container);

   @POST
   @Path("{container}")
   @ResponseParser(ParseCdnUriFromHeaders.class)
   @Endpoint(CloudFilesCDN.class)
   URI updateCDN(@PathParam("container") String container,
            @HeaderParam(CloudFilesHeaders.CDN_TTL) Long ttl);

   @POST
   @Path("{container}")
   @Headers(keys = CloudFilesHeaders.CDN_ENABLED, values = "False")
   @Endpoint(CloudFilesCDN.class)
   boolean disableCDN(@PathParam("container") String container);

   @PUT
   @Path("{container}")
   Future<Boolean> createContainer(@PathParam("container") String container);

   @DELETE
   @ExceptionParser(ReturnTrueOn404FalseOn409.class)
   @Path("{container}")
   Future<Boolean> deleteContainerIfEmpty(@PathParam("container") String container);

   @GET
   @QueryParams(keys = "format", values = "json")
   @ResponseParser(ParseObjectInfoListFromJsonResponse.class)
   @Path("{container}")
   Future<BoundedSortedSet<ObjectInfo>> listObjects(@PathParam("container") String container,
            ListContainerOptions... options);

   @HEAD
   @Path("{container}")
   @ExceptionParser(ReturnFalseOn404.class)
   boolean containerExists(@PathParam("container") String container);

   @PUT
   @Path("{container}/{name}")
   @ResponseParser(ParseETagHeader.class)
   Future<String> putObject(
            @PathParam("container") String container,
            @PathParam("name") @ParamParser(ObjectName.class) @BinderParam(BindCFObjectToEntity.class) CFObject object);

   @GET
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   Future<CFObject> getObject(@PathParam("container") String container,
            @PathParam("name") String name, GetOptions... options);

   @HEAD
   @ResponseParser(ParseObjectInfoFromHeaders.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @Path("{container}/{name}")
   MutableObjectInfoWithMetadata getObjectInfo(@PathParam("container") String container,
            @PathParam("name") String name);

   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("{container}/{name}")
   Future<Void> removeObject(@PathParam("container") String container,
            @PathParam("name") String name);

}
