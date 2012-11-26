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
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;

import java.text.ParseException;
import java.util.Date;

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
   protected Logger logger = Logger.NULL;
   protected final DateService dateService;

   @Inject
   public NovaErrorHandler(DateService dateService) {
      this.dateService = dateService;
   }

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
            exception = buildRetryException(requestLine, message, body, exception, new Date(System.currentTimeMillis()));
            break;
      }
      command.setException(exception);
   }

  /**
   * Build an exception from the response. If it contains the JSON payload then that is parsed to create a
   * {@link RetryLaterException}, otherwise a {@link InsufficientResourcesException } is created
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
   * or
   * <pre>
   *    {
   *      "overLimit": {
   *        "message": "This request was rate-limited.",
   *        "code": 413,
   *        "retryAfter": "54",
   *        "details": "Only 1 POST request(s) can be made to \"*\" every minute."
   *      }
   *    }
   * </pre>
   * @param requestLine the HTTP request line
   * @param message the formatted message for use in other exceptions; this is the one that can be built up differently
   * in the error handler -though it may also be the same as the body parameter
   * @param jsonBody message body
   * @param exception the exception raised
   * @return An exception: a {@link RetryLaterException} if the parsing was successful, an {@link InsufficientResourcesException}
   * if the parsing was unsuccessful
   */
  @VisibleForTesting
  Exception buildRetryException(String requestLine, String message, String jsonBody, Exception exception, Date now) {
     // try to parse the payload as JSON
     try {
        //first look for the retryAfter delta.
        Integer retryAfter = parseRetryAfterField(jsonBody);
        if (retryAfter != null) {
           return new RetryLaterException(requestLine + " failed - retry after " + retryAfter,
                                          retryAfter);
        }
        //next look for the retryAt field.
        Date date = parseRetryAtField(jsonBody);
        if (date != null) {
           return new RetryLaterException(requestLine + " failed - retry at " + date,
                                          date, now);
        } else {
           //parsing failed.
           return new InsufficientResourcesException(message, exception);
        }
     } catch (Exception e) {
        //an error was raised during parsing -which can include badly formatted fields.
        logger.error("Failed to parse " + jsonBody + "", e);
        return new InsufficientResourcesException(message
                                                  + "\n" + " parse failure " + e,
                                                  exception);
     }
  }

   /**
    * The extraction and parsing of the retry time from JSON is isolated for better testing.
    * @param jsonText the text to parse into a JSON structure
    * @return the time in seconds after which a request can be retried
    * @throws ParseException on a error parsing the date
    */
   @VisibleForTesting
   Integer parseRetryAfterField(String jsonText) throws ParseException {
      String retryAt = parseToOverLimitSubElement(jsonText, "retryAfter");
      return (retryAt == null) ? null : Integer.valueOf(retryAt);
   }

   /**
    * The extraction and parsing of the retry time from JSON is isolated for better testing.
    * @param jsonText the text to parse into a JSON structure
    * @return the date of that parsed event, or null if there was none.
    * @throws ParseException on a error parsing the date
    */
   @VisibleForTesting
   Date parseRetryAtField(String jsonText) throws ParseException {
      String retryTime = parseToOverLimitSubElement(jsonText, "retryAt");
      return (retryTime == null) ? null : dateService.iso8601SecondsDateParse(retryTime);
   }

   /**
    * Take the JSON response and parse it to the "overLimit" element
    * @param jsonText the text to parse
    * @return null if there is no overlimit element found.
    * @throws RuntimeException on JSON parse problems.
    */
   private String parseToOverLimitSubElement(String jsonText, String subElement) {
      JsonParser parse = new JsonParser();
      JsonElement rootElt = parse.parse(jsonText);
      if (!(rootElt instanceof JsonObject)) {
         return null;
      }
      JsonObject json = (JsonObject) rootElt;
      JsonObject overLimit = (JsonObject) json.get("overLimit");
      if (overLimit == null) {
         return null;
      }
      JsonElement jsonElement = overLimit.get(subElement);
      if (jsonElement == null) {
         return null;
      }
      return jsonElement.getAsString();
   }
}
