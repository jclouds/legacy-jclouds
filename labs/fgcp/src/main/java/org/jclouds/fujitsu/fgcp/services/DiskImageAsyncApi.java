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
package org.jclouds.fujitsu.fgcp.services;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.fujitsu.fgcp.compute.functions.SingleElementResponseToElement;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.filters.RequestAuthenticator;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Non-blocking API relating to disk images.
 * 
 * @author Dies Koper
 */
@RequestFilters(RequestAuthenticator.class)
@QueryParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@PayloadParams(keys = RequestParameters.VERSION, values = FGCPAsyncApi.VERSION)
@Consumes(MediaType.TEXT_XML)
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface DiskImageAsyncApi {

    @GET
    @JAXBResponseParser
    @QueryParams(keys = "Action", values = "GetDiskImageAttributes")
    @Transform(SingleElementResponseToElement.class)
    ListenableFuture<DiskImage> get(@QueryParam("diskImageId") String id);

    @GET
    @JAXBResponseParser
    @QueryParams(keys = "Action", values = "UpdateDiskImageAttribute")
    ListenableFuture<Void> update(
            @QueryParam("diskImageId") String diskImageId,
            @QueryParam("updateLcId") String localeId,
            @QueryParam("attributeName") String name,
            @QueryParam("attributeValue") String value);

    @GET
    @JAXBResponseParser
    @QueryParams(keys = "Action", values = "UnregisterDiskImage")
    ListenableFuture<Void> deregister(@QueryParam("diskImageId") String id);
}
