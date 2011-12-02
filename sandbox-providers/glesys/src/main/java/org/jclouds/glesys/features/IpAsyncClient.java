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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to IP Addresses via their REST API.
 * <p/>
 *
 * @author Adrian Cole
 * @see ServerClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface IpAsyncClient {


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

}
