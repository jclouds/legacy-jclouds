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
package org.jclouds.http.functions;

import static com.google.common.net.HttpHeaders.ETAG;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;

/**
 * Parses an MD5 checksum from the header {@link com.google.common.net.HttpHeaders.HttpHeaders#ETAG}.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseETagHeader implements Function<HttpResponse, String> {

   public String apply(HttpResponse from) {
      releasePayload(from);
      String eTag = from.getFirstHeaderOrNull(ETAG);
      if (eTag == null) {
         // TODO: Cloud Files sends incorrectly cased ETag header... Remove this when fixed.
         eTag = from.getFirstHeaderOrNull("Etag");
      }
      if (eTag != null) {
         return eTag;
      }
      throw new HttpException("did not receive ETag");
   }

}
