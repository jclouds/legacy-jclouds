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
package org.jclouds.trmk.ecloud;

import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.disabled;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.functions.ParseTaskFromLocationHeader;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.AddNodeOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.NetworkConfig;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CustomizationParametersHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServicesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairByNameHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairsHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NetworkHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodeHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.PublicIpAddressHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppExtendedInfoHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VDCHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code TerremarkECloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", singleThreaded = true, testName = "TerremarkECloudAsyncClientTest")
public class TerremarkECloudAsyncClientTest extends BaseTerremarkECloudAsyncClientTest<TerremarkECloudAsyncClient> {

   public void testListOrgs() {
      assertEquals(injector.getInstance(TerremarkECloudAsyncClient.class).listOrgs().toString(),
            ImmutableMap.of(ORG_REF.getName(), ORG_REF).toString());
   }

   public void testNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getNetwork", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NetworkHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDelete() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "deleteVApp", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vApp/1")));

      assertRequestLineEquals(request, "DELETE https://vcloud.safesecureweb.com/api/v0.8/vApp/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseTaskFromLocationHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindNetworkInOrgVDCNamed() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "findNetworkInOrgVDCNamed", String.class,
            String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("org", "vdc", "network"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/network/1990 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NetworkHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /**
    * ignore parameter of catalog id since this doesn't work
    */
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getCatalog", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://catalog")));

      assertRequestLineEquals(request, "GET https://catalog HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getVDC", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/vdc/1")));

      assertRequestLineEquals(request, "GET https://vcloud/vdc/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplateInVDCURI() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "instantiateVAppTemplateInVDC", URI.class, URI.class,
            String.class, InstantiateVAppTemplateOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), URI.create("https://vcloud/vAppTemplate/3"),
            "name"));

      assertRequestLineEquals(request,
            "POST https://vcloud.safesecureweb.com/api/v0.8/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/InstantiateVAppTemplateParams-test.xml")),
            "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testInstantiateVAppTemplateInVDCURIOptions() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "instantiateVAppTemplateInVDC", URI.class, URI.class,
            String.class, InstantiateVAppTemplateOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(
            method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"),
            URI.create("https://vcloud/vAppTemplate/3"),
            "name",
            InstantiateVAppTemplateOptions.Builder.processorCount(2).memory(512).inGroup("group")
                  .withPassword("password").inRow("row")
                  .addNetworkConfig(new NetworkConfig(URI.create("http://network")))));

      assertRequestLineEquals(request,
            "POST https://vcloud.safesecureweb.com/api/v0.8/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream(
            "/InstantiateVAppTemplateParams-options-test.xml")),
            "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testActivatePublicIpInVDC() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "activatePublicIpInVDC", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1")));

      assertRequestLineEquals(request, "POST https://vcloud.safesecureweb.com/api/v0.8/publicIps/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.publicIp+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PublicIpAddressHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetAllInternetServices() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getAllInternetServicesInVDC", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/internetServices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServicesHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getInternetService", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "deleteInternetService", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12")));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIp() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "addInternetServiceToExistingIp", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/publicIp/12"),
            "name", Protocol.TCP, 22));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetService+xml\n");
      assertPayloadEquals(
            request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-test2.xml")).replace(
                  "vCloudExpressExtensions-1.6", "eCloudExtensions-2.8"),
            "application/vnd.tmrk.ecloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIpOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "addInternetServiceToExistingIp", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/publicIp/12"),
            "name", Protocol.TCP, 22, disabled().withDescription("yahoo").monitorDisabled()));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.internetService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-options-test.xml"))
                  .replace("vCloudExpressExtensions-1.6", "eCloudExtensions-2.8"),
            "application/vnd.tmrk.ecloud.internetService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "addNode", URI.class, String.class, String.class,
            int.class, AddNodeOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12"),
            "10.2.2.2", "name", 22));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService-test2.xml")).replace(
                  "vCloudExpressExtensions-1.6", "eCloudExtensions-2.8"),
            "application/vnd.tmrk.vCloud.nodeService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNodeOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "addNode", URI.class, String.class, String.class,
            int.class, AddNodeOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12"),
            "10.2.2.2", "name", 22, AddNodeOptions.Builder.disabled().withDescription("yahoo")));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");

      assertPayloadEquals(
            request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService-options-test.xml")).replace(
                  "vCloudExpressExtensions-1.6", "eCloudExtensions-2.8"),
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKeyPairInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "findKeyPairInOrg", URI.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"), "keyPair"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairByNameHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testConfigureNodeWithDescription() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "configureNode", URI.class, String.class,
            boolean.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/nodeService/12"),
            "name", true, "eggs"));

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            "<NodeService xmlns=\"urn:tmrk:eCloudExtensions-2.8\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled><Description>eggs</Description></NodeService>",
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testConfigureNodeNoDescription() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "configureNode", URI.class, String.class,
            boolean.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(URI.create("https://vcloud/extensions/nodeService/12"),
            "name", true, null));

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            "<NodeService xmlns=\"urn:tmrk:eCloudExtensions-2.8\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled></NodeService>",
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNodes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getNodes", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodesHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "deleteNode", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/nodeService/12")));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetCustomizationOptionsOfCatalogItem() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getCustomizationOptions", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/template/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/template/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "Accept: application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListKeyPairsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "listKeyPairsInOrg", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListKeyPairsInOrgNotFound() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "listKeyPairsInOrg", URI.class);
      processor.createRequest(method, ImmutableList.<Object> of(URI.create("d")));
   }

   public void testGetKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getKeyPair", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/key/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "deleteKeyPair", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/key/12")));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getNode", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/nodeService/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetExtendedInfo() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkECloudAsyncClient.class, "getVAppExtendedInfo", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/vapp/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/vapp/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.vApp+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppExtendedInfoHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }
}
