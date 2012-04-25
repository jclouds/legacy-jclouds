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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.jenkins.v1.domain.JobDetails;
import org.jclouds.jenkins.v1.filters.BasicAuthenticationUnlessAnonymous;
import org.jclouds.jenkins.v1.functions.ReturnVoidOn302Or404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Job Services
 * 
 * @see JobClient
 * @author Adrian Cole
 * @see <a href="http://ci.jruby.org/computer/api/">api doc</a>
 */
@RequestFilters(BasicAuthenticationUnlessAnonymous.class)
public interface JobAsyncClient {
   
   /**
    * @see JobClient#createFromXML
    */
   @POST
   @Path("/createItem")
   @Produces(MediaType.TEXT_XML)
   ListenableFuture<Void> createFromXML(@QueryParam("name") String displayName, @BinderParam(BindToStringPayload.class) String xml); 
   
   /**
    * @see JobClient#get
    */
   @GET
   @Path("/job/{displayName}/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<JobDetails> get(@PathParam("displayName") String displayName);
   
   /**
    * @see JobClient#delete
    */
   @POST
   @Path("/job/{displayName}/doDelete")
   @ExceptionParser(ReturnVoidOn302Or404.class)
   ListenableFuture<Void> delete(@PathParam("displayName") String displayName);
   
   /**
    * @see JobClient#buildJob
    */
   @POST
   @Path("/job/{displayName}/build")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Void> build(@PathParam("displayName") String displayName);
   
   /**
    * @see JobClient#fetchConfigXML
    */
   @GET
   @Path("/job/{displayName}/config.xml")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<String> fetchConfigXML(@PathParam("displayName") String displayName);
   
}
