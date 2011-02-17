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

package org.jclouds.vcloud.terremark;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.disabled;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.VCloudExpressAsyncClientTest.VCloudRestClientModuleExtension.TestOrgCatalogItemSupplier;
import org.jclouds.vcloud.VCloudExpressAsyncClientTest.VCloudRestClientModuleExtension.TestOrgCatalogSupplier;
import org.jclouds.vcloud.config.CommonVCloudRestClientModule.OrgVDCSupplier;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.NetworkConfig;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.config.TerremarkECloudRestClientModule;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkOrgImpl;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVDCImpl;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.xml.CustomizationParametersHandler;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairByNameHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairHandler;
import org.jclouds.vcloud.terremark.xml.KeyPairsHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
import org.jclouds.vcloud.terremark.xml.NodesHandler;
import org.jclouds.vcloud.terremark.xml.PublicIpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkOrgNetworkFromTerremarkVCloudExpressNetworkHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.VCloudExpressVAppHandler;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkECloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", sequential = true, testName = "TerremarkECloudAsyncClientTest")
public class TerremarkECloudAsyncClientTest extends RestClientTest<TerremarkECloudAsyncClient> {
   public void testNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getNetwork", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkOrgNetworkFromTerremarkVCloudExpressNetworkHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindNetworkInOrgVDCNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("findNetworkInOrgVDCNamed", String.class,
               String.class, String.class);
      HttpRequest request = processor.createRequest(method, "org", "vdc", "network");

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkOrgNetworkFromTerremarkVCloudExpressNetworkHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /**
    * ignore parameter of catalog id since this doesn't work
    */
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getCatalog", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://catalog"));

      assertRequestLineEquals(request, "GET https://catalog HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getVDC", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/vdc/1"));

      assertRequestLineEquals(request, "GET https://vcloud/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplateInVDCURI() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", URI.class, URI.class,
               String.class, InstantiateVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), URI.create("https://vcloud/vAppTemplate/3"),
               "name");

      assertRequestLineEquals(request,
               "POST https://vcloud.safesecureweb.com/api/v0.8/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams-test.xml")),
               "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VCloudExpressVAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplateInVDCURIOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC", URI.class, URI.class,
               String.class, InstantiateVAppTemplateOptions[].class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), URI.create("https://vcloud/vAppTemplate/3"),
               "name", TerremarkInstantiateVAppTemplateOptions.Builder.processorCount(2).memory(512).inGroup("group")
                        .withPassword("password").inRow("row").addNetworkConfig(
                                 new NetworkConfig(URI.create("http://network"))));

      assertRequestLineEquals(request,
               "POST https://vcloud.safesecureweb.com/api/v0.8/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams-options-test.xml")),
               "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VCloudExpressVAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testActivatePublicIpInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("activatePublicIpInVDC", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"));

      assertRequestLineEquals(request, "POST https://vcloud.safesecureweb.com/api/v0.8/publicIps/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.publicIp+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PublicIpAddressesHandler.class);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetAllInternetServices() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getAllInternetServicesInVDC", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/internetServices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServicesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getInternetService", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/internetService/12"));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("deleteInternetService", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/internetService/12"));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("addInternetServiceToExistingIp", URI.class,
               String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/publicIp/12"),
               "name", Protocol.TCP, 22);

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetService+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(
               getClass().getResourceAsStream("/terremark/CreateInternetService-test2.xml")).replace(
               "vCloudExpressExtensions-1.6", "eCloudExtensions-2.7"),
               "application/vnd.tmrk.ecloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIpOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("addInternetServiceToExistingIp", URI.class,
               String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/publicIp/12"),
               "name", Protocol.TCP, 22, disabled().withDescription("yahoo"));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetService+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(
               getClass().getResourceAsStream("/terremark/CreateInternetService-options-test.xml")).replace(
               "vCloudExpressExtensions-1.6", "eCloudExtensions-2.7"),
               "application/vnd.tmrk.ecloud.internetService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("addNode", URI.class, String.class, String.class,
               int.class, AddNodeOptions[].class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/internetService/12"),
               "10.2.2.2", "name", 22);

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(
               getClass().getResourceAsStream("/terremark/CreateNodeService-test2.xml")).replace(
               "vCloudExpressExtensions-1.6", "eCloudExtensions-2.7"), "application/vnd.tmrk.vCloud.nodeService+xml",
               false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNodeOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("addNode", URI.class, String.class, String.class,
               int.class, AddNodeOptions[].class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/internetService/12"),
               "10.2.2.2", "name", 22, AddNodeOptions.Builder.disabled().withDescription("yahoo"));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");

      assertPayloadEquals(request, Strings2.toStringAndClose(
               getClass().getResourceAsStream("/terremark/CreateNodeService-options-test.xml")).replace(
               "vCloudExpressExtensions-1.6", "eCloudExtensions-2.7"), "application/vnd.tmrk.vCloud.nodeService+xml",
               false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKeyPairInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("findKeyPairInOrg", URI.class, String.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/org/1"), "keyPair");

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairByNameHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testConfigureNodeWithDescription() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("configureNode", URI.class, String.class,
               boolean.class, String.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/nodeService/12"),
               "name", true, "eggs");

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
               request,
               "<NodeService xmlns=\"urn:tmrk:eCloudExtensions-2.7\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled><Description>eggs</Description></NodeService>",
               "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testConfigureNodeNoDescription() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("configureNode", URI.class, String.class,
               boolean.class, String.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/nodeService/12"),
               "name", true, null);

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
               request,
               "<NodeService xmlns=\"urn:tmrk:eCloudExtensions-2.7\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled></NodeService>",
               "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getNodes", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/internetService/12"));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("deleteNode", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/nodeService/12"));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetCustomizationOptionsOfCatalogItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getCustomizationOptions", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/template/12"));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/template/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "Accept: application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListKeyPairsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("listKeyPairsInOrg", URI.class);
      HttpRequest request = processor.createRequest(method, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListKeyPairsInOrgNotFound() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("listKeyPairsInOrg", URI.class);
      processor.createRequest(method, URI.create("d"));
   }

   public void testGetKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getKeyPair", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/key/12"));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("deleteKeyPair", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/key/12"));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkECloudAsyncClient.class.getMethod("getNode", URI.class);
      HttpRequest request = processor.createRequest(method, URI.create("https://vcloud/extensions/nodeService/12"));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TerremarkECloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TerremarkECloudAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TerremarkVCloudRestClientModuleExtension();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("trmk-ecloud", "identity", "credential", new Properties());
   }

   @RequiresHttp
   @ConfiguresRestClient
   protected static class TerremarkVCloudRestClientModuleExtension extends TerremarkECloudRestClientModule {
      @Override
      protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
               @Named(PROPERTY_API_VERSION) String version) {
         return URI.create("https://vcloud/login");
      }

      @Override
      protected void configure() {
         super.configure();
         bind(OrgNameToKeysListSupplier.class).to(TestOrgNameToKeysListSupplier.class);
         bind(OrgMapSupplier.class).to(TestTerremarkOrgMapSupplier.class);
         bind(OrgCatalogSupplier.class).to(TestOrgCatalogSupplier.class);
         bind(OrgCatalogItemSupplier.class).to(TestOrgCatalogItemSupplier.class);
         bind(OrgVDCSupplier.class).to(TestTerremarkOrgVDCSupplier.class);
      }

      @Singleton
      public static class TestOrgNameToKeysListSupplier extends OrgNameToKeysListSupplier {
         @Inject
         protected TestOrgNameToKeysListSupplier(Supplier<VCloudSession> sessionSupplier) {
            super(sessionSupplier, null);
         }

         @Override
         public Map<String, ReferenceType> get() {
            return Maps.transformValues(sessionSupplier.get().getOrgs(), new Function<ReferenceType, ReferenceType>() {

               @Override
               public ReferenceType apply(ReferenceType from) {
                  return new ReferenceTypeImpl(from.getName(), TerremarkECloudMediaType.KEYSLIST_XML, URI.create(from
                           .getHref().toASCIIString()
                           + "/keysList"));
               }
            });
         }
      }

      @Singleton
      public static class TestTerremarkOrgMapSupplier extends OrgMapSupplier {
         @Inject
         protected TestTerremarkOrgMapSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Org> get() {
            return ImmutableMap.<String, Org> of("org", new TerremarkOrgImpl("org", null, URI
                     .create("https://vcloud.safesecureweb.com/api/v0.8/org/1"), null, ImmutableMap
                     .<String, ReferenceType> of("catalog", new ReferenceTypeImpl("catalog",
                              TerremarkECloudMediaType.CATALOG_XML, URI
                                       .create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"))), ImmutableMap
                     .<String, ReferenceType> of("vdc", new ReferenceTypeImpl("vdc", TerremarkECloudMediaType.VDC_XML,
                              URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"))), ImmutableMap
                     .<String, ReferenceType> of(), new ReferenceTypeImpl("tasksList",
                     TerremarkECloudMediaType.TASKSLIST_XML, URI
                              .create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/1")), new ReferenceTypeImpl(
                     "keysList", TerremarkECloudMediaType.KEYSLIST_XML, URI
                              .create("https://vcloud.safesecureweb.com/api/v0.8/keysList/1"))));
         }
      }

      @Override
      protected URI provideOrg(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return URI.create("https://org");
      }

      @Override
      protected String provideOrgName(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return "org";
      }

      @Override
      protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) String user) {
         return URI.create("https://catalog");
      }

      @Override
      protected Org provideOrg(CommonVCloudClient discovery) {
         return null;
      }

      @Override
      protected Iterable<ReferenceType> provideOrgs(Supplier<VCloudSession> cache, @Named(PROPERTY_IDENTITY) String user) {
         return null;
      }

      @Override
      protected URI provideDefaultTasksList(Org org) {
         return URI.create("https://taskslist");
      }

      @Override
      protected URI provideDefaultVDC(Org org, @org.jclouds.vcloud.endpoints.VDC String defaultVDC) {
         return URI.create("https://vdc/1");
      }

      @Override
      protected String provideDefaultVDCName(
               @org.jclouds.vcloud.endpoints.VDC Supplier<Map<String, String>> vDCtoOrgSupplier) {
         return "vdc";
      }

      @Override
      protected URI provideDefaultNetwork(URI vdc, Injector injector) {
         return URI.create("https://vcloud.safesecureweb.com/network/1990");
      }
   }

   @Singleton
   public static class TestTerremarkOrgVDCSupplier extends OrgVDCSupplier {
      @Inject
      protected TestTerremarkOrgVDCSupplier() {
         super(null, null);
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> get() {
         return ImmutableMap.<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> of("org",

         ImmutableMap.<String, org.jclouds.vcloud.domain.VDC> of("vdc", new TerremarkVDCImpl("vdc", null, URI
                  .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), VDCStatus.READY, null, "description",
                  ImmutableSet.<Task> of(), AllocationModel.UNRECOGNIZED, new Capacity("MB", 0, 0, 0, 0), new Capacity(
                           "MB", 0, 0, 0, 0), new Capacity("MB", 0, 0, 0, 0), ImmutableMap.<String, ReferenceType> of(
                           "vapp", new ReferenceTypeImpl("vapp", "application/vnd.vmware.vcloud.vApp+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1")), "network",
                           new ReferenceTypeImpl("network", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2"))), ImmutableMap
                           .<String, ReferenceType> of(), 0, 0, 0, false, new ReferenceTypeImpl("catalog",
                           TerremarkVCloudMediaType.CATALOG_XML, URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1")),
                  new ReferenceTypeImpl("publicIps", TerremarkVCloudMediaType.PUBLICIPSLIST_XML, URI
                           .create("https://vcloud.safesecureweb.com/api/v0.8/publicIps/1")), new ReferenceTypeImpl(
                           "internetServices", TerremarkVCloudMediaType.INTERNETSERVICESLIST_XML, URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/internetServices/1")))));
      }
   }
}
