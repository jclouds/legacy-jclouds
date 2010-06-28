/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.nirvanix.sdn.functions.ParseSessionTokenFromJsonResponse;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Nirvanix SDN resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @author Adrian Cole
 */
@QueryParams(keys = SDNQueryParams.OUTPUT, values = "json")
public interface SDNAuthAsyncClient {

   public interface AuthenticationResponse {
      @SessionToken
      String getSessionToken();
   }

   @GET
   @ResponseParser(ParseSessionTokenFromJsonResponse.class)
   @Path("/ws/Authentication/Login.ashx")
   ListenableFuture<String> authenticate(@QueryParam(SDNQueryParams.APPKEY) String appKey,
            @QueryParam(SDNQueryParams.USERNAME) String user,
            @QueryParam(SDNQueryParams.PASSWORD) String password);
}
