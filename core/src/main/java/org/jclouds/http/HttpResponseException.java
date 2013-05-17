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
package org.jclouds.http;

import java.io.IOException;

import org.jclouds.io.payloads.StringPayload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;

/**
 * Represents an error obtained from an HttpResponse.
 * 
 * @author Adrian Cole
 * 
 */
public class HttpResponseException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   protected final HttpCommand command;
   protected final HttpResponse response;
   private String content;

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response, Throwable cause) {
      super(message, cause);
      this.command = command;
      this.response = response;
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response,
         String content, Throwable cause) {
      super(message, cause);
      this.command = command;
      this.response = response;
      this.content = content;
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, Throwable cause) {
      this(String.format("command: %1$s failed with response: %2$s", command.getCurrentRequest().getRequestLine(),
            response.getStatusLine()), command, response, cause);
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, String content, Throwable cause) {
      this(String.format("command: %1$s failed with response: %2$s; content: [%3$s]", command.getCurrentRequest()
            .getRequestLine(), response.getStatusLine(), content), command, response, content, cause);
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response) {
      super(message);
      this.command = command;
      this.response = response;
   }

   public HttpResponseException(String message, HttpCommand command, @Nullable HttpResponse response, String content) {
      super(message);
      this.command = command;
      this.response = response;
      this.content = content;
   }

   public HttpResponseException(HttpCommand command, HttpResponse response) {
      this(String.format("request: %s %sfailed with response: %s", command.getCurrentRequest().getRequestLine(),
            requestPayloadIfStringOrFormIfNotReturnEmptyString(command.getCurrentRequest()),
            response.getStatusLine()), command, response);
   }

   static String requestPayloadIfStringOrFormIfNotReturnEmptyString(HttpRequest request) {
      if (request.getPayload() != null
            && ("application/x-www-form-urlencoded".equals(request.getPayload().getContentMetadata().getContentType()) || request
                  .getPayload() instanceof StringPayload)
            && request.getPayload().getContentMetadata().getContentLength() != null
            && request.getPayload().getContentMetadata().getContentLength() < 1024) {
         try {
            return String.format(" [%s] ", request.getPayload() instanceof StringPayload ? request.getPayload()
                  .getRawContent() : Strings2.toString(request.getPayload()));
         } catch (IOException e) {
         }
      }
      return "";
   }

   public HttpResponseException(HttpCommand command, HttpResponse response, String content) {
      this(String.format("command: %s failed with response: %s; content: [%s]", command.getCurrentRequest()
            .getRequestLine(), response.getStatusLine(), content), command, response, content);
   }

   public HttpCommand getCommand() {
      return command;
   }

   @Nullable
   public HttpResponse getResponse() {
      return response;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getContent() {
      return content;
   }

}
