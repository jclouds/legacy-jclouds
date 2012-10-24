/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.abiquo.binders.BindLinkToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.functions.ReturnNullOn303;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Task API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see TaskApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
public interface TaskAsyncApi {
   /*********************** Task ***********************/

   /**
    * @see TaskApi#getTask(RESTLink)
    */
   @GET
   @Consumes(TaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOn303.class)
   ListenableFuture<TaskDto> getTask(@BinderParam(BindLinkToPath.class) RESTLink link);

   /**
    * @see TaskApi#listTasks(SingleResourceTransportDto)
    */
   @GET
   @Consumes(TasksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   <T extends SingleResourceTransportDto> ListenableFuture<TasksDto> listTasks(
         @EndpointLink("tasks") @BinderParam(BindToPath.class) T dto);
}
