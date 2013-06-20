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
package org.jclouds.cloudstack.compute.extensions;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Properties;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.compute.CloudStackComputeService;
import org.jclouds.cloudstack.internal.BaseCloudStackComputeServiceContextExpectTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "CloudStackImageExtensionExpectTest")
public class CloudStackImageExtensionExpectTest extends BaseCloudStackComputeServiceContextExpectTest<ComputeService> {

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty("jclouds.zones", "MTV-Zone1");
      return overrides;
   }
   
   public void testCreateImage() {
      HttpRequest listVM = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "listVirtualMachines")
         .addQueryParam("listAll", "true")
         .addQueryParam("id", "3239ade9-fd25-405c-8eda-59f0313a3fb0")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "Qq7Br3qNsyr5ifWZHIrLAslhwm0%3D")
         .addHeader("Accept", "application/json")
         .build(); 

      HttpResponse listVMResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/listvirtualmachinesresponse-imageextension.json"))
         .build();

      HttpRequest stopVM = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "stopVirtualMachine")
         .addQueryParam("id", "3239ade9-fd25-405c-8eda-59f0313a3fb0")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "y9vxRK61K8sDoWtvSJHIx5WO9AE%3D")
         .addHeader("Accept", "application/json")
         .build(); 

      HttpResponse stopVMResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/stopvirtualmachineresponse-imageextension.json"))
         .build();

      HttpRequest stopAsyncJobResult = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "queryAsyncJobResult")
         .addQueryParam("jobid", "a7d5127b-24a2-4a44-a4a7-25a6d057b453")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "CVpnN%2FSbx%2FMCOOyj%2FoVAt3bn684%3D")
         .addHeader("Accept", "application/json")
         .build();

      HttpResponse stopAsyncJobResultResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/queryasyncjobresultresponse-stopvirtualmachine-imageextension.json"))
         .build();

      HttpRequest listVolumes = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "listVolumes")
         .addQueryParam("listAll", "true")
         .addQueryParam("virtualmachineid", "3239ade9-fd25-405c-8eda-59f0313a3fb0")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "drLPf9NE9ROZPOfeDkASiKa50t8%3D")
         .addHeader("Accept", "application/json")
         .build(); 

      HttpResponse listVolumesResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/listvolumesresponse-imageextension.json"))
         .build();


      HttpRequest createTemplate = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "createTemplate")
         .addQueryParam("volumeid", "fe1ada16-57a0-40ae-b577-01a153690fb4")
         .addQueryParam("name", "temp-template-ignore")
         .addQueryParam("ostypeid", "45de18f1-87c6-4646-8099-95c61f2a300a")
         .addQueryParam("displaytext", "temp-template-ignore")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "madHsBgxjYbM6JnZKYWajOlfPlY%3D")
         .addHeader("Accept", "application/json")
         .build(); 

      HttpResponse createTemplateResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/createtemplateresponse-imageextension.json"))
         .build();

      HttpRequest createAsyncJobResult = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "queryAsyncJobResult")
         .addQueryParam("jobid", "4e345230-8fcc-48a3-8a37-c5fe960df671")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "6mTKL9fjz7bn6C7tOaZBzKdZwHs%3D")
         .addHeader("Accept", "application/json")
         .build();

      HttpResponse createAsyncJobResultResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/queryasyncjobresultresponse-createtemplate-imageextension.json"))
         .build();

      HttpRequest getTemplate = HttpRequest.builder().method("GET")
         .endpoint("http://localhost:8080/client/api")
         .addQueryParam("response", "json")
         .addQueryParam("command", "listTemplates")
         .addQueryParam("listAll", "true")
         .addQueryParam("templatefilter", "executable")
         .addQueryParam("id", "3dc6ce25-a6cf-4d60-a664-3499993b511b")
         .addQueryParam("apiKey", "APIKEY")
         .addQueryParam("signature", "dXv%2Bl04EDd7hmrWv5CdW8v298RE%3D")
         .addHeader("Accept", "application/json")
         .build(); 

      HttpResponse getTemplateResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResource("/listtemplatesresponse-imageextension.json"))
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
            .put(listVM, listVMResponse)
            .put(stopVM, stopVMResponse)
            .put(stopAsyncJobResult, stopAsyncJobResultResponse)
            .put(listVolumes, listVolumesResponse)
            .put(createTemplate, createTemplateResponse)
            .put(createAsyncJobResult, createAsyncJobResultResponse)
            .put(getTemplate, getTemplateResponse)
            .build();

      ImageExtension apiThatCreatesImage = requestsSendResponses(requestResponseMap).getImageExtension().get();
      
      ImageTemplate newImageTemplate = apiThatCreatesImage.buildImageTemplateFromNode("temp-template-ignore", "3239ade9-fd25-405c-8eda-59f0313a3fb0");
      
      Image image = Futures.getUnchecked(apiThatCreatesImage.createImage(newImageTemplate));
      assertEquals(image.getId(), "3dc6ce25-a6cf-4d60-a664-3499993b511b");
   }

   @Override
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return clientFrom(createInjector(fn, module, props).getInstance(CloudStackContext.class));
   }

   @Override
   protected ComputeService clientFrom(CloudStackContext context) {
      return context.getComputeService();
   }

}
