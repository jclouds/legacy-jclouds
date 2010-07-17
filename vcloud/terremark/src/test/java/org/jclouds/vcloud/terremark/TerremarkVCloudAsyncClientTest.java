/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.disabled;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.config.TerremarkVCloudExpressRestClientModule;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.functions.ReturnEmptySetOnResourceNotFoundException;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.xml.CustomizationParametersHandler;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairsHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
import org.jclouds.vcloud.terremark.xml.NodesHandler;
import org.jclouds.vcloud.terremark.xml.PublicIpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkVCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "vcloud.TerremarkVCloudAsyncClientTest")
public class TerremarkVCloudAsyncClientTest extends RestClientTest<TerremarkVCloudExpressAsyncClient> {
   /**
    * ignore parameter of catalog id since this doesn't work
    */
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getCatalog", String.class);
      HttpRequest request = processor.createRequest(method, "1");

      assertRequestLineEquals(request, "GET https://catalog HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetDefaultVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getDefaultVDC");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getVDC", String.class);
      HttpRequest request = processor.createRequest(method, "1");

      assertRequestLineEquals(request, "GET https://vcloud/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", String.class,
            String.class, String.class, Array.newInstance(InstantiateVAppTemplateOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "1", "name", 3 + "");

      assertRequestLineEquals(request, "POST https://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/InstantiateVAppTemplateParams-test.xml")),
            "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplateOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", String.class,
            String.class, String.class, Array.newInstance(InstantiateVAppTemplateOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "1", "name", 3 + "",
            TerremarkInstantiateVAppTemplateOptions.Builder.processorCount(2).memory(512).inRow("row").inGroup("group")
                  .withPassword("password").inNetwork(URI.create("http://network")));

