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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.ENTITY;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASK;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.TASKS_LIST;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.Test;

/**
 * Test the {@link TaskApi} by observing its side effects.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "TaskApiExpectTest")
public class TaskApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   static String tasksList = "6f312e42-cd2b-488d-a2bb-97519cd57ed0";
   static URI tasksListHref = URI.create(endpoint + "/tasksList/" + tasksList);
   
   HttpRequest getTasksList = HttpRequest.builder()
            .method("GET")
            .endpoint(tasksListHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getTasksListResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/task/tasksList.xml", TASKS_LIST + ";version=1.5"))
            .build();
    
   @Test
   public void testGetTasksListHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, getTasksList, getTasksListResponse);
      assertEquals(api.getTaskApi().getTasksList(tasksListHref), tasksList());
   }
  
   private TasksList tasksList() {
      return TasksList.builder()
               .name("Tasks Lists")
               .type("application/vnd.vmware.vcloud.tasksList+xml")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/tasksList/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
               .task(task())
               .task(taskTwo())
               .build();
   }
   
   static String task = "5fcd2af3-d0ec-45ce-9451-8c585a2c766b";
   static String taskUrn = "urn:vcloud:task:" + task;
   static URI taskHref = URI.create(endpoint + "/task/" + task);
   
   HttpRequest get = HttpRequest.builder()
            .method("GET")
            .endpoint(taskHref)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();

    HttpResponse getResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/task/task.xml", TASK + ";version=1.5"))
            .build();
    
   @Test
   public void testGetTaskHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, get, getResponse);
      assertEquals(api.getTaskApi().get(taskHref), task());
   }
   
   HttpRequest resolveTask = HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/entity/" + taskUrn)
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token)
            .build();
   
   String taskEntity = asString(createXMLBuilder("Entity").a("xmlns", "http://www.vmware.com/vcloud/v1.5")
                                                             .a("name", taskUrn)
                                                             .a("id", taskUrn)
                                                             .a("type", ENTITY)
                                                             .a("href", endpoint + "/entity/" + taskUrn)
                                  .e("Link").a("rel", "alternate").a("type", TASK).a("href", taskHref.toString()).up());
   
   HttpResponse resolveTaskResponse = HttpResponse.builder()
           .statusCode(200)
           .payload(payloadFromStringWithContentType(taskEntity, ENTITY + ";version=1.5"))
           .build();
   
   @Test
   public void testGetTaskUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveTask, resolveTaskResponse, get, getResponse);
      assertEquals(api.getTaskApi().get(taskUrn), task());
   }

   public static Task task() {
      return Task.builder()
               .type("application/vnd.vmware.vcloud.task+xml")
               .name("task")
               .id("urn:vcloud:task:5fcd2af3-d0ec-45ce-9451-8c585a2c766b")
               .href(URI.create("https://vcloudbeta.bluelock.com/api/task/5fcd2af3-d0ec-45ce-9451-8c585a2c766b"))
               .status("success")
               .operation("Created Catalog QunyingTestCatalog(7212e451-76e1-4631-b2de-ba1dfd8080e4)")
               .operationName("catalogCreateCatalog")
               .startTime(dateService.iso8601DateParse("2012-02-07T00:16:28.450-05:00"))
               .endTime(dateService.iso8601DateParse("2012-02-07T00:16:28.867-05:00"))
               .expiryTime(dateService.iso8601DateParse("2012-05-07T00:16:28.450-04:00"))
               .owner(Reference.builder()
                     .type("application/vnd.vmware.vcloud.catalog+xml")
                     .name("QunyingTestCatalog")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/catalog/7212e451-76e1-4631-b2de-ba1dfd8080e4"))
                     .build())
               .user(Reference.builder()
                     .type("application/vnd.vmware.admin.user+xml")
                     .name("JClouds")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                     .build())
               .org(Reference.builder()
                     .type("application/vnd.vmware.vcloud.org+xml")
                     .name("JClouds")
                     .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
                     .build())
               .build();
   }

   HttpRequest cancel = HttpRequest.builder()
            .method("POST")
            .endpoint(taskHref+ "/action/cancel")
            .addHeader("Accept", "*/*")
            .addHeader("x-vcloud-authorization", token)
            .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + token).build();

   HttpResponse cancelResponse = HttpResponse.builder()
            .statusCode(200)
            .build();
   
   @Test
   public void testCancelTaskHref() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, cancel, cancelResponse);
      api.getTaskApi().cancel(taskHref);
   }
   
   @Test
   public void testCancelTaskUrn() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, resolveTask, resolveTaskResponse, cancel, cancelResponse);
      api.getTaskApi().cancel(taskHref);
   }

   public static Task taskTwo() {
      return Task.builder()
		            .type("application/vnd.vmware.vcloud.task+xml")
		            .name("task")
		            .id("urn:vcloud:task:bd22e745-9c2a-4f82-a954-0e35b6f76ba5")
		            .href(URI.create("https://vcloudbeta.bluelock.com/api/task/bd22e745-9c2a-4f82-a954-0e35b6f76ba5"))
		            .status("success")
		            .operation("Enabled User (967d317c-4273-4a95-b8a4-bf63b78e9c69)")
		            .operationName("jobEnable")
		            .startTime(dateService.iso8601DateParse("2012-02-06T17:30:38.507-05:00"))
		            .endTime(dateService.iso8601DateParse("2012-02-06T17:30:38.507-05:00"))
		            .expiryTime(dateService.iso8601DateParse("2012-05-06T17:30:38.507-04:00"))
		            .user(Reference.builder()
		                  .type("application/vnd.vmware.admin.user+xml")
		                  .name("adrian@jclouds.org")
		                  .href(URI.create("https://vcloudbeta.bluelock.com/api/admin/user/8c360b93-ed25-4c9a-8e24-d48cd9966d93"))
		                  .build())
		            .org(Reference.builder()
		                  .type("application/vnd.vmware.vcloud.org+xml")
		                  .name("JClouds")
		                  .href(URI.create("https://vcloudbeta.bluelock.com/api/org/6f312e42-cd2b-488d-a2bb-97519cd57ed0"))
		                  .build())
		            .build();
   }
}
		
