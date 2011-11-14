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
package org.jclouds.trmk.enterprisecloud.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.trmk.enterprisecloud.domain.Task;
import org.jclouds.trmk.enterprisecloud.domain.Tasks;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.net.URI;

/**
 * Provides asynchronous access to Task via their REST API.
 * <p/>
 * 
 * @see TaskClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Headers(keys = "x-trmk-version", values = "{jclouds.api-version}")
public interface TaskAsyncClient {

   /**
    * @see TaskClient#getTasksInEnvironment
    */
   @GET
   @Path("/tasks/environments/{environmentId}")
   @Consumes("application/vnd.tmrk.cloud.task; type=collection")
   @JAXBResponseParser
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Tasks> getTasksInEnvironment(@PathParam("environmentId") long environmentId);

   /**
    * @see TaskClient#getTask
    */
   @GET
   @Consumes("application/vnd.tmrk.cloud.task")
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Task> getTask(@EndpointParam URI taskId);

}
