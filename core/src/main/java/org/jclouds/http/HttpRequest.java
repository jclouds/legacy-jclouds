/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.command.Request;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

/**
 * Represents a request that can be executed within {@link HttpFutureCommandClient}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage implements Request<URI> {

   private URI endPoint;
   private final HttpMethod method;
   private final String uri;
   Object payload;

   @Resource
   protected Logger logger = Logger.NULL;

   /**
    * 
    * @param endPoint
    *           initial endPoint (ex. http://host:port ). This may change over the life of the
    *           request due to redirects.
    * @param method
    * @param uri
    */
   public HttpRequest(URI endPoint, HttpMethod method, String uri) {
      this.endPoint = checkNotNull(endPoint, "endPoint");
      this.method = checkNotNull(method, "method");
      this.uri = Utils.encodeUriPath(checkNotNull(uri, "uri"));
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("HttpRequest");
      sb.append("{endPoint='").append(endPoint).append('\'');
      sb.append(", method='").append(method).append('\'');
      sb.append(", uri='").append(uri).append('\'');
      sb.append(", headers=").append(headers);
      sb.append(", payload set=").append(payload != null);
      sb.append('}');
      return sb.toString();
   }

   public HttpMethod getMethod() {
      return method;
   }

   public String getUri() {
      return uri;
   }

   public boolean isReplayable() {
      Object content = getPayload();
      if (content != null && content instanceof InputStream) {
         logger.warn("%1$s: InputStreams are not replayable", toString());
         return false;
      }
      return true;
   }

   public Object getPayload() {
      return payload;
   }

   public void setPayload(Object content) {
      this.payload = content;
   }

   /**
    * only the scheme, host, and port of the URI designates the endpoint
    */
   public void setEndPoint(URI endPoint) {
      this.endPoint = endPoint;
   }

   /**
    * only the scheme, host, and port of the URI designates the endpoint
    */
   public URI getEndPoint() {
      return endPoint;
   }

}
