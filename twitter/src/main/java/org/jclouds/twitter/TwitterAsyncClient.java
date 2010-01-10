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
package org.jclouds.twitter;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.functions.ParseStatusesFromJsonResponse;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Twitter via their REST API.
 * <p/>
 * 
 * @see TwitterClient
 * @see <a href= "http://apiwiki.twitter.com/Twitter-REST-API-Method" />
 * @author Adrian Cole
 */
@Endpoint(Twitter.class)
@RequestFilters(BasicAuthentication.class)
public interface TwitterAsyncClient {

   /**
    * @see TwitterClient#getMyMentions()
    */
   @GET
   @ResponseParser(ParseStatusesFromJsonResponse.class)
   @Path("/statuses/mentions.json")
   ListenableFuture<SortedSet<Status>> getMyMentions();

}
