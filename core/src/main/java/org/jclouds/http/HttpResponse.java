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
package org.jclouds.http;

import java.io.InputStream;

/**
 * Represents a response produced from {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpResponse extends HttpMessage {
   private int statusCode;
   private String message;
   private InputStream content;

   public HttpResponse() {
   }

   public HttpResponse(InputStream content) {
      this.content = content;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public InputStream getContent() {
      return content;
   }

   public void setContent(InputStream content) {
      this.content = content;
   }

   public String getStatusLine() {
      return String.format("HTTP/1.1 %d %s", getStatusCode(), getMessage());
   }

}