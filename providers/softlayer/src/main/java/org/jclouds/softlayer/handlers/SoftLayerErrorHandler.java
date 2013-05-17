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
package org.jclouds.softlayer.handlers;

import java.io.IOException;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class SoftLayerErrorHandler implements HttpErrorHandler {

   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      String message = parseMessage(response);
      Exception exception = message != null ? new HttpResponseException(command, response, message)
               : new HttpResponseException(command, response);
      try {
         message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
                  response.getStatusLine());
         switch (response.getStatusCode()) {
            case 401:
            case 403:
               exception = new AuthorizationException(message, exception);
               break;
            case 404:
               if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
                  exception = new ResourceNotFoundException(message, exception);
               }
               break;
            case 500:
               if (message != null ){
                  if (message.indexOf("Unable to determine package for") != -1) {
                     exception = new ResourceNotFoundException(message, exception);
                  } else if (message.indexOf("currently an active transaction") != -1) {
                     exception = new IllegalStateException(message, exception);
                  }
               }
         }
      } finally {
         Closeables.closeQuietly(response.getPayload());
         command.setException(exception);
      }
   }

   public String parseMessage(HttpResponse response) {
      if (response.getPayload() == null)
         return null;
      try {
         return Strings2.toString(response.getPayload());
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         try {
            response.getPayload().getInput().close();
         } catch (IOException e) {
            Throwables.propagate(e);
         }
      }
   }
}
