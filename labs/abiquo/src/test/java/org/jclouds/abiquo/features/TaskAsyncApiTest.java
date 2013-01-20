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

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.abiquo.AbiquoFallbacks.NullOn303;
import org.jclouds.abiquo.domain.CloudResources;
import org.jclouds.abiquo.domain.TemplateResources;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code TaskAsyncApi}
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "TaskAsyncApiTest")
public class TaskAsyncApiTest extends BaseAbiquoAsyncApiTest<TaskAsyncApi> {
   /*********************** Task ***********************/

   public void testGetTaskVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncApi.class, "getTask", RESTLink.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(new RESTLink("task",
                  "http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines/1"
                        + "/tasks/169f1877-5f17-4f62-9563-974001295c54"))));

      assertRequestLineEquals(request,
            "GET http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/"
                  + "tasks/169f1877-5f17-4f62-9563-974001295c54 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TaskDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOn303.class);

      checkFilters(request);
   }

   public void testListTasksVirtualMachine() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncApi.class, "listTasks", SingleResourceTransportDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(CloudResources.virtualMachinePut())));

      assertRequestLineEquals(request,
            "GET http://localhost/api/cloud/virtualdatacenters/1/virtualappliances/1/virtualmachines/1/tasks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TasksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetTaskVirtualMachineTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncApi.class, "getTask", RESTLink.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(new RESTLink("task",
                  "http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1/"
                        + "tasks/169f1877-5f17-4f62-9563-974001295c54"))));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/virtualmachinetemplates/1"
                  + "/tasks/169f1877-5f17-4f62-9563-974001295c54 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TaskDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOn303.class);

      checkFilters(request);
   }

   public void testListTasksVirtualMachineTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TaskAsyncApi.class, "listTasks", SingleResourceTransportDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(TemplateResources.virtualMachineTemplatePut())));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1/"
            + "virtualmachinetemplates/1/tasks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TasksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
