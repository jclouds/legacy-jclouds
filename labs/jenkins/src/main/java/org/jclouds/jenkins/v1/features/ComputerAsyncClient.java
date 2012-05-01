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
package org.jclouds.jenkins.v1.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.jenkins.v1.domain.Computer;
import org.jclouds.jenkins.v1.domain.ComputerView;
import org.jclouds.jenkins.v1.filters.BasicAuthenticationUnlessAnonymous;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Computer Services
 * 
 * @see ComputerClient
 * @author Adrian Cole
 * @see <a href=
 *      "http://ci.jruby.org/computer/api/"
 *      >api doc</a>
 */
@RequestFilters(BasicAuthenticationUnlessAnonymous.class)
public interface ComputerAsyncClient {

   /**
    * @see ComputerClient#getView
    */
   @GET
   @Path("/computer/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ComputerView> getView();
   
   /**
    * @see ComputerClient#get
    */
   @GET
   @Path("/computer/{displayName}/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Computer> get(@PathParam("displayName") String displayName);

}
