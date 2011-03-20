package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.TaskError;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TaskHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TaskHandlerTest")
public class TaskHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
   }

   public void testVAppSuccess() {
      InputStream is = getClass().getResourceAsStream("/task-vapp.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.SUCCESS)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/21-1002"))
            .owner(
                  Resource
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  Resource
                        .builder()
                        .id("1002")
                        .name("mock_vpdc_item_007")
                        .type("application/vnd.vmware.vcloud.vApp+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002"))
                        .build()).build();
      assertEquals(result.toString(), expects.toString());

   }

   public void testVMDKSuccess() {
      InputStream is = getClass().getResourceAsStream("/task-vmdk.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.SUCCESS)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/21-1002"))
            .owner(
                  Resource
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  Resource
                        .builder()
                        .id("1234567")
                        .name("mock_vpdc_item_008")
                        .type("application/vnd.vmware.vcloud.vApp+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/vdc/2736/vmdk/1234567"))
                        .build()).build();
      assertEquals(result.toString(), expects.toString());

   }

   public void testRunning() {
      InputStream is = getClass().getResourceAsStream("/task-running.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.RUNNING)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/21-1002"))
            .owner(
                  Resource
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  Resource
                        .builder()
                        .id("1002")
                        .name("mock_vpdc_item_007")
                        .type("application/vnd.vmware.vcloud.catalogItem+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002"))
                        .build()).build();

      assertEquals(result.toString(), expects.toString());

   }

   public void testQueued() {
      InputStream is = getClass().getResourceAsStream("/task-queued.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .id("6904")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.QUEUED)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/6904"))
            .owner(
                  Resource
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  Resource
                        .builder()
                        .id("1002")
                        .name("mock_vpdc_item_007")
                        .type("application/vnd.vmware.vcloud.catalogItem+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002"))
                        .build()).build();
      assertEquals(result.toString(), expects.toString());

   }

   public void testFailed() {
      InputStream is = getClass().getResourceAsStream("/task-failed.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .id("6904-123")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .status(Task.Status.ERROR)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/6904-123"))
            .owner(
                  Resource
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  Resource
                        .builder()
                        .id("1002")
                        .name("mock_vpdc_item_007")
                        .type("application/vnd.vmware.vcloud.vApp+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002"))
                        .build()).build();
      assertEquals(result.toString(), expects.toString());

   }

   public void testError() {
      InputStream is = getClass().getResourceAsStream("/task-error.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .id("6904-123")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .status(Task.Status.ERROR)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/6904-123"))
            .error(
                  TaskError
                        .builder()
                        .vendorSpecificErrorCode("0")
                        .minorErrorCode(0)
                        .majorErrorCode(0)
                        .message(
                              "There is an internal exception occured in System, please kindly contact savvis support team to get solution.")
                        .build()).build();
      assertEquals(result.toString(), expects.toString());

   }

   public void testUnsupported() {
      InputStream is = getClass().getResourceAsStream("/task-unsupported.xml");

      Task result = factory.create(injector.getInstance(TaskHandler.class)).parse(is);
      Task expects = Task
            .builder()
            .id("6904-123")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .status(Task.Status.NONE)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/6904-123"))
            .error(
                  TaskError
                        .builder()
                        .vendorSpecificErrorCode("3000")
                        .minorErrorCode(-1)
                        .majorErrorCode(500)
                        .message(
                              "your requested task id is not found. Please contact Savvis Administrator for further assistance/clarification ")
                        .build()).build();
      assertEquals(result.toString(), expects.toString());
   }
}
