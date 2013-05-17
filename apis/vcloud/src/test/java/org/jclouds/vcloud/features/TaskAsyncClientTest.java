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

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.net.URI;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code TaskAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TaskAsyncClientTest")
public class TaskAsyncClientTest extends BaseVCloudAsyncClientTest<TaskAsyncClient> {

   public void testGetTasksList() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncClient.class, "getTasksList", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/tasksList/1")));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindTasksListInOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncClient.class, "findTasksListInOrgNamed", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("org"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncClient.class, "getTask", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1")));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/task/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCancelTask() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncClient.class, "cancelTask", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1")));

      assertRequestLineEquals(request, "POST https://vcenterprise.bluelock.com/api/v1.0/task/1/action/cancel HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

}
