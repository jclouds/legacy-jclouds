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
package org.jclouds.vcloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TaskAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TaskAsyncClientTest")
public class TaskAsyncClientTest extends BaseVCloudAsyncClientTest<TaskAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TaskAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TaskAsyncClient>>() {
      };
   }

   public void testGetTasksList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TaskAsyncClient.class.getMethod("getTasksList", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/tasksList/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindTasksListInOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TaskAsyncClient.class.getMethod("findTasksListInOrgNamed", String.class);
      HttpRequest request = processor.createRequest(method, "org");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TaskAsyncClient.class.getMethod("getTask", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/task/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCancelTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TaskAsyncClient.class.getMethod("cancelTask", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1"));

      assertRequestLineEquals(request, "POST https://vcenterprise.bluelock.com/api/v1.0/task/1/action/cancel HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

}
