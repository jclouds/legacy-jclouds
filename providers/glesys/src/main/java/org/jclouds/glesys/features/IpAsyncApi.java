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
package org.jclouds.glesys.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.options.ListIpOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to IP Addresses via their REST API.
 * <p/>
 *
 * @author Adrian Cole, Mattias Holmqvist, Adam Lowe
 * @see IpApi
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface IpAsyncApi {
   /**
    * @see IpApi#listFree
    */
   @GET
   @Path("/ip/listfree/ipversion/{ipversion}/datacenter/{datacenter}/platform/{platform}/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("ipaddresses")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<String>> listFree(@PathParam("ipversion") int ipversion,
                                          @PathParam("datacenter") String datacenter,
                                          @PathParam("platform") String platform);

   /**
    * @see IpApi#take
    */
   @POST
   @Path("/ip/take/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> take(@FormParam("ipaddress") String ipAddress);

   /**
    * @see IpApi#release
    */
   @POST
   @Path("/ip/release/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> release(@FormParam("ipaddress") String ipAddress);

   /**
    * @see IpApi#list
    */
   @GET
   @Path("/ip/listown/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("iplist")
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<IpDetails>> list(ListIpOptions... options);

   /**
    * @see IpApi#get
    */
   @GET
   @Path("/ip/details/ipaddress/{ipaddress}/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<IpDetails> get(@PathParam("ipaddress") String ipAddress);

   /**
    * @see IpApi#addToServer
    */
   @POST
   @Path("/ip/add/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> addToServer(@FormParam("ipaddress") String ipAddress,
                                             @FormParam("serverid") String serverId);

   /**
    * @see IpApi#removeFromServer
    */
   @POST
   @Path("/ip/remove/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> removeFromServer(@FormParam("ipaddress") String ipAddress,
                                                  @FormParam("serverid") String serverId);

   /**
    * @see IpApi#removeFromServer
    */
   @POST
   @FormParams(keys = "release", values = "true")
   @Path("/ip/remove/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> removeFromServerAndRelease(@FormParam("ipaddress") String ipAddress,
                                                            @FormParam("serverid") String serverId);

   /**
    * @see IpApi#setPtr
    */
   @POST
   @Path("/ip/setptr/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> setPtr(@FormParam("ipaddress") String ipAddress,
                                      @FormParam("data") String ptr);

   /**
    * @see IpApi#resetPtr
    */
   @POST
   @Path("/ip/resetptr/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<IpDetails> resetPtr(@FormParam("ipaddress") String ipAddress);


}
