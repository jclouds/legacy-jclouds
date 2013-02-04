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

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.jenkins.v1.JenkinsFallbacks.VoidOn302Or404;
import org.jclouds.jenkins.v1.binders.BindMapToOptionalParams;
import org.jclouds.jenkins.v1.domain.JobDetails;
import org.jclouds.jenkins.v1.domain.LastBuild;
import org.jclouds.jenkins.v1.filters.BasicAuthenticationUnlessAnonymous;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Job Services
 * 
 * @see JobApi
 * @author Adrian Cole
 * @see <a href="http://ci.jruby.org/computer/api/">api doc</a>
 */
@RequestFilters(BasicAuthenticationUnlessAnonymous.class)
public interface JobAsyncApi {
   
   /**
    * @see JobApi#createFromXML
    */
   @Named("CreateItem")
   @POST
   @Path("/createItem")
   @Produces(MediaType.TEXT_XML)
   ListenableFuture<Void> createFromXML(@QueryParam("name") String displayName, @BinderParam(BindToStringPayload.class) String xml); 
   
   /**
    * @see JobApi#get
    */
   @Named("GetJob")
   @GET
   @Path("/job/{displayName}/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<JobDetails> get(@PathParam("displayName") String displayName);
   
   /**
    * @see JobApi#delete
    */
   @Named("DeleteJob")
   @POST
   @Path("/job/{displayName}/doDelete")
   @Fallback(VoidOn302Or404.class)
   ListenableFuture<Void> delete(@PathParam("displayName") String displayName);
   
   /**
    * @see JobApi#buildJob
    */
   @Named("Build")
   @POST
   @Path("/job/{displayName}/build")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Void> build(@PathParam("displayName") String displayName);
   
   /**
    * @see JobApi#buildJobWithParameters
    */
   @Named("BuildWithParameters")
   @POST
   @Path("/job/{displayName}/buildWithParameters")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Void> buildWithParameters(@PathParam("displayName") String displayName,
            @BinderParam(BindMapToOptionalParams.class) Map<String, String> parameters);
   
   /**
    * @see JobApi#fetchConfigXML
    */
   @Named("GetConfigXML")
   @GET
   @Path("/job/{displayName}/config.xml")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<String> fetchConfigXML(@PathParam("displayName") String displayName);
   
   /**
    * @see JobApi#lastBuild
    */
   @Named("GetLastBuild")
   @GET
   @Path("/job/{displayName}/lastBuild/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<LastBuild> lastBuild(@PathParam("displayName") String displayName);
}
