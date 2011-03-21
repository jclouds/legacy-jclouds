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

package org.jclouds.savvis.vpdc.internal;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.savvis.vpdc.domain.internal.VCloudSession;
import org.jclouds.savvis.vpdc.functions.ParseLoginResponseFromHeaders;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Establishes a context with a VCloud endpoint.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
public interface LoginAsyncClient {

   /**
    * This request returns a token to use in subsequent requests. After 30 minutes of inactivity,
    * the token expires and you have to request a new token with this call.
    */
   @POST
   @ResponseParser(ParseLoginResponseFromHeaders.class)
   @Path("v{jclouds.api-version}/login")
   ListenableFuture<VCloudSession> login();
}
