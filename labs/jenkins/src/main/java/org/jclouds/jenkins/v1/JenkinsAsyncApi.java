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
package org.jclouds.jenkins.v1;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.jenkins.v1.domain.Node;
import org.jclouds.jenkins.v1.features.ComputerAsyncApi;
import org.jclouds.jenkins.v1.features.JobAsyncApi;
import org.jclouds.jenkins.v1.filters.BasicAuthenticationUnlessAnonymous;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Jenkins via their REST API.
 * <p/>
 * 
 * @see JenkinsApi
 * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API">api doc</a>
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthenticationUnlessAnonymous.class)
public interface JenkinsAsyncApi {
   
   /**
    * @see JenkinsApi#getMaster
    */
   @Named("GetMaster")
   @GET
   @Path("/api/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Node> getMaster();
   
   /**
    * Provides asynchronous access to Computer features.
    */
   @Delegate
   ComputerAsyncApi getComputerApi();
   
   /**
    * Provides asynchronous access to Job features.
    */
   @Delegate
   JobAsyncApi getJobApi();
}
