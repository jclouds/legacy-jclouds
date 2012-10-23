/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;

/**
 * Appends the Api version to the given mime type.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AppendApiVersionToAbiquoMimeType implements Function<String, String> {
   /** The prefix for Abiquo custom media types. */
   private static final String ABIQUO_MIME_TYPE_PREFIX = "application/vnd.abiquo.";

   /** The version to append to media types without version. */
   protected String apiVersion;

   @Inject
   public AppendApiVersionToAbiquoMimeType(@ApiVersion final String apiVersion) {
      super();
      this.apiVersion = checkNotNull(apiVersion, "apiVersion");
   }

   @Override
   public String apply(final String input) {
      MediaType mediaType = MediaType.valueOf(checkNotNull(input, "input"));
      if (isAbiquoMimeType(input) && !mediaType.getParameters().containsKey("version")) {
         return mediaType.toString() + ";version=" + apiVersion;
      } else {
         return mediaType.toString();
      }
   }

   private static boolean isAbiquoMimeType(final String mimeType) {
      return mimeType.startsWith(ABIQUO_MIME_TYPE_PREFIX);
   }

}
