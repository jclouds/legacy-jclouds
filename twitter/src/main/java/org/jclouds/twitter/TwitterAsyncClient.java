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

package org.jclouds.twitter;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.twitter.domain.Status;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Twitter via their REST API.
 * <p/>
 * 
 * @see TwitterClient
 * @see <a href= "http://apiwiki.twitter.com/Twitter-REST-API-Method" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
public interface TwitterAsyncClient {

   /**
    * @see TwitterClient#getMyMentions()
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/statuses/mentions.json")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Status>> getMyMentions();

}
