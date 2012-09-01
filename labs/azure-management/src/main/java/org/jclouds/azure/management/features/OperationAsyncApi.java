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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.azure.management.domain.Operation;
import org.jclouds.azure.management.xml.OperationHandler;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Service Management API includes one operation for tracking the progress of asynchronous requests.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460796">docs</a>
 * @see OperationApi
 * @author Gerald Pereira
 */
@SkipEncoding('/')
@Headers(keys = "x-ms-version", values = "2012-03-01")
public interface OperationAsyncApi {

   @GET
   @Path("/operations/{request-id}")
   @XMLResponseParser(OperationHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_XML)
   ListenableFuture<Operation> get(@PathParam("request-id") String requestId);

}
