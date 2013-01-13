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
package org.jclouds.joyent.cloudapi.v6_5.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.BaseJoyentCloudComputeServiceExpectTest;
import org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions;
import org.jclouds.joyent.cloudapi.v6_5.features.DatasetApiExpectTest;
import org.jclouds.joyent.cloudapi.v6_5.features.MachineApiExpectTest;
import org.jclouds.joyent.cloudapi.v6_5.features.PackageApiExpectTest;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.ssh.SshKeyPairGenerator;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JoyentCloudComputeServiceExpectTest")
public class JoyentCloudComputeServiceExpectTest extends BaseJoyentCloudComputeServiceExpectTest {
   Properties onlySW = new Properties();
   
   public JoyentCloudComputeServiceExpectTest(){
      onlySW.setProperty(LocationConstants.PROPERTY_ZONES, "us-sw-1");
   }
   private ImmutableMap<String, String> keyPair = ImmutableMap.of("public", "ssh-rsa AAAAB3NzaC...", "private",
            "-----BEGIN RSA PRIVATE KEY-----\n");
   
   DatasetApiExpectTest datasets = new DatasetApiExpectTest();
   PackageApiExpectTest packages = new PackageApiExpectTest();
   MachineApiExpectTest machines = new MachineApiExpectTest();
   
   @Test
   public void testCreateNodeWithGeneratedKeyPairInWestRegion() throws Exception {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(getDatacenters, getDatacentersResponse);
      requestResponseMap.put(datasets.list, datasets.listResponse);
      requestResponseMap.put(packages.list, packages.listResponse);
      
      HttpRequest createKey = HttpRequest.builder().method("POST")
               .endpoint("https://api.joyentcloud.com/my/keys")
               .addHeader("X-Api-Version", "~6.5")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
               .payload(
                     payloadFromStringWithContentType(
                           "{\"name\":\"jclouds-test-0\",\"key\":\"" + keyPair.get("public") + "\"}",
                           "application/json")).build();

      HttpResponse createKeyResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
               .payload(payloadFromResourceWithContentType("/key.json", "application/json; charset=UTF-8"))
               .build();

      requestResponseMap.put(createKey, createKeyResponse);

      // look for number to start count at
      requestResponseMap.put(machines.list, machines.listResponse);

      HttpRequest createMachine = HttpRequest.builder().method("POST")
               .endpoint("https://us-sw-1.api.joyentcloud.com/my/machines?dataset=sdc%3Asdc%3Aubuntu-10.04%3A1.0.1&name=test-1&package=Small%201GB")
               .addHeader("X-Api-Version", "~6.5")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();

      HttpResponse createMachineResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
               .payload(payloadFromResourceWithContentType("/new_machine.json", "application/json; charset=UTF-8"))
               .build();

      requestResponseMap.put(createMachine, createMachineResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build(), new AbstractModule() {

         @Override
         protected void configure() {
            // predicatable node names
            final AtomicInteger suffix = new AtomicInteger();
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(new Supplier<String>() {

               @Override
               public String get() {
                  return suffix.getAndIncrement() + "";
               }

            });
            bind(SshKeyPairGenerator.class).toInstance(new SshKeyPairGenerator() {

               @Override
               public Map<String, String> get() {
                  return keyPair;
               }

            });
            
         }

      }, onlySW);

      TemplateOptions options = apiThatCreatesNode.templateOptions().blockUntilRunning(false);
      
      assertTrue(options.as(JoyentCloudTemplateOptions.class).shouldGenerateKey().get());

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1, options));
      
      assertEquals(node.getCredentials().getPrivateKey(), keyPair.get("private"));
   }
}
