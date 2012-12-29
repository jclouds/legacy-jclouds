/*
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
package org.jclouds.googlecompute;

import com.google.common.annotations.Beta;
import org.jclouds.googlecompute.features.OperationAsyncApi;
import org.jclouds.googlecompute.features.ProjectAsyncApi;
import org.jclouds.googlecompute.features.ZoneAsyncApi;
import org.jclouds.rest.annotations.Delegate;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Provides asynchronous access to GoogleCompute via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see GoogleComputeApi
 */
@Beta
public interface GoogleComputeAsyncApi {

   /**
    * Provides asynchronous access to Project features
    */
   @Delegate
   ProjectAsyncApi getProjectApi();

   /**
    * Provides asynchronous access to Operation features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}")
   OperationAsyncApi getOperationApiForProject(@PathParam("project") String projectName);

   /**
    * Provides asynchronous access to Zone features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}")
   ZoneAsyncApi getZoneApiForProject(@PathParam("project") String projectName);
}
