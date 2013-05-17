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
import org.jclouds.trmk.vcloud_0_8.domain.VCloudError.MinorCode;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ErrorImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.TaskImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TaskHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TaskHandlerTest")
public class TaskHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
   }

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/task.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = new TaskImpl(URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3299"), null,
            TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:29:32.983Z"),
            dateService.iso8601DateParse("2009-08-24T21:29:44.65Z"), null, new ReferenceTypeImpl("Server1",
                  TerremarkVCloudMediaType.VAPP_XML,
                  URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")), null

      );
      assertEquals(result, expects);

   }

   public void testSelf() {
      InputStream is = getClass().getResourceAsStream("/task-self.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("https://vcloud.safesecureweb.com/api/v0.8/task/d188849-78"), null,
            TaskStatus.QUEUED, null, null, null, null, null);
      assertEquals(result, expects);

   }

   public void testError() {
      InputStream is = getClass().getResourceAsStream("/task-error.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("http://10.150.4.49/api/v0.8/task/23"), null, TaskStatus.ERROR,
            dateService.iso8601SecondsDateParse("2009-12-07T19:05:02Z"),
            dateService.iso8601SecondsDateParse("2009-12-10T14:40:32Z"), null, new ReferenceTypeImpl("testapp1",
                  TerremarkVCloudMediaType.VAPP_XML, URI.create("http://10.150.4.49/api/v0.8/vapp/1")), new ErrorImpl(
                  "Error processing job", 500, MinorCode.UNRECOGNIZED,
                  " Error in runDailySummaries date used:2009-12-09 19:40:30.577326+00:00", null));
      assertEquals(result, expects);

   }
}
