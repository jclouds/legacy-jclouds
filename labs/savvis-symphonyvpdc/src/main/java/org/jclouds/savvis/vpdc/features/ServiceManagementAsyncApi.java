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
package org.jclouds.savvis.vpdc.features;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.functions.DefaultOrgIfNull;
import org.jclouds.savvis.vpdc.xml.TaskHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface ServiceManagementAsyncApi {

   /**
    * @see VMApi#powerOnVMInVDC
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/{vAppId}/action/powerOn")
   ListenableFuture<Task> powerOnVMInVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("vAppId") String vAppId);

   /**
    * @see VMApi#powerOnVM
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("/action/powerOn")
   ListenableFuture<Task> powerOnVM(@EndpointParam URI vm);

   /**
    * @see VMApi#powerOffVMInVDC
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("v{jclouds.api-version}/org/{billingSiteId}/vdc/{vpdcId}/vApp/{vAppId}/action/powerOff")
   ListenableFuture<Task> powerOffVMInVDC(
            @PathParam("billingSiteId") @Nullable @ParamParser(DefaultOrgIfNull.class) String billingSiteId,
            @PathParam("vpdcId") String vpdcId, @PathParam("vAppId") String vAppId);

   /**
    * @see VMApi#powerOffVM
    */
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Path("/action/powerOff")
   ListenableFuture<Task> powerOffVM(@EndpointParam URI vm);

}
