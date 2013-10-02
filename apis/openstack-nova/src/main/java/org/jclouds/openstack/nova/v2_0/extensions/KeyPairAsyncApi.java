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
package org.jclouds.openstack.nova.v2_0.extensions;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.binders.BindKeyPairToJsonPayload;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.functions.internal.ParseKeyPairs;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Key Pairs via the REST API.
 * <p/>
 * 
 * @see KeyPairApi
 * @author Jeremy Daggett
 * @see ExtensionAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/Extensions-d1e1444.html"
 *      />
 * @see <a href="http://nova.openstack.org/api_ext" />
 * @see <a href="http://nova.openstack.org/api_ext/ext_keypairs.html" />
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.KEYPAIRS)
@RequestFilters(AuthenticateRequest.class)
public interface KeyPairAsyncApi {

   @Named("keypair:list")
   @GET
   @Path("/os-keypairs")
   @ResponseParser(ParseKeyPairs.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<? extends FluentIterable<? extends KeyPair>> list();

   @Named("keypair:create")
   @POST
   @Path("/os-keypairs")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Payload("%7B\"keypair\":%7B\"name\":\"{name}\"%7D%7D")
   ListenableFuture<? extends KeyPair> create(@PayloadParam("name") String name);

   @Named("keypair:create")
   @POST
   @Path("/os-keypairs")
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindKeyPairToJsonPayload.class)
   ListenableFuture<? extends KeyPair> createWithPublicKey(@PayloadParam("name") String name,
         @PayloadParam("public_key") String publicKey);

   @Named("keypair:delete")
   @DELETE
   @Path("/os-keypairs/{name}")
   @Fallback(FalseOnNotFoundOr404.class)
   @Consumes
   ListenableFuture<Boolean> delete(@PathParam("name") String name);

}
