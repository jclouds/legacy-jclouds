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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TaskStatus;
import org.jclouds.trmk.vcloud_0_8.domain.TasksList;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.TaskImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code TasksListHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TasksListHandlerTest")
public class TasksListHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/taskslist.xml");

      TasksList result = factory.create(injector.getInstance(TasksListHandler.class)).parse(is);
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/tasksList/1"));
      Task task1 = new TaskImpl(URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3300"),
               null, TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:30:19.587Z"), dateService
                        .iso8601DateParse("2009-08-24T21:30:32.63Z"), null, new ReferenceTypeImpl("Server1",
                              TerremarkVCloudMediaType.VAPP_XML, URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")), null);
      Task task2 = new TaskImpl(URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3299"),
               null, TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:29:32.983Z"), dateService
                        .iso8601DateParse("2009-08-24T21:29:44.65Z"), null, new ReferenceTypeImpl("Server1",
                              TerremarkVCloudMediaType.VAPP_XML, URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")), null);
      assertEquals(result.getTasks(), ImmutableSortedSet.of(task1, task2));
   }

}
