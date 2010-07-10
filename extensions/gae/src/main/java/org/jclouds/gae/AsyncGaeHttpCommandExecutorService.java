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
package org.jclouds.gae;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;


import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpResponse;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Google App Engine version of {@link HttpCommandExecutorService} using their fetchAsync call
 * 
 * @author Adrian Cole
 */
@SingleThreaded
@Singleton
public class AsyncGaeHttpCommandExecutorService implements HttpCommandExecutorService {

   private final URLFetchService urlFetchService;
   private final ConvertToGaeRequest convertToGaeRequest;
   private final ConvertToJcloudsResponse convertToJcloudsResponse;

   @Inject
   public AsyncGaeHttpCommandExecutorService(URLFetchService urlFetchService,
            ConvertToGaeRequest convertToGaeRequest,
            ConvertToJcloudsResponse convertToJcloudsResponse) {
      this.urlFetchService = urlFetchService;
      this.convertToGaeRequest = convertToGaeRequest;
      this.convertToJcloudsResponse = convertToJcloudsResponse;
   }

   @Override
   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      // TODO: this needs to handle retrying and filtering
      return Futures.compose(ConcurrentUtils.makeListenable(urlFetchService
               .fetchAsync(convertToGaeRequest.apply(command.getRequest())), sameThreadExecutor()),
               convertToJcloudsResponse);
   }

}
