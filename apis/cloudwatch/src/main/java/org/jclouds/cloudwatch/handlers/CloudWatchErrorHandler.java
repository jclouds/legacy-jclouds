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
package org.jclouds.cloudwatch.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.annotations.Beta;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.InsufficientResourcesException;

/**
 * @author Jeremy Whitlock
 */
@Beta
@Singleton
public class CloudWatchErrorHandler implements HttpErrorHandler {

   private final ParseSax.Factory factory;
   private final Provider<ErrorHandler> handlers;

   @Inject
   CloudWatchErrorHandler(ParseSax.Factory factory, Provider<ErrorHandler> handlers) {
      this.factory = factory;
      this.handlers = handlers;
   }

   @Override
   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         if (response.getPayload() != null) {
            AWSError error = factory.create(handlers.get())
                                    .parse(new String(closeClientButKeepContentStream(response)));
            exception = refineException(new AWSResponseException(command, response, error));
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   private Exception refineException(AWSResponseException in) {
      int statusCode = in.getResponse().getStatusCode();
      String errorCode = in.getError().getCode();
      String message = in.getError().getMessage();

      if (statusCode == 400) {
         if ("LimitExceeded".equals(errorCode)) {
            return new InsufficientResourcesException(message, in);
         } else if ("InvalidFormat".equals(errorCode)) {
            return new IllegalArgumentException(message, in);
         }
      }

      return in;
   }

}
