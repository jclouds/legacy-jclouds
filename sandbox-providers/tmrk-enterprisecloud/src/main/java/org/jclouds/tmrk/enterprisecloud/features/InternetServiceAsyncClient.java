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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.tmrk.enterprisecloud.binders.BindCreateInternetServiceToXmlPayload;
import org.jclouds.tmrk.enterprisecloud.binders.BindInternetServiceToXmlPayload;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.functions.URISource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * Provides asynchronous access to Layouts via their REST API.
 * <p/>
 * 
 * @see org.jclouds.tmrk.enterprisecloud.features.LayoutClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@RequestFilters(BasicAuthentication.class)
@Headers(keys = "x-tmrk-version", values = "{jclouds.api-version}")
public interface InternetServiceAsyncClient {

   /**
    * @see org.jclouds.tmrk.enterprisecloud.features.InternetServiceClient#getInternetService
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.internetService")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InternetService> getInternetService(@EndpointParam URI uri);

   /**
    * @see org.jclouds.tmrk.enterprisecloud.features.InternetServiceClient#editInternetService
    */
   @PUT
   @Produces(MediaType.APPLICATION_XML)
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> editInternetService(@EndpointParam(parser = URISource.GetURI.class) @BinderParam(BindInternetServiceToXmlPayload.class) InternetService internetService);

   /**
    * @see org.jclouds.tmrk.enterprisecloud.features.InternetServiceClient#editInternetService
    */
   @POST
   @Produces(MediaType.APPLICATION_XML)
   @Consumes("application/vnd.tmrk.cloud.internetService")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InternetService> createInternetService(@EndpointParam URI uri, @BinderParam(BindCreateInternetServiceToXmlPayload.class)InternetService data);

   /**
    * @see org.jclouds.tmrk.enterprisecloud.features.InternetServiceClient#removeInternetService
    */
   @DELETE
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> removeInternetService(@EndpointParam URI uri);

}
