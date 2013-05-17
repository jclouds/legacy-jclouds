/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dynect.v3.handlers;
import static com.google.common.net.HttpHeaders.LOCATION;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;

/**
 * if the redirection URL is a Job, do not replay the original request; just get
 * the job.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetJobRedirectionRetryHandler extends RedirectionRetryHandler {
   
   private final Payload emptyPayload;

   @Inject
   protected GetJobRedirectionRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      super(backoffHandler);
      this.emptyPayload = Payloads.newPayload(new byte[]{});
      this.emptyPayload.getContentMetadata().setContentType(APPLICATION_JSON);     
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      String location = response.getFirstHeaderOrNull(LOCATION);
      if (location != null && location.indexOf("Job") != -1) {
         HttpRequest getRequest = command.getCurrentRequest().toBuilder()
                                                             .method(GET)
                                                             .payload((Payload) null).build();
         command.setCurrentRequest(getRequest);
      }
      return super.shouldRetryRequest(command, response);
   }
}
