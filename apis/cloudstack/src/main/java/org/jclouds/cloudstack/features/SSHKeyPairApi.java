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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.ListSSHKeyPairsOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack SSHKeyPair features.
 *
 * @author Vijay Kiran
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.8/TOC_User.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface SSHKeyPairApi {
   /**
    * Returns a list of {@link SshKeyPair}s registered by current user.
    *
    * @param options if present, how to constrain the list
    * @return Set of {@link SshKeyPair}s matching the current constrains or
    *         empty set if no SshKeyPairs found.
    */
   @Named("listSSHKeyPairs")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSSHKeyPairs", "true" })
   @SelectJson("sshkeypair")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<SshKeyPair> listSSHKeyPairs(ListSSHKeyPairsOptions... options);

   /**
    * Registers a {@link SshKeyPair} with the given name and  public kay material.
    *
    * @param name      of the keypair
    * @param publicKey Public key material of the keypair
    * @return Created SshKeyPair.
    */
   @Named("registerSSHKeyPair")
   @GET
   @QueryParams(keys = "command", values = "registerSSHKeyPair")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   SshKeyPair registerSSHKeyPair(@QueryParam("name") String name, @QueryParam("publickey") String publicKey);

   /**
    * Creates a {@link SshKeyPair} with specified name.
    *
    * @param name of the SshKeyPair.
    * @return Created SshKeyPair.
    */
   @Named("createSSHKeyPair")
   @GET
   @QueryParams(keys = "command", values = "createSSHKeyPair")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   SshKeyPair createSSHKeyPair(@QueryParam("name") String name);

   /**
    * Retrieves the {@link SSHKeyPairApi} with given name.
    *
    * @param name name of the key pair
    * @return SSH Key pair or null if not found.
    */
   @Named("listSSHKeyPairs")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listSSHKeyPairs", "true" })
   @SelectJson("sshkeypair")
   @OnlyElement()
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   SshKeyPair getSSHKeyPair(@QueryParam("name") String name);

   /**
    * Deletes the {@link SSHKeyPairApi} with given name.
    *
    * @param name name of the key pair
    * @return
    */
   @Named("deleteSSHKeyPair")
   @GET
   @QueryParams(keys = "command", values = "deleteSSHKeyPair")
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteSSHKeyPair(@QueryParam("name") String name);

}
