/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.functions.OrgNameToTasksListEndpoint;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Task functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface TaskAsyncClient {

   /**
    * @see TaskClient#getTasksList
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<TasksList> getTasksList(@EndpointParam URI tasksListId);

   /**
    * @see TaskClient#findTasksListInOrgNamed
    */
   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<TasksList> findTasksListInOrgNamed(
            @Nullable @EndpointParam(parser = OrgNameToTasksListEndpoint.class) String orgName);

   /**
    * @see TaskClient#getTask
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> getTask(@EndpointParam URI taskId);

   /**
    * @see TaskClient#cancelTask
    */
   @POST
   @Path("/action/cancel")
   ListenableFuture<Void> cancelTask(@EndpointParam URI taskId);

}
