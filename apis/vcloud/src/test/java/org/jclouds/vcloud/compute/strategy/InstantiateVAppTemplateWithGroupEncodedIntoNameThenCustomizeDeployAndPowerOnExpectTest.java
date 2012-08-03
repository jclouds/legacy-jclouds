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
package org.jclouds.vcloud.compute.strategy;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.compute.BaseVCloudComputeServiceExpectTest;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOnExpectTest")
public class InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOnExpectTest extends
         BaseVCloudComputeServiceExpectTest {

   String ns = "http://www.vmware.com/vcloud/v1";
   Properties outputProperties;
   public InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOnExpectTest(){
      outputProperties = new Properties();
      outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
   }

   HttpResponse successfulVersion1_0InstantiatedVApp = HttpResponse.builder()
         .statusCode(200)
         .message("HTTP/1.1 200 OK")
         .payload(payloadFromResourceWithContentType("/instantiatedvapp.xml",  "application/vnd.vmware.vcloud.vApp+xml")).build();

   // TODO: finish me!
   @Test(enabled = false)
   public void testCreateNodeUsingVCloud1_0ApiAgainstVCloudDirector1_5WhenVAppTemplateHasNetworkNamedNone()
            throws Exception {

      String group = "group";
      String name = "group-abcd";

      String instantiateXML = XMLBuilder.create("InstantiateVAppTemplateParams")
                                           .a("xmlns", ns).a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1")
                                           .a("deploy", "false").a("name", name).a("powerOn", "false")
                                        .e("Description").up()
                                        .e("InstantiationParams")
                                           .e("NetworkConfigSection")
                                              .e("ovf:Info").t("Configuration parameters for logical networks").up()
                                              .e("NetworkConfig").a("networkName", "orgNet-jclouds-External") // NOTE not "None"
                                                 .e("Configuration")
                                                    .e("ParentNetwork").a("href", ENDPOINT + "/v1.0/network/" + networkId).up()
                                                    .e("FenceMode").t("bridged").up()
                                                 .up()
                                              .up()
                                           .up()
                                        .up()
                                        .e("Source").a("href", ENDPOINT + "/v1.0/vAppTemplate/" + templateId).up()
                                        .e("AllEULAsAccepted").t("true").up()
                                        .asString(outputProperties);
     
      HttpRequest version1_0InstantiateWithNetworkNamedSameAsOrgNetwork = HttpRequest.builder().method("POST")
                                                                           .endpoint(ENDPOINT + "/v1.0/vdc/" + vdcId + "/action/instantiateVAppTemplate")
                                                                           .addHeader(HttpHeaders.ACCEPT, "application/vnd.vmware.vcloud.vApp+xml")
                                                                           .addHeader("x-vcloud-authorization", sessionToken)
                                                                           .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken)
                                                                           .payload(payloadFromStringWithContentType(instantiateXML, "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")).build();
                                                                        
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(versionsRequest, versionsResponseFromVCD1_5)
              .put(version1_0LoginRequest, successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg)
              .put(version1_0GetOrgRequest, successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork)
              .put(version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate)
              .put(version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate)
              .put(version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork)
              .put(version1_0GetVAppTemplateRequest, successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent)
              .put(version1_0GetOVFForVAppTemplateRequest, successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM)
              .put(version1_0InstantiateWithNetworkNamedSameAsOrgNetwork, successfulVersion1_0InstantiatedVApp).build());

      InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn starter = compute.getContext()
               .utils().injector().getInstance(
                        InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn.class);


      NodeAndInitialCredentials<VApp> appAndCreds = starter.createNodeWithGroupEncodedIntoName(group, name, compute
               .templateBuilder().build());

      assertEquals(appAndCreds.getNode().getName(), name);
      assertEquals(appAndCreds.getCredentials(), LoginCredentials.builder().user("root").password("fromVApp").build());

   }
   
   public void testInstantiateVAppFromTemplateWhenUsingOverriddenNetworkAndFenceMode()
         throws Exception {
   
      String name = "group-abcd";
      FenceMode fenceMode = FenceMode.NAT_ROUTED;
      URI parentNetwork = URI.create(ENDPOINT + "/v1.0/network/" + "foooooooo");
   
      String instantiateXML = XMLBuilder.create("InstantiateVAppTemplateParams")
                                           .a("xmlns", ns).a("xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1")
                                           .a("deploy", "false").a("name", name).a("powerOn", "false")
                                        .e("Description").up()
                                        .e("InstantiationParams")
                                           .e("NetworkConfigSection")
                                              .e("ovf:Info").t("Configuration parameters for logical networks").up()
                                              .e("NetworkConfig").a("networkName", "jclouds") // NOTE not "None"
                                                 .e("Configuration")
                                                    .e("ParentNetwork").a("href", parentNetwork.toASCIIString()).up()
                                                    .e("FenceMode").t(fenceMode.toString()).up()
                                                 .up()
                                              .up()
                                           .up()
                                        .up()
                                        .e("Source").a("href", ENDPOINT + "/v1.0/vAppTemplate/" + templateId).up()
                                        .e("AllEULAsAccepted").t("true").up()
                                        .asString(outputProperties);
     
      HttpRequest version1_0InstantiateWithCustomizedNetwork = HttpRequest.builder().method("POST")
                                                                          .endpoint(ENDPOINT + "/v1.0/vdc/" + vdcId + "/action/instantiateVAppTemplate")
                                                                          .addHeader(HttpHeaders.ACCEPT, "application/vnd.vmware.vcloud.vApp+xml")
                                                                          .addHeader("x-vcloud-authorization", sessionToken)
                                                                          .addHeader(HttpHeaders.COOKIE, "vcloud-token=" + sessionToken)
                                                                          .payload(payloadFromStringWithContentType(instantiateXML, "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")).build();
                                                                        
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse> builder()
              .put(versionsRequest, versionsResponseFromVCD1_5)
              .put(version1_0LoginRequest, successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg)
              .put(version1_0GetOrgRequest, successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork)
              .put(version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate)
              .put(version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate)
              .put(version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork)
              .put(version1_0GetVAppTemplateRequest, successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent)
              .put(version1_0GetOVFForVAppTemplateRequest, successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM)
              .put(version1_0InstantiateWithCustomizedNetwork, successfulVersion1_0InstantiatedVApp).build());
   
      InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn starter = compute.getContext()
               .utils().injector().getInstance(
                        InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn.class);

      Template template = compute.templateBuilder().build();
      template.getOptions().as(VCloudTemplateOptions.class).parentNetwork(parentNetwork).fenceMode(fenceMode);
      starter.instantiateVAppFromTemplate(name, template);

   }
}
