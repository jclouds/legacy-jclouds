/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TaskHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TaskHandlerTest")
public class TaskHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/task.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/task/3299"),
            TaskStatus.SUCCESS, dateService.iso8601DateParse("2009-08-24T21:29:32.983Z"), dateService
                  .iso8601DateParse("2009-08-24T21:29:44.65Z"), null, new NamedResourceImpl("VDC Name",
                  VCloudMediaType.VDC_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/1")),
            new NamedResourceImpl("Server1", VCloudMediaType.VAPP_XML, URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/4012")

            ), null

      );
      assertEquals(result, expects);

   }

   public void testStates() {
      assert VAppStatus.ON.compareTo(VAppStatus.OFF) > 0;
      assert VAppStatus.SUSPENDED.compareTo(VAppStatus.OFF) > 0;
      assert VAppStatus.OFF.compareTo(VAppStatus.OFF) == 0;
   }

   public void testSelf() {
      InputStream is = getClass().getResourceAsStream("/task-self.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("https://vcloud.safesecureweb.com/api/v0.8/task/d188849-78"),
            TaskStatus.QUEUED, null, null, null, null, null, null);
      assertEquals(result, expects);

   }

   public void testApplyInputStream2() {
      InputStream is = getClass().getResourceAsStream("/task-hosting.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("https://vcloud.safesecureweb.com/api/v0.8/task/97806"),
            TaskStatus.SUCCESS, dateService.iso8601SecondsDateParse("2010-01-14T20:04:51Z"), dateService
                  .iso8601SecondsDateParse("2010-01-14T20:05:02Z"), dateService
                  .iso8601SecondsDateParse("2010-01-15T20:05:02Z"),

            new NamedResourceImpl("188849-96", VCloudMediaType.VAPP_XML, URI
                  .create("https://vcloud.safesecureweb.com/api/v0.8/vapp/188849-96")), null, null);
      assertEquals(result, expects);
   }

   public void testError() {
      InputStream is = getClass().getResourceAsStream("/task-error.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);

      Task expects = new TaskImpl(URI.create("http://10.150.4.49/api/v0.8/task/23"), TaskStatus.ERROR, dateService
            .iso8601SecondsDateParse("2009-12-07T19:05:02Z"), dateService
            .iso8601SecondsDateParse("2009-12-10T14:40:32Z"), null, new NamedResourceImpl("APIOrg",
            VCloudMediaType.ORG_XML, URI.create("http://10.150.4.49/api/v0.8/org/1")), new NamedResourceImpl(
            "testapp1", VCloudMediaType.VAPP_XML, URI.create("http://10.150.4.49/api/v0.8/vapp/1")),
            new TaskImpl.ErrorImpl("Error processing job", "500",
                  " Error in runDailySummaries date used:2009-12-09 19:40:30.577326+00:00"));
      assertEquals(result, expects);

   }
}
