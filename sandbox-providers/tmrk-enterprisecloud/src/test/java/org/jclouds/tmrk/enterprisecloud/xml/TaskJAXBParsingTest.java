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
package org.jclouds.tmrk.enterprisecloud.xml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.Tasks;
import org.jclouds.tmrk.enterprisecloud.features.TaskAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Set;

import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of JAXB parsing for Task/Tasks
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TaskHandlerTest")
public class TaskJAXBParsingTest extends BaseRestClientTest {
   private SimpleDateFormatDateService dateService;
   private Task expected1;
   private Task expected2;

  @BeforeMethod
  public void setUp() {
     dateService = new SimpleDateFormatDateService();
     expected1 = Task
         .builder()
         .href(URI.create("/livespec/tasks/1002"))
         .type("application/vnd.tmrk.cloud.task")
         .operation("Add Node Service")
         .status(Task.Status.ERROR)
         .impactedItem(
               NamedResource.builder().href(URI.create("/livespec/nodeservices/1")).name("sample node internet 1")
                     .type("application/vnd.tmrk.cloud.nodeService").build())
         .startTime(dateService.iso8601DateParse("2011-11-07T11:19:13.38225Z"))
         .completedTime(dateService.iso8601DateParse("2011-11-07T11:20:13.38225Z"))
         .notes("Some notes about the operation.")
         .errorMessage("sample error message 1 here")
         .initiatedBy(
               NamedResource.builder().href(URI.create("/livespec/admin/users/1")).name("User 1")
                     .type("application/vnd.tmrk.cloud.admin.user").build()).build();

      expected2 = Task
         .builder()
         .href(URI.create("/livespec/tasks/1003"))
         .type("application/vnd.tmrk.cloud.task")
         .operation("Add Node Service 2")
         .status(Task.Status.SUCCESS)
         .impactedItem(
               NamedResource.builder().href(URI.create("/livespec/nodeservices/2")).name("sample node internet 2")
                     .type("application/vnd.tmrk.cloud.nodeService").build())
         .startTime(dateService.iso8601DateParse("2011-11-11T11:19:13.38225Z"))
         .completedTime(dateService.iso8601DateParse("2011-11-11T11:20:13.38225Z"))
         .notes("Some notes about the operation.")
         .errorMessage("sample success message 1 here")
         .initiatedBy(
               NamedResource.builder().href(URI.create("/livespec/admin/users/3")).name("User 3")
                     .type("application/vnd.tmrk.cloud.admin.user").build()).build();
  }

   @BeforeClass
   void setupFactory() {
   RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
        "credentialFoo", String.class, Integer.class,
        ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

            @Override
            protected void configure() {}

            @SuppressWarnings("unused")
            @Provides
            @Named("exception")
            Set<String> exception() {
                throw new AuthorizationException();
            }

        }));

      injector = createContextBuilder(contextSpec).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

   @Test
   public void testParseTaskWithJAXB() throws Exception {

      Method method = TaskAsyncClient.class.getMethod("getTask",URI.class);
      HttpRequest request = factory(TaskAsyncClient.class).createRequest(method, new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, Task> parser = (Function<HttpResponse, Task>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/task.xml");
      Task task = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));
      assertEquals(task, expected1);
   }

   @Test
   public void testParseTasksWithJAXB() throws Exception {

      Method method = TaskAsyncClient.class.getMethod("getTasksInEnvironment",URI.class);
      HttpRequest request = factory(TaskAsyncClient.class).createRequest(method,new URI("/1"));
      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);

      Function<HttpResponse, Tasks> parser = (Function<HttpResponse, Tasks>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      InputStream is = getClass().getResourceAsStream("/tasks.xml");
      Tasks tasksResponse = parser.apply(new HttpResponse(200, "ok", newInputStreamPayload(is)));

      Set<Task> tasks = tasksResponse.getTasks();
      assertEquals(tasks.size(), 2);
      assertTrue(tasks.contains(expected1));
      assertTrue(tasks.contains(expected2));
   }
}
