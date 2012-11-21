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
package org.jclouds.openstack.nova.v2_0.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
// TODO: is there error spec someplace? let's type errors, etc.
@Singleton
public class NovaErrorHandler implements HttpErrorHandler {

  @Resource
  @Named(ComputeServiceConstants.COMPUTE_LOGGER)
  protected Logger logger = Logger.NULL;

   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);
      String body = data != null ? new String(data) : null;
      String message = body;

      Exception exception = message != null ? new HttpResponseException(command, response, message)
               : new HttpResponseException(command, response);
      String requestLine = command.getCurrentRequest().getRequestLine();
      message = message != null ? message : String.format("%s -> %s", requestLine,
                                                          response.getStatusLine());
      switch (response.getStatusCode()) {
         case 400:
            if (message.indexOf("quota exceeded") != -1)
               exception = new InsufficientResourcesException(message, exception);
            else if (message.indexOf("has no fixed_ips") != -1)
               exception = new IllegalStateException(message, exception);
            else if (message.indexOf("already exists") != -1)
               exception = new IllegalStateException(message, exception);
            break;
         case 401:
         case 403:
            exception = new AuthorizationException(message, exception);
            break;
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
            }
            break;
         case 413:
            exception = buildRetryAfterException(requestLine, message, body, exception);
            break;
      }
      command.setException(exception);
   }

  /**
   * Build an exception from the response. If it contains the JSON payload then that is parsed to create a
   * {@link RetryAfterException}, otherwise a {@link InsufficientResourcesException } is created
   * The expected body contains the time as in this (real) response
   * <pre>
   *   {
   * "overLimit" : {
   *  "code" : 413,
   *  "message" : "OverLimit Retry...",
   *  "details" : "Error Details...",
   *  "retryAt" : "2012-11-14T21:51:28UTC"
   *  }
   * }
   * </pre>
   *
   * @param requestLine
   * @param message message body
   * @param body
   * @param exception the exception raised  @return either a RetryAfterException, or something else
   */
  @VisibleForTesting
  Exception buildRetryAfterException(String requestLine, String message, String body, Exception exception) {
     // try to parse the payload as JSON
     try {
        Date date = parseRetryDate(body);
        return new RetryAfterException(requestLine + " failed - retry after" + date.toString(),
                                       null,
                                       date.getTime());
     } catch (Exception e) {
        logger.warn("Failed to parse " + body, e);
        //parse failure or JSON of the wrong format. Either way, fall back
        return new InsufficientResourcesException(message, exception);
     }
  }

   /**
    * The extraction and parsing of the retry time from JSON is isolated for better testing.
    * @param json the json to parse
    * @return the date of that parsed event
    * @throws ParseException on a parsing error
    * @throws ClassCastException if the JSON tree isn't as expected
    * @throws NullPointerException if part of the tree is missing.
    */
   @VisibleForTesting
   Date parseRetryDate(String jsonText) throws ParseException {
      JsonParser parse = new JsonParser();
      JsonObject json = (JsonObject) parse.parse(jsonText);
      String retryTime = ((JsonObject) json.get("overLimit")).get("retryAt").getAsString();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssz", Locale.US);
      return sdf.parse(retryTime);
   }
}
