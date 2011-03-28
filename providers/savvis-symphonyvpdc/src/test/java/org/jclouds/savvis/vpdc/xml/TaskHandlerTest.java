package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.savvis.vpdc.domain.ResourceImpl;
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
            .id("21-1002")
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.SUCCESS)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/21-1002"))
            .owner(
                  ResourceImpl
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                     ResourceImpl
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
            .id("21-1002")
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.SUCCESS)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/21-1002"))
            .owner(
                  ResourceImpl
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  ResourceImpl
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
            .id("6904")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .status(Task.Status.RUNNING)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/6904"))
            .owner(
                  ResourceImpl
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  ResourceImpl
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
            .id("113927-1005")
            .startTime(dateService.iso8601DateParse("2011-03-24T20:37:34.000Z"))
            .status(Task.Status.QUEUED)
            .href(URI.create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/task/113927-1005"))
            .owner(
                  ResourceImpl
                        .builder()
                        .id("4253")
                        .name("Foo")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/4253"))
                        .build())
            .result(
                  ResourceImpl
                        .builder()
                        .id("1005")
                        .name("adriancole")
                        .type("application/vnd.vmware.vcloud.vApp+xml")
                        .href(URI
                              .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/4253/vApp/1005"))
                        .build()).build();
      assertEquals(result.toString(), expects.toString());
      assertEquals(result.getError(), null);
      assert result.getId() != null;
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
                  ResourceImpl
                        .builder()
                        .id("2736")
                        .name("mockVpdc8")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI
                              .create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736"))
                        .build())
            .result(
                  ResourceImpl
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
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .status(Task.Status.ERROR)
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
            .id("3904-1002")
            .startTime(dateService.iso8601DateParse("2010-05-26T08:08:08.000+08:00"))
            .endTime(dateService.iso8601DateParse("2010-05-26T08:09:09.000+08:00"))
            .status(Task.Status.NONE)
            .type("application/vnd.vmware.vcloud.task+xml")
            .href(URI.create("https://api.symphonyvpdc.savvis.net/rest/api/v0.8/task/3904-1002"))
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
