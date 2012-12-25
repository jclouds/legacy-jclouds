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
package org.jclouds.openstack.swift.v1.features;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.functions.ParseAccountMetadataResponseFromHeaders;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Storage Account Services
 * 
 * @see AccountApi
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-account-services.html"
 *      >api doc</a>
 */
@RequestFilters(AuthenticateRequest.class)
public interface AccountAsyncApi {

   /**
    * @see AccountApi#get
    */
   @HEAD
   @ResponseParser(ParseAccountMetadataResponseFromHeaders.class)
   @Path("/")
   ListenableFuture<? extends Account> get();

}
