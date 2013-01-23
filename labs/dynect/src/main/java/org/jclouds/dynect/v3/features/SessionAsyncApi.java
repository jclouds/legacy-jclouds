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
package org.jclouds.dynect.v3.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.dynect.v3.DynECTFallbacks.FalseOn400;
import org.jclouds.dynect.v3.domain.Session;
import org.jclouds.dynect.v3.domain.SessionCredentials;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to DynECT Managed DNS through the API2 api
 * <p/>
 * 
 * @see SessionApi
 * @see <a href="https://manage.dynect.net/help/docs/api2/rest/" />
 * @author Adrian Cole
 */
// required for all calls
@Produces(APPLICATION_JSON)
@Headers(keys = "API-Version", values = "{jclouds.api-version}")
@Path("/Session")
public interface SessionAsyncApi {

   /**
    * @see SessionApi#create
    */
   @Named("POST:Session")
   @POST
   @SelectJson("data")
   ListenableFuture<Session> login(@BinderParam(BindToJsonPayload.class) SessionCredentials credentials);

   /**
    * @see SessionApi#isValid
    */
   @Named("GET:Session")
   @GET
   @Fallback(FalseOn400.class)
   ListenableFuture<Boolean> isValid(@HeaderParam("Auth-Token") String token);

   /**
    * @see SessionApi#logout
    */
   @Named("DELETE:Session")
   @DELETE
   ListenableFuture<Void> logout(@HeaderParam("Auth-Token") String token);
}