      assertRequestLineEquals(request, "POST https://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/InstantiateVAppTemplateParams-options-test.xml")),
            "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testActivatePublicIpInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("activatePublicIpInVDC", String.class);
      HttpRequest request = processor.createRequest(method, "1");

      assertRequestLineEquals(request, "POST https://vcloud/extensions/1/publicIps HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PublicIpAddressesHandler.class);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAddInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addInternetServiceToVDC", String.class,
            String.class, Protocol.class, int.class, Array.newInstance(AddInternetServiceOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "1", "name", Protocol.TCP, 22);

      assertRequestLineEquals(request, "POST https://vcloud/extensions/vdc/1/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateInternetService-test2.xml")), "application/vnd.tmrk.vCloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addInternetServiceToVDC", String.class,
            String.class, Protocol.class, int.class, Array.newInstance(AddInternetServiceOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "1", "name", Protocol.TCP, 22, disabled().withDescription(
            "yahoo"));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/vdc/1/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateInternetService-options-test.xml")), "application/vnd.tmrk.vCloud.internetService+xml",
            false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetAllInternetServices() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getAllInternetServicesInVDC", String.class);
      HttpRequest request = processor.createRequest(method, "1");

      assertRequestLineEquals(request, "GET https://vcloud/extensions/vdc/1/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServicesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getInternetService", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("deleteInternetService", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addInternetServiceToExistingIp", int.class,
            String.class, Protocol.class, int.class, Array.newInstance(AddInternetServiceOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, 12, "name", Protocol.TCP, 22);

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateInternetService-test2.xml")), "application/vnd.tmrk.vCloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIpOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addInternetServiceToExistingIp", int.class,
            String.class, Protocol.class, int.class, Array.newInstance(AddInternetServiceOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, 12, "name", Protocol.TCP, 22, disabled().withDescription(
            "yahoo"));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateInternetService-options-test.xml")), "application/vnd.tmrk.vCloud.internetService+xml",
            false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addNode", int.class, String.class,
            String.class, int.class, Array.newInstance(AddNodeOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, 12, "10.2.2.2", "name", 22);

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateNodeService-test2.xml")), "application/vnd.tmrk.vCloud.nodeService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNodeOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("addNode", int.class, String.class,
            String.class, int.class, Array.newInstance(AddNodeOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, 12, "10.2.2.2", "name", 22, AddNodeOptions.Builder
            .disabled().withDescription("yahoo"));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");

      assertPayloadEquals(request, Utils.toStringAndClose(getClass().getResourceAsStream(
            "/terremark/CreateNodeService-options-test.xml")), "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getNode", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "GET https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testConfigureNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("configureNode", int.class,
            NodeConfiguration.class);
      HttpRequest request = processor.createRequest(method, 12, new NodeConfiguration().changeDescriptionTo("eggs"));

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Description>eggs</Description></NodeService>",
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getNodes", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnResourceNotFoundException.class);

      checkFilters(request);
   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("deleteNode", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetCustomizationOptionsOfCatalogItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getCustomizationOptionsOfCatalogItem",
            String.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "GET https://vcloud/extensions/template/12/options/customization HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "Accept: application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("listKeyPairs");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://keysList HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vcloudExpress.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListKeyPairsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("listKeyPairsInOrg", String.class);
      HttpRequest request = processor.createRequest(method, "org1");

      assertRequestLineEquals(request, "GET https://vcloud/extensions/org/org1/keys HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vcloudExpress.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("getNode", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "GET https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   // TODO
   // public void testConfigureKeyPair() throws SecurityException,
   // NoSuchMethodException, IOException {
   // Method method = TerremarkVCloudAsyncClient.class.getMethod(
   // "configureKeyPair", int.class, KeyPairConfiguration.class);
   // HttpRequest request = processor
   // .createRequest(method, 12, new KeyPairConfiguration()
   // .changeDescriptionTo("eggs"));
   //
   // assertRequestLineEquals(request,
   // "PUT https://vcloud/extensions/keyPairService/12 HTTP/1.1");
   // assertHeadersEqual(
   // request,
   // "Accept: application/vnd.tmrk.vCloud.keyPairService+xml\nContent-Length: 155\nContent-Type: application/vnd.tmrk.vCloud.keyPairService+xml\n");
   // assertPayloadEquals(
   // request,
   // "<KeyPairService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><Description>eggs</Description></KeyPairService>");
   // assertResponseParserClassEquals(method, request, ParseSax.class);
   // assertSaxResponseParserClassEquals(method, KeyPairHandler.class);
   // assertExceptionParserClassEquals(method, null);
   //
   // checkFilters(request);
   // }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudExpressAsyncClient.class.getMethod("deleteKeyPair", int.class);
      HttpRequest request = processor.createRequest(method, 12);

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TerremarkVCloudExpressAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TerremarkVCloudExpressAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TerremarkVCloudRestClientModuleExtension();
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("trmk-vcloudexpress", "identity", "credential",
            new Properties());
   }

   @RequiresHttp
   @ConfiguresRestClient
   protected static class TerremarkVCloudRestClientModuleExtension extends TerremarkVCloudExpressRestClientModule {
      @Override
      protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
            @Named(PROPERTY_API_VERSION) String version) {
         return URI.create("https://vcloud/login");
      }

      @Override
      protected URI provideOrg(@Org Iterable<NamedResource> orgs) {
         return URI.create("https://org");

      }

      @Override
      protected URI provideCatalog(Organization org, @Named(PROPERTY_IDENTITY) String user) {
         return URI.create("https://catalog");

      }

      @Override
      protected Organization provideOrganization(VCloudClient discovery) {
         return null;
      }

      @Override
      protected Iterable<NamedResource> provideOrgs(Supplier<VCloudSession> cache, @Named(PROPERTY_IDENTITY) String user) {
         return null;
      }

      @Override
      protected URI provideDefaultTasksList(Organization org) {
         return URI.create("https://taskslist");
      }

      @Override
      protected URI provideDefaultVDC(Organization org) {
         return URI.create("https://vdc/1");
      }

      @Override
      protected URI provideDefaultNetwork(VCloudClient client) {
         return URI.create("https://vcloud.safesecureweb.com/network/1990");
      }

      @Override
      protected URI provideDefaultKeysList(Organization org) {
         return URI.create("https://keysList");

      }
   }

}
