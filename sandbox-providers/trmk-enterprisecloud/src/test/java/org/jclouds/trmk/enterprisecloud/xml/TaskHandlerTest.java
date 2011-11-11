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
package org.jclouds.trmk.enterprisecloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.enterprisecloud.domain.NamedResource;
import org.jclouds.trmk.enterprisecloud.domain.Task;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TaskHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TaskHandlerTest")
public class TaskHandlerTest {
   static SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();
   static Task expected = Task
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

   public void test() {
      InputStream is = getClass().getResourceAsStream("/task.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      assertEquals(result.toString(), expected.toString());
   }
}
