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
package org.jclouds.rackspace.cloudfiles.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.CloudFilesAuthentication.AuthenticationResponse;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;

import com.google.common.base.Function;

/**
 * This parses {@link AuthenticationResponse} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseAuthenticationResponseFromHeaders implements
         Function<HttpResponse, AuthenticationResponse> {

   /**
    * parses the http response headers to create a new {@link AuthenticationResponse} object.
    */
   public AuthenticationResponse apply(final HttpResponse from) {

      return new AuthenticationResponse() {

         public String getAuthToken() {
            return checkNotNull(from.getFirstHeaderOrNull(CloudFilesHeaders.AUTH_TOKEN),
                     CloudFilesHeaders.AUTH_TOKEN);
         }

         public URI getCDNManagementUrl() {
            String cdnManagementUrl = checkNotNull(from
                     .getFirstHeaderOrNull(CloudFilesHeaders.CDN_MANAGEMENT_URL),
                     CloudFilesHeaders.CDN_MANAGEMENT_URL);
            return URI.create(cdnManagementUrl);
         }

         public URI getStorageUrl() {
            String storageUrl = checkNotNull(from
                     .getFirstHeaderOrNull(CloudFilesHeaders.STORAGE_URL),
                     CloudFilesHeaders.STORAGE_URL);
            return URI.create(storageUrl);
         }

      };

   }
}