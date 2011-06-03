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
package org.jclouds.vcloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.utils.TestUtils;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VmHandler;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VmAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VmAsyncClientTest")
public class VmAsyncClientTest extends BaseVCloudAsyncClientTest<VmAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VmAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VmAsyncClient>>() {
      };
   }

   public void testGetThumbnailOfVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("getScreenThumbnailForVm", URI.class);
      HttpRequest request = processor
               .createRequest(method, URI.create("http://vcloud.example.com/api/v1.0/vApp/vm-12"));

      assertRequestLineEquals(request, "GET http://vcloud.example.com/api/v1.0/vApp/vm-12/screen HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: image/png\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnInputStream.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test(dataProvider = "ignoreOnWindows", description = "see http://code.google.com/p/jclouds/issues/detail?id=402")
   public void testUpdateGuestConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("updateGuestCustomizationOfVm", GuestCustomizationSection.class,
               URI.class);
      GuestCustomizationSection guest = new GuestCustomizationSection(URI
               .create("http://vcloud.example.com/api/v1.0/vApp/vm-12/guestCustomizationSection"));
      guest.setCustomizationScript("cat > /tmp/foo.txt<<EOF\nI love candy\nEOF");
      HttpRequest request = processor.createRequest(method, guest, URI
               .create("http://vcloud.example.com/api/v1.0/vApp/vm-12"));

      assertRequestLineEquals(request,
               "PUT http://vcloud.example.com/api/v1.0/vApp/vm-12/guestCustomizationSection HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/guestCustomizationSection.xml")), "application/vnd.vmware.vcloud.guestCustomizationSection+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateCPUCountOfVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("updateCPUCountOfVm", int.class, URI.class);
      HttpRequest request = processor.createRequest(method, 2, URI
               .create("http://vcloud.example.com/api/v1.0/vApp/vm-12"));

      assertRequestLineEquals(request,
               "PUT http://vcloud.example.com/api/v1.0/vApp/vm-12/virtualHardwareSection/cpu HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/cpuItem.xml")),
               "application/vnd.vmware.vcloud.rasdItem+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateMemoryMBOfVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("updateMemoryMBOfVm", int.class, URI.class);
      HttpRequest request = processor.createRequest(method, 512, URI
               .create("http://vcloud.example.com/api/v1.0/vApp/vm-12"));

      assertRequestLineEquals(request,
               "PUT http://vcloud.example.com/api/v1.0/vApp/vm-12/virtualHardwareSection/memory HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/memoryItem.xml")),
               "application/vnd.vmware.vcloud.rasdItem+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeployVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("deployVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request, "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/action/deploy HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\"/>",
               "application/vnd.vmware.vcloud.deployVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeployAndPowerOnVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("deployAndPowerOnVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request, "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/action/deploy HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, "<DeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\" powerOn=\"true\"/>",
               "application/vnd.vmware.vcloud.deployVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("getVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vm/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vm/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vm+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VmHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRebootVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("rebootVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/reboot HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUndeployVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("undeployVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/action/undeploy HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, "<UndeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\"/>",
               "application/vnd.vmware.vcloud.undeployVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUndeployAndSaveStateOfVmSaveState() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("undeployAndSaveStateOfVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/action/undeploy HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request,
               "<UndeployVAppParams xmlns=\"http://www.vmware.com/vcloud/v1\" saveState=\"true\"/>",
               "application/vnd.vmware.vcloud.undeployVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testPowerOnVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("powerOnVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/powerOn HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testPowerOffVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("powerOffVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/powerOff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testResetVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("resetVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/reset HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSuspendVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("suspendVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/suspend HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testShutdownVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VmAsyncClient.class.getMethod("shutdownVm", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vApp/1/power/action/shutdown HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @DataProvider
   public Object[][] ignoreOnWindows() {
      return (TestUtils.isWindowsOs() ? TestUtils.NO_INVOCATIONS 
                                      : TestUtils.SINGLE_NO_ARG_INVOCATION);
   }
   
}
