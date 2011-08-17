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
package org.jclouds.vcloud;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.addNetworkConfig;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.ovf.xml.EnvelopeHandler;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.OrgListHandler;
import org.jclouds.vcloud.xml.OrgNetworkHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VDCHandler;
import org.jclouds.vcloud.xml.VmHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VCloudAsyncClientTest")
public class VCloudAsyncClientTest extends BaseVCloudAsyncClientTest<VCloudAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>>() {
      };
   }

   private VCloudAsyncClient asyncClient;
   private VCloudClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getVAppClient() != null;
      assert syncClient.getCatalogClient() != null;
      assert syncClient.getVmClient() != null;
      assert syncClient.getVAppTemplateClient() != null;
      assert syncClient.getTaskClient() != null;
      assert syncClient.getVDCClient() != null;
      assert syncClient.getNetworkClient() != null;
      assert syncClient.getOrgClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getVAppClient() != null;
      assert asyncClient.getCatalogClient() != null;
      assert asyncClient.getVmClient() != null;
      assert asyncClient.getVAppTemplateClient() != null;
      assert asyncClient.getTaskClient() != null;
      assert asyncClient.getVDCClient() != null;
      assert asyncClient.getNetworkClient() != null;
      assert asyncClient.getOrgClient() != null;
   }

   public void testGetThumbnailOfVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getThumbnailOfVm", URI.class);
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

   public void testUpdateGuestConfiguration() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("updateGuestCustomizationOfVm", URI.class,
               GuestCustomizationSection.class);
      GuestCustomizationSection guest = new GuestCustomizationSection(URI
               .create("http://vcloud.example.com/api/v1.0/vApp/vm-12/guestCustomizationSection"));
      guest.setCustomizationScript("cat > /tmp/foo.txt<<EOF\nI love candy\nEOF");
      HttpRequest request = processor.createRequest(method,
               URI.create("http://vcloud.example.com/api/v1.0/vApp/vm-12"), guest);

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

   public void testInstantiateVAppTemplateInVDCURIOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", URI.class, URI.class,
               String.class, InstantiateVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/3"), "my-vapp",
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
   public void testInstantiateVAppTemplateInOrgOptionsIllegalName() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", URI.class, URI.class,
               String.class, InstantiateVAppTemplateOptions[].class);
      processor.createRequest(method, URI.create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), "CentOS 01",
               addNetworkConfig(new NetworkConfig(null, URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/network/1991"), null)));
   }

   @Deprecated
   public void testCloneVAppInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cloneVAppInVDC", URI.class, URI.class, String.class,
               CloneVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/4181"), "my-vapp");

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

   @Deprecated
   public void testCloneVAppInVDCOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cloneVAppInVDC", URI.class, URI.class, String.class,
               CloneVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/201"), "new-linux-server",
               new CloneVAppOptions().deploy().powerOn().description("The description of the new vApp"));

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

   public void testCaptureVAppInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("captureVAppInVDC", URI.class, URI.class, String.class,
               CaptureVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/4181"), "my-template");

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

   public void testCaptureVAppInVDCOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("captureVAppInVDC", URI.class, URI.class, String.class,
               CaptureVAppOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vapp/201"), "my-template", new CaptureVAppOptions()
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

   public void testlistOrgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("listOrgs");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.orgList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgListHandler.class);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getOrg", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/org/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findOrgNamed", String.class);
      HttpRequest request = processor.createRequest(method, "org");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/org/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getCatalog", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalog/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCatalogInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findCatalogInOrgNamed", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "org", "catalog");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalog/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getNetwork", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/network/2"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/network/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgNetworkHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindNetworkInOrgVDCNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findNetworkInOrgVDCNamed", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, "org", "vdc", "network");

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/network/1990 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgNetworkHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCatalogItemURI() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getCatalogItem", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindCatalogItemInOrgCatalogNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findCatalogItemInOrgCatalogNamed", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, "org", "catalog", "item");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalogItem/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindVAppTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVAppTemplateInOrgCatalogNamed", String.class,
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
      Method method = VCloudAsyncClient.class.getMethod("getVAppTemplate", URI.class);
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
      Method method = VCloudAsyncClient.class.getMethod("getOvfEnvelopeForVAppTemplate", URI.class);
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

   public void testFindVDCInOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVDCInOrgNamed", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "org", "vdc");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testFindVDCInOrgNamedBadVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVDCInOrgNamed", String.class, String.class);
      processor.createRequest(method, "org", "vdc1");
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testFindVDCInOrgNamedBadOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVDCInOrgNamed", String.class, String.class);
      processor.createRequest(method, "org1", "vdc");
   }

   public void testFindVDCInOrgNamedNullOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVDCInOrgNamed", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "vdc");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindVDCInOrgNamedNullOrgAndVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findVDCInOrgNamed", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, null);

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getVDC", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetTasksList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getTasksList", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/tasksList/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindTasksListInOrgNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("findTasksListInOrgNamed", String.class);
      HttpRequest request = processor.createRequest(method, "org");

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/tasksList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeployVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("deployVAppOrVm", URI.class);
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

   public void testDeployAndPowerOnVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("deployAndPowerOnVAppOrVm", URI.class);
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
      Method method = VCloudAsyncClient.class.getMethod("getVApp", URI.class);
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

   public void testGetVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getVm", URI.class);
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

   public void testRebootVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("rebootVAppOrVm", URI.class);
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

   public void testUndeployVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("undeployVAppOrVm", URI.class);
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

   public void testUndeployAndSaveStateOfVAppOrVmSaveState() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("undeployAndSaveStateOfVAppOrVm", URI.class);
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
      Method method = VCloudAsyncClient.class.getMethod("deleteVApp", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/1"));

      assertRequestLineEquals(request, "DELETE https://vcenterprise.bluelock.com/api/v1.0/vApp/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testPowerOnVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("powerOnVAppOrVm", URI.class);
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

   public void testPowerOffVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("powerOffVAppOrVm", URI.class);
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

   public void testResetVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("resetVAppOrVm", URI.class);
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

   public void testSuspendVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("suspendVAppOrVm", URI.class);
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

   public void testShutdownVAppOrVm() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("shutdownVAppOrVm", URI.class);
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

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getTask", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/task/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCancelTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cancelTask", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/task/1"));

      assertRequestLineEquals(request, "POST https://vcenterprise.bluelock.com/api/v1.0/task/1/action/cancel HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(VCloudAsyncClient.class);
      syncClient = injector.getInstance(VCloudClient.class);
   }

}
