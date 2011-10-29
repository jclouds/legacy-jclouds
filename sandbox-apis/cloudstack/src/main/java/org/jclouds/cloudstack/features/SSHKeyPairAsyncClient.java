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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.SSHKeyPair;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.ListSSHKeyPairsOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface SSHKeyPairAsyncClient {
   /**
    * @see org.jclouds.cloudstack.features.SSHKeyPairClient#listSSHKeyPairs
    */
   @GET
   @QueryParams(keys = "command", values = "listSSHKeyPairs")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<SSHKeyPair>> listSSHKeyPairs(ListSSHKeyPairsOptions... options);

   @GET
   @QueryParams(keys = "command", values = "createSSHKeyPair")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<SSHKeyPair> createSSHKeyPair(@QueryParam("name") String name);

   @GET
   @QueryParams(keys = "command", values = "listSSHKeyPairs")
   @SelectJson("keypair")
   @OnlyElement()
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SSHKeyPair> getSSHKeyPair(@QueryParam("name") String name);

}
