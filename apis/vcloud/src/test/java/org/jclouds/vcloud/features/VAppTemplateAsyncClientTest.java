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

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.xml.EnvelopeHandler;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VAppTemplateAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VAppTemplateAsyncClientTest")
public class VAppTemplateAsyncClientTest extends BaseVCloudAsyncClientTest<VAppTemplateAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VAppTemplateAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VAppTemplateAsyncClient>>() {
      };
   }

   public void testCreateVAppInVDCByInstantiatingTemplate() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("createVAppInVDCByInstantiatingTemplate", String.class,
               URI.class, URI.class, InstantiateVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, "my-vapp", URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3"),
               addNetworkConfig(new NetworkConfig("aloha", URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/network/1991"), FenceMode.NAT_ROUTED)));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/instantiationparams-network.xml")), "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml",
               false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCreateVAppInVDCByInstantiatingTemplateOptionsIllegalName() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("createVAppInVDCByInstantiatingTemplate", String.class,
               URI.class, URI.class, InstantiateVAppTemplateOptions[].class);
      processor.createRequest(method, "CentOS 01", URI.create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), addNetworkConfig(new NetworkConfig(null,
               URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/1991"), null)));
   }

   public void testcopyVAppTemplateToVDCAndName() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("copyVAppTemplateToVDCAndName", URI.class, URI.class,
               String.class, CloneVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/4181"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "my-vapptemplate");

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/copyVAppTemplate-default.xml")), "application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testcopyVAppTemplateToVDCAndNameOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("copyVAppTemplateToVDCAndName", URI.class, URI.class,
               String.class, CloneVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/201"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "new-linux-server",
               new CloneVAppTemplateOptions().description("The description of the new vAppTemplate"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/copyVAppTemplate.xml")),
               "application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testmoveVAppTemplateToVDCAndRenameOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("moveVAppTemplateToVDCAndRename", URI.class, URI.class,
               String.class, CloneVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/201"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "new-linux-server",
               new CloneVAppTemplateOptions().description("The description of the new vAppTemplate"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/cloneVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/moveVAppTemplate.xml")),
               "application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testcaptureVAppAsTemplateInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("captureVAppAsTemplateInVDC", URI.class, String.class,
               URI.class, CaptureVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/4181"), "my-template", URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/captureVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vAppTemplate+xml\n");
      assertPayloadEquals(request, Strings2
               .toStringAndClose(getClass().getResourceAsStream("/captureVApp-default.xml")),
               "application/vnd.vmware.vcloud.captureVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppTemplateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testcaptureVAppAsTemplateInVDCOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("captureVAppAsTemplateInVDC", URI.class, String.class,
               URI.class, CaptureVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/201"), "my-template", URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), new CaptureVAppOptions()
               .withDescription("The description of the new vApp Template"));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/vdc/1/action/captureVApp HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vAppTemplate+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/captureVApp.xml")),
               "application/vnd.vmware.vcloud.captureVAppParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppTemplateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testFindVAppTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("findVAppTemplateInOrgCatalogNamed", String.class,
               String.class, String.class);
      HttpRequest request = processor.createRequest(method, "org", "catalog", "template");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vAppTemplate+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppTemplateHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testVAppTemplateURI() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("getVAppTemplate", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vAppTemplate+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppTemplateHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetOvfEnvelopeForVAppTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VAppTemplateAsyncClient.class.getMethod("getOvfEnvelopeForVAppTemplate", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2/ovf HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: text/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, EnvelopeHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
