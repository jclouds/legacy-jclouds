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
package org.jclouds.cloudstack.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Map;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.compute.strategy.CloudStackComputeServiceAdapter;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.internal.BaseCloudStackComputeServiceContextExpectTest;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * Tests the compute service abstraction of the cloudstack api.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CloudStackComputeServiceAdapterExpectTest extends BaseCloudStackComputeServiceContextExpectTest<Injector> {
   HttpResponse deployVMResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/deployvirtualmachineresponse.json"))
         .build();
  
   HttpRequest queryAsyncJobResult = HttpRequest.builder().method("GET")
        .endpoint("http://localhost:8080/client/api")
        .addQueryParam("response", "json")
        .addQueryParam("command", "queryAsyncJobResult")
        .addQueryParam("jobid", "50006")
        .addQueryParam("apiKey", "APIKEY")
        .addQueryParam("signature", "v8BWKMxd%2BIzHIuTaZ9sNSzCWqFI%3D")
        .addHeader("Accept", "application/json")
        .build();

   HttpResponse queryAsyncJobResultResponse = HttpResponse.builder().statusCode(200)
        .payload(payloadFromResource("/queryasyncjobresultresponse-virtualmachine.json"))
        .build();
   
   public void testCreateNodeWithGroupEncodedIntoNameWithKeyPair() {
      HttpRequest deployVM = HttpRequest.builder().method("GET")
            .endpoint("http://localhost:8080/client/api")
            .addQueryParam("response", "json")
            .addQueryParam("command", "deployVirtualMachine")
            .addQueryParam("zoneid", "1")
            .addQueryParam("templateid", "4")
            .addQueryParam("serviceofferingid", "1")
            .addQueryParam("displayname", "test-e92")
            .addQueryParam("name", "test-e92")
            .addQueryParam("networkids", "204")
            .addQueryParam("keypair", "mykeypair")
            .addQueryParam("apiKey", "APIKEY")
            .addQueryParam("signature", "hI%2FU4cWXdU6KTZKbJvzPCmOpGmU%3D")
            .addHeader("Accept", "application/json")
            .build(); 
  
      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(listTemplates, listTemplatesResponse)
            .put(listOsTypes, listOsTypesResponse)
            .put(listOsCategories, listOsCategoriesResponse)
            .put(listZones, listZonesResponse)
            .put(listServiceOfferings, listServiceOfferingsResponse)
            .put(listAccounts, listAccountsResponse)
            .put(listNetworks, listNetworksResponse)
            .put(getZone, getZoneResponse)
            .put(deployVM, deployVMResponse)
            .put(queryAsyncJobResult, queryAsyncJobResultResponse)
            .build();

      Injector forKeyPair = requestsSendResponses(requestResponseMap);

      Template template = forKeyPair.getInstance(TemplateBuilder.class).osFamily(OsFamily.CENTOS).build();
      template.getOptions().as(CloudStackTemplateOptions.class).keyPair("mykeypair").setupStaticNat(false);

      CloudStackComputeServiceAdapter adapter = forKeyPair.getInstance(CloudStackComputeServiceAdapter.class);

      NodeAndInitialCredentials<VirtualMachine> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
            template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().password("dD7jwajkh").build());
   }

   public void testCreateNodeWithGroupEncodedIntoNameWithKeyPairAssignedToAccountAndDomain() {
      HttpRequest deployVM = HttpRequest.builder().method("GET")
            .endpoint("http://localhost:8080/client/api")
            .addQueryParam("response", "json")
            .addQueryParam("command", "deployVirtualMachine")
            .addQueryParam("zoneid", "1")
            .addQueryParam("templateid", "4")
            .addQueryParam("serviceofferingid", "1")
            .addQueryParam("displayname", "test-e92")
            .addQueryParam("name", "test-e92")
            .addQueryParam("account", "account") //
            .addQueryParam("domainid", "domainId") //
            .addQueryParam("networkids", "204")
            .addQueryParam("keypair", "mykeypair")
            .addQueryParam("apiKey", "APIKEY")
            .addQueryParam("signature", "ly5Pip8ICOoVTmNLdDBTc3gbKlA%3D")
            .addHeader("Accept", "application/json")
            .build();
  
      Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(listTemplates, listTemplatesResponse)
            .put(listOsTypes, listOsTypesResponse)
            .put(listOsCategories, listOsCategoriesResponse)
            .put(listZones, listZonesResponse)
            .put(listServiceOfferings, listServiceOfferingsResponse)
            .put(listAccounts, listAccountsResponse)
            .put(listNetworks, listNetworksResponse)
            .put(getZone, getZoneResponse)
            .put(deployVM, deployVMResponse)
            .put(queryAsyncJobResult, queryAsyncJobResultResponse)
            .build();

      Injector forKeyPair = requestsSendResponses(requestResponseMap);

      Template template = forKeyPair.getInstance(TemplateBuilder.class).osFamily(OsFamily.CENTOS).build();
      template.getOptions().as(CloudStackTemplateOptions.class).keyPair("mykeypair").account("account").domainId("domainId").setupStaticNat(false);

      CloudStackComputeServiceAdapter adapter = forKeyPair.getInstance(CloudStackComputeServiceAdapter.class);

      NodeAndInitialCredentials<VirtualMachine> server = adapter.createNodeWithGroupEncodedIntoName("test", "test-e92",
            template);
      assertNotNull(server);
      assertEquals(server.getCredentials(), LoginCredentials.builder().password("dD7jwajkh").build());
   }   
   
   @Override
   protected Injector clientFrom(CloudStackContext context) {
      return context.utils().injector();
   }
}
