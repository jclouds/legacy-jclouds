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
package org.jclouds.trmk.vcloudexpress;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOG_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.NETWORK_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.ORG_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.TASKSLIST_XML;
import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.VDC_XML;
import static org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions.Builder.disabled;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogItemImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.OrgImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VDCImpl;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.functions.ParseTaskFromLocationHeader;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudLoginClient;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudVersionsClient;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.AddNodeOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.NetworkConfig;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CatalogItemHandler;
import org.jclouds.trmk.vcloud_0_8.xml.CustomizationParametersHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServiceHandler;
import org.jclouds.trmk.vcloud_0_8.xml.InternetServicesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairByNameHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairHandler;
import org.jclouds.trmk.vcloud_0_8.xml.KeyPairsHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodeHandler;
import org.jclouds.trmk.vcloud_0_8.xml.NodesHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VAppHandler;
import org.jclouds.trmk.vcloud_0_8.xml.VDCHandler;
import org.jclouds.trmk.vcloudexpress.config.TerremarkVCloudExpressRestClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
/**
 * Tests behavior of {@code TerremarkVCloudExpressAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", singleThreaded = true, testName = "TerremarkVCloudExpressAsyncClientTest")
public class TerremarkVCloudExpressAsyncClientTest extends BaseAsyncClientTest<TerremarkVCloudExpressAsyncClient> {

   public void testListOrgs() {
      assertEquals(injector.getInstance(TerremarkVCloudExpressAsyncClient.class).listOrgs().toString(), ImmutableMap
            .of(ORG_REF.getName(), ORG_REF).toString());
   }

   public void testCatalogItemURI() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getCatalogItem", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDelete() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "deleteVApp", URI.class);
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

   public void testFindCatalogItemInOrgCatalogNamed() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "findCatalogItemInOrgCatalogNamed",
            String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("org", "catalog", "item"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/catalogItem/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /**
    * ignore parameter of catalog id since this doesn't work
    */
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getCatalog", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getVDC", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "instantiateVAppTemplateInVDC", URI.class,
            URI.class, String.class, InstantiateVAppTemplateOptions[].class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "instantiateVAppTemplateInVDC", URI.class,
            URI.class, String.class, InstantiateVAppTemplateOptions[].class);
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

   public void testAddInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addInternetServiceToVDC", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), "name", Protocol.TCP, 22));

      assertRequestLineEquals(request, "POST https://vcloud.safesecureweb.com/api/v0.8/internetServices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-test2.xml")),
            "application/vnd.tmrk.vCloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addInternetServiceToVDC", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
            .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"), "name", Protocol.TCP, 22, disabled()
            .withDescription("yahoo")));

      assertRequestLineEquals(request, "POST https://vcloud.safesecureweb.com/api/v0.8/internetServices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-options-test.xml")),
            "application/vnd.tmrk.vCloud.internetService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetAllInternetServices() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getAllInternetServicesInVDC", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/internetServices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServicesHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getInternetService", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/internetService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetServicesList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeleteInternetService() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "deleteInternetService", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addInternetServiceToExistingIp", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/publicIp/12"),
            "name", Protocol.TCP, 22));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-test2.xml")),
            "application/vnd.tmrk.vCloud.internetService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddInternetServiceToExistingIpOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addInternetServiceToExistingIp", URI.class,
            String.class, Protocol.class, int.class, AddInternetServiceOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/publicIp/12"),
            "name", Protocol.TCP, 22, disabled().withDescription("yahoo")));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/publicIp/12/internetServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.internetService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService-options-test.xml")),
            "application/vnd.tmrk.vCloud.internetService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addNode", URI.class, String.class,
            String.class, int.class, AddNodeOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12"),
            "10.2.2.2", "name", 22));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService-test2.xml")),
            "application/vnd.tmrk.vCloud.nodeService+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddNodeOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "addNode", URI.class, String.class,
            String.class, int.class, AddNodeOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/internetService/12"),
            "10.2.2.2", "name", 22, AddNodeOptions.Builder.disabled().withDescription("yahoo")));

      assertRequestLineEquals(request, "POST https://vcloud/extensions/internetService/12/nodeServices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");

      assertPayloadEquals(request,
            Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService-options-test.xml")),
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKeyPairInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "findKeyPairInOrg", URI.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"), "keyPair"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vcloudExpress.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairByNameHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testConfigureNodeWithDescription() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "configureNode", URI.class, String.class,
            boolean.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/nodeService/12"),
            "name", true, "eggs"));

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled><Description>eggs</Description></NodeService>",
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testConfigureNodeNoDescription() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "configureNode", URI.class, String.class,
            boolean.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(URI.create("https://vcloud/extensions/nodeService/12"),
            "name", true, null));

      assertRequestLineEquals(request, "PUT https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(
            request,
            "<NodeService xmlns=\"urn:tmrk:vCloudExpressExtensions-1.6\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>name</Name><Enabled>true</Enabled></NodeService>",
            "application/vnd.tmrk.vCloud.nodeService+xml", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNodes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getNodes", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "deleteNode", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getCustomizationOptions", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud/extensions/template/12/options/customization")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/template/12/options/customization HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "Accept: application/vnd.tmrk.vCloud.catalogItemCustomizationParameters+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListKeyPairsInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "listKeyPairsInOrg", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1")));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vcloudExpress.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListKeyPairsInOrgNull() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "listKeyPairsInOrg", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((URI) null));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/keysList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vcloudExpress.keysList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairsHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListKeyPairsInOrgNotFound() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "listKeyPairsInOrg", URI.class);
      processor.createRequest(method, ImmutableList.<Object> of(URI.create("d")));
   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getNode", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/nodeService/12")));

      assertRequestLineEquals(request, "GET https://vcloud/extensions/nodeService/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.vCloud.nodeService+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "getKeyPair", URI.class);
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
      Invokable<?, ?> method = method(TerremarkVCloudExpressAsyncClient.class, "deleteKeyPair", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("https://vcloud/extensions/key/12")));

      assertRequestLineEquals(request, "DELETE https://vcloud/extensions/key/12 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected Module createModule() {
      return new TerremarkVCloudRestClientModuleExtension();
   }
   
   @Override
   public ProviderMetadata createProviderMetadata() {
      return new TerremarkVCloudExpressProviderMetadata();
   }

   protected static final ReferenceTypeImpl ORG_REF = new ReferenceTypeImpl("org", ORG_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

   protected static final ReferenceTypeImpl CATALOG_REF = new ReferenceTypeImpl("catalog", CATALOG_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"));

   protected static final ReferenceTypeImpl TASKSLIST_REF = new ReferenceTypeImpl("tasksList", TASKSLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/1"));

   protected static final ReferenceTypeImpl VDC_REF = new ReferenceTypeImpl("vdc", VDC_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"));

   protected static final ReferenceTypeImpl KEYSLIST_REF = new ReferenceTypeImpl("keysList",
         TerremarkVCloudExpressMediaType.KEYSLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/keysList/1"));

   protected static final ReferenceTypeImpl NETWORK_REF = new ReferenceTypeImpl("network", NETWORK_XML,
         URI.create("https://vcloud.safesecureweb.com/network/1990"));

   protected static final ReferenceTypeImpl PUBLICIPS_REF = new ReferenceTypeImpl("publicIps",
         TerremarkVCloudExpressMediaType.PUBLICIPSLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/publicIps/1"));

   protected static final ReferenceTypeImpl INTERNETSERVICES_REF = new ReferenceTypeImpl("internetServices",
         TerremarkVCloudExpressMediaType.INTERNETSERVICESLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/internetServices/1"));

   protected static final Org ORG = new OrgImpl(ORG_REF.getName(), ORG_REF.getType(), ORG_REF.getHref(), "org",
         ImmutableMap.<String, ReferenceType> of(CATALOG_REF.getName(), CATALOG_REF),
         ImmutableMap.<String, ReferenceType> of(VDC_REF.getName(), VDC_REF), ImmutableMap.<String, ReferenceType> of(
               TASKSLIST_REF.getName(), TASKSLIST_REF), KEYSLIST_REF);

   protected static final VDC VDC = new VDCImpl(VDC_REF.getName(), VDC_REF.getType(), VDC_REF.getHref(), "description",
         CATALOG_REF, PUBLICIPS_REF, INTERNETSERVICES_REF, ImmutableMap.<String, ReferenceType> of(
               "vapp",
               new ReferenceTypeImpl("vapp", "application/vnd.vmware.vcloud.vApp+xml", URI
                     .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1")),
               "network",
               new ReferenceTypeImpl("network", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                     .create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2"))),
         ImmutableMap.<String, ReferenceType> of(NETWORK_REF.getName(), NETWORK_REF));

      @ConfiguresRestClient
   protected static class TerremarkVCloudRestClientModuleExtension extends TerremarkVCloudExpressRestClientModule {

      @Override
      protected Supplier<URI> provideAuthenticationURI(TerremarkVCloudVersionsClient versionService, String version) {
         return Suppliers.ofInstance(URI.create("https://vcloud.safesecureweb.com/api/v0.8/login"));
      }

      @Override
      protected Supplier<Org> provideOrg(Supplier<Map<String, ? extends Org>> orgSupplier,
            @org.jclouds.trmk.vcloud_0_8.endpoints.Org Supplier<ReferenceType> defaultOrg) {
         return Suppliers.ofInstance(ORG);
      }

      @Override
      protected void installDefaultVCloudEndpointsModule() {
         install(new AbstractModule() {

            @Override
            protected void configure() {
               TypeLiteral<Supplier<ReferenceType>> refTypeSupplier = new TypeLiteral<Supplier<ReferenceType>>() {
               };
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Org.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(ORG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Catalog.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(CATALOG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.TasksList.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(TASKSLIST_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.VDC.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(VDC_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Network.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(NETWORK_REF));
            }

         });
      }

      @Override
      protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, TerremarkVCloudLoginClient login) {
         return Suppliers.<VCloudSession> ofInstance(new VCloudSession() {

            @Override
            public Map<String, ReferenceType> getOrgs() {
               return ImmutableMap.<String, ReferenceType> of(ORG_REF.getName(), ORG_REF);
            }

            @Override
            public String getVCloudToken() {
               return "token";
            }

         });

      }

      @Override
      protected void configure() {
         super.configure();
         bind(OrgMapSupplier.class).to(TestOrgMapSupplier.class);
         bind(OrgCatalogSupplier.class).to(TestOrgCatalogSupplier.class);
         bind(OrgCatalogItemSupplier.class).to(TestOrgCatalogItemSupplier.class);
      }

      @Override
      protected Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> provideOrgVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgVDCSupplier supplier) {
         return Suppliers
               .<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> ofInstance(ImmutableMap
                     .<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> of(ORG_REF.getName(),
                           ImmutableMap.<String, org.jclouds.trmk.vcloud_0_8.domain.VDC> of(VDC.getName(), VDC)));
      }

      @Singleton
      public static class TestOrgMapSupplier extends OrgMapSupplier {

         @Inject
         protected TestOrgMapSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Org> get() {
            return ImmutableMap.<String, Org> of(ORG.getName(), ORG);
         }
      }

      @Singleton
      public static class TestOrgCatalogSupplier extends OrgCatalogSupplier {
         @Inject
         protected TestOrgCatalogSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> get() {
            return ImmutableMap.<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> of(ORG_REF
                  .getName(), ImmutableMap.<String, org.jclouds.trmk.vcloud_0_8.domain.Catalog> of(
                  CATALOG_REF.getName(),
                  new CatalogImpl(CATALOG_REF.getName(), CATALOG_REF.getType(), CATALOG_REF.getHref(), null,
                        ImmutableMap.<String, ReferenceType> of(
                              "item",
                              new ReferenceTypeImpl("item", "application/vnd.vmware.vcloud.catalogItem+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/1")),
                              "template",
                              new ReferenceTypeImpl("template", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2"))))));
         }
      }

      @Singleton
      public static class TestOrgCatalogItemSupplier extends OrgCatalogItemSupplier {
         protected TestOrgCatalogItemSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>> get() {
            return ImmutableMap
                  .<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>> of(
                        ORG_REF.getName(),
                        ImmutableMap.<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>> of(
                              CATALOG_REF.getName(),
                              ImmutableMap
                                    .<String, org.jclouds.trmk.vcloud_0_8.domain.CatalogItem> of(
                                          "template",
                                          new CatalogItemImpl(
                                                "template",
                                                URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2"),
                                                "description",
                                                new ReferenceTypeImpl(
                                                      "template",
                                                      "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                                      URI.create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/2")),
                                                null, null, ImmutableMap.<String, String> of()))));

         }
      }

   }

}
