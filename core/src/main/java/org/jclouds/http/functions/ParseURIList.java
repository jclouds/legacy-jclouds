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
package org.jclouds.http.functions;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

/**
 * parses a single URI from a list
 * 
 * @author Adrian Cole
 */
public class ParseURIList implements Function<HttpResponse, URI> {
   public URI apply(HttpResponse from) {
      try {
         checkState("text/uri-list".equals(from.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)),
                  "response should have content type test/uri-list");

         String toParse = Utils.toStringAndClose(from.getContent());
         return URI.create(toParse.trim());
      } catch (IOException e) {
         throw new HttpResponseException("couldn't parse url from response", null, from, e);
      }
   }
}