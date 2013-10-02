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

import org.jclouds.Fallbacks;
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

/**
 * Provides access to Task functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface TaskApi {

   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   TasksList getTasksList(@EndpointParam URI tasksListId);

   @GET
   @Consumes(TASKSLIST_XML)
   @XMLResponseParser(TasksListHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   TasksList findTasksListInOrgNamed(@Nullable @EndpointParam(parser = OrgNameToTasksListEndpoint.class) String orgName);

   /**
    * Whenever the result of a request cannot be returned immediately, the server creates a Task
    * object and includes it in the response, as a member of the Tasks container in the response
    * body. Each Task has an href value, which is a URL that the client can use to retrieve the Task
    * element alone, without the rest of the response in which it was contained. All information
    * about the task is included in the Task element when it is returned in the response’s Tasks
    * container, so a client does not need to make an additional request to the Task URL unless it
    * wants to follow the progress of a task that was incomplete.
    */
   @GET
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Task getTask(@EndpointParam URI taskId);

   @POST
   @Path("/action/cancel")
   void cancelTask(@EndpointParam URI taskId);

}
