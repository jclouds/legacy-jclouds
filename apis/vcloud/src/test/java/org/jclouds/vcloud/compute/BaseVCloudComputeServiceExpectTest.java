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
package org.jclouds.vcloud.compute;

import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.VCloudMediaType;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;

/**
 * Base class for writing VCloud Expect tests for ComputeService operations
 * 
 * @author Adrian Cole
 */
public abstract class BaseVCloudComputeServiceExpectTest extends BaseRestClientExpectTest<ComputeService> {
   protected static final String ENDPOINT = "https://zone.myvcloud.com/api";

   protected HttpRequest versionsRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/versions")).build();

   protected HttpResponse versionsResponseFromVCD1_5 = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/versions-vcd15.xml", "text/xml"))
            .build();

   // initial auth is using basic
   protected HttpRequest version1_0LoginRequest = HttpRequest.builder().method("POST").endpoint(
            URI.create(ENDPOINT + "/v1.0/login"))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.ORGLIST_XML)
            .put(HttpHeaders.AUTHORIZATION, "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

   protected String sessionToken = "AtatAgvJMrwOc9pDQq4RRCRLazThpnTKJDxSVH9oB2I=";
   
   // login response includes a cookie and also a vcloud extended header with the session token in it
   // NOTE: vCloud Director 1.5 returns ;version=1.0 on responses to requests made in 1.0 format.
   protected HttpResponse successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/orgList1.0-vcd15.xml",  VCloudMediaType.ORGLIST_XML +";version=1.0"))
            .headers(ImmutableMultimap.<String, String> builder()
            .put("x-vcloud-authorization", sessionToken)
            .put(HttpHeaders.SET_COOKIE, String.format("vcloud-token=%s; Secure; Path=/", sessionToken)).build()).build();

   // objects are looked up by id and the format of the id is hex-hyphen
   protected String orgId = "c076f90a-397a-49fa-89b8-b294c1599cd0";
   
   protected HttpRequest version1_0GetOrgRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/org/" + orgId))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.ORG_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/org1.0-vcd15.xml",  VCloudMediaType.ORG_XML +";version=1.0"))
            .build();
   
   protected String catalogId = "3155f393-1e1d-4572-8c9c-d76f72ddb658";
   protected String vdcId = "e9cd3387-ac57-4d27-a481-9bee75e0690f";

   protected HttpRequest version1_0GetCatalogRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/catalog/" + catalogId))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.CATALOG_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/catalog1.0-vcd15.xml",  VCloudMediaType.CATALOG_XML +";version=1.0"))
            .build();   
   
   protected String catalogItemId = "ceb369f7-1d07-4e32-9dbd-ebb5aa6ca55c";
   
   protected HttpRequest version1_0GetCatalogItemRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/catalogItem/" + catalogItemId))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.CATALOGITEM_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/catalogItem1.0-vcd15.xml",  VCloudMediaType.CATALOGITEM_XML +";version=1.0"))
            .build();   
   
   // note vAppTemplate has a prefix in its id
   protected String templateId = "vappTemplate-51891b97-c5dd-47dc-a687-aabae354f728";

   protected HttpRequest version1_0GetVDCRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/vdc/" + vdcId))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.VDC_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/vdc1.0-vcd15.xml",  VCloudMediaType.VDC_XML +";version=1.0"))
            .build();   
   
   protected String networkId = "b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1";
   
   protected HttpRequest version1_0GetVAppTemplateRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/vAppTemplate/" + templateId))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, VCloudMediaType.VAPPTEMPLATE_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/template1.0-vcd15.xml",  VCloudMediaType.VAPPTEMPLATE_XML +";version=1.0"))
            .build();   

   protected HttpResponse successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithMultipleVMsAndVDCParent = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/template1.0-vcd15-multi-vm.xml",  VCloudMediaType.VAPPTEMPLATE_XML +";version=1.0"))
            .build();   

   protected HttpRequest version1_0GetOVFForVAppTemplateRequest = HttpRequest.builder().method("GET").endpoint(
            URI.create(ENDPOINT + "/v1.0/vAppTemplate/" + templateId + "/ovf"))
            .headers(ImmutableMultimap.<String, String> builder()
            .put(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .put(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken).build()).build();
   
   protected HttpResponse successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/ovf-ubuntu64.xml",  MediaType.TEXT_XML +";version=1.0"))
            .build();   

   protected HttpResponse successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithMultipleVMs = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK").payload(payloadFromResourceWithContentType("/ovf-multi-vm.xml",  MediaType.TEXT_XML +";version=1.0"))
            .build();   

   public BaseVCloudComputeServiceExpectTest() {
      provider = "vcloud";
   }

   @Override
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeService.class);
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return new VCloudApiMetadata();
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(provider + ".endpoint", ENDPOINT);
      return props;
   }
}
