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
package org.jclouds.openstack.swift.extensions;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.functions.ParseTemporaryUrlKeyFromHeaders;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;

import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author Andrei Savu
 * @see TemporaryUrlKeyApi
 */
@SkipEncoding('/')
@RequestFilters(AuthenticateRequest.class)
@Endpoint(Storage.class)
public interface TemporaryUrlKeyAsyncApi {

   /**
    * @see TemporaryUrlKeyApi#getTemporaryUrlKey
    */
   @HEAD
   @Path("/")
   @ResponseParser(ParseTemporaryUrlKeyFromHeaders.class)
   ListenableFuture<String> getTemporaryUrlKey();

   /**
    * @see TemporaryUrlKeyApi#setTemporaryUrlKey
    */
   @POST
   @Path("/")
   ListenableFuture<Void> setTemporaryUrlKey(@HeaderParam(SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY) String key);

}
