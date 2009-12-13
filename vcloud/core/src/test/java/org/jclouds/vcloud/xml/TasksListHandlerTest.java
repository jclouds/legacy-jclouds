/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code TasksListHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TasksListHandlerTest")
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
      Task task1 = new TaskImpl(VCloudMediaType.TASK_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3300"),
               TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:30:19.587Z"),
               dateService.iso8601DateParse("2009-08-24T21:30:32.63Z"), new NamedLinkImpl(
                        "VDC Name", VCloudMediaType.VDC_XML,
                        URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/1")),
               new NamedLinkImpl("Server1", VCloudMediaType.VAPP_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")));
      Task task2 = new TaskImpl(VCloudMediaType.TASK_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3299"),
               TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:29:32.983Z"),
               dateService.iso8601DateParse("2009-08-24T21:29:44.65Z"), new NamedLinkImpl(
                        "VDC Name", VCloudMediaType.VDC_XML,
                        URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/1")),
               new NamedLinkImpl("Server1", VCloudMediaType.VAPP_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")

               )

      );
      assertEquals(result.getTasks(), ImmutableSortedSet.of(task1, task2));
      assertEquals(
               result.getTasksByResult(),
               ImmutableListMultimap
                        .of(
                                 URI
                                          .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012"),
                                 task2,
                                 URI
                                          .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012"),
                                 task1));
      assertEquals(result.getTasksByOwner(), ImmutableListMultimap.of(URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/1"), task2, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/1"), task1));
   }

   public void testApplyInputStreamHosting() {
      InputStream is = getClass().getResourceAsStream("/taskslist-hosting.xml");

      TasksList result = factory.create(injector.getInstance(TasksListHandler.class)).parse(is);
      assertEquals(result.getLocation(), URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/188849"));

   }

}
