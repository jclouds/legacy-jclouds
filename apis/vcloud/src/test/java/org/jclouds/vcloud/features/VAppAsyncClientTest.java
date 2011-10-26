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
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VAppAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VAppAsyncClientTest")
public class VAppAsyncClientTest extends BaseVCloudAsyncClientTest<VAppAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VAppAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VAppAsyncClient>>() {
      };
   }

   public void testopyVAppToVDCAndName() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("copyVAppToVDCAndName", URI.class, URI.class, String.class,
               CloneVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/4181"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "my-vapp");

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/copyVApp-default.xml")),
               "application/vnd.vmware.vcloud.cloneVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCopyVAppToVDCAndNameOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("copyVAppToVDCAndName", URI.class, URI.class, String.class,
               CloneVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/201"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "new-linux-server", new CloneVAppOptions()
               .deploy().powerOn().description("The description of the new vApp"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/copyVApp.xml")),
               "application/vnd.vmware.vcloud.cloneVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testMoveVAppToVDCAndRenameOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("moveVAppToVDCAndRename", URI.class, URI.class, String.class,
               CloneVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/201"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "new-linux-server", new CloneVAppOptions()
               .deploy().powerOn().description("The description of the new vApp"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/moveVApp.xml")),
               "application/vnd.vmware.vcloud.cloneVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("deployVApp", URI.class);
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

   public void testDeployAndPowerOnVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("deployAndPowerOnVApp", URI.class);
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

   public void testGetVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("getVApp", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vApp/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRebootVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("rebootVApp", URI.class);
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

   public void testUndeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("undeployVApp", URI.class);
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

   public void testUndeployAndSaveStateOfVAppSaveState() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("undeployAndSaveStateOfVApp", URI.class);
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

   public void testDeleteVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("deleteVApp", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request, "DELETE https://vcenterprise.bluelock.com/api/v1.0/vApp/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testPowerOnVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("powerOnVApp", URI.class);
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

   public void testPowerOffVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("powerOffVApp", URI.class);
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

   public void testResetVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("resetVApp", URI.class);
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

   public void testSuspendVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("suspendVApp", URI.class);
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

   public void testShutdownVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppAsyncClient.class.getMethod("shutdownVApp", URI.class);
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

}
