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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to IP Addresses via their REST API.
 * <p/>
 *
 * @author Adrian Cole, Mattias Holmqvist
 *
 * @see ServerClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface IpAsyncClient {


   /**
    * @see IpClient#take
    */
   @POST
   @Path("/ip/take/format/json")
   ListenableFuture<Void> take(@FormParam("ipaddress") String ipAddress);


   /**
    * @see IpClient#release
    */
   @POST
   @Path("/ip/release/format/json")
   ListenableFuture<Void> release(@FormParam("ipaddress") String ipAddress);

   /**
    * @see IpClient#add
    */
   @POST
   @Path("/ip/add/format/json")
   ListenableFuture<Void> addIpToServer(@FormParam("ipaddress") String ipAddress,
                                        @FormParam("serverid") String serverId);


   /**
    * @see IpClient#remove
    *
    * TODO: add optional release_ip parameter
    */
   @POST
   @Path("/ip/remove/format/json")
   ListenableFuture<Void> removeIpFromServer(@FormParam("ipaddress") String ipAddress,
                                 @FormParam("serverid") String serverId);


   /**
    * @see IpClient#listFree
    */
   @GET
   @Path("/ip/listfree/ipversion/{ipversion}/datacenter/{datacenter}/platform/{platform}/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("iplist")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<String>> listFree(@PathParam("ipversion") String ipversion,
                                          @PathParam("datacenter") String datacenter,
                                          @PathParam("platform") String platform);

   /**
    * @see IpClient#getIpDetails
    */
   @GET
   @Path("/ip/details/ipaddress/{ipaddress}/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("details")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<IpDetails> getIpDetails(@PathParam("ipaddress") String ipAddress);

}
