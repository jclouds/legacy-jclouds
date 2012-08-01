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
package org.jclouds.azure.servicemanagement;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import org.jclouds.azure.servicemanagement.AzureServiceManagementAsyncClient;
import org.jclouds.azure.servicemanagement.AzureServiceManagementClient;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AzureVirtualMachinesClient}
 * 
 * @author Gérald Pereira
 */
@Test(groups = "live", testName = "azure-servicemanagement.AzureServiceManagementClientLiveTest")
public class AzureServiceManagementClientLiveTest {

   protected RestContext<AzureServiceManagementClient, AzureServiceManagementAsyncClient> context;
   protected String provider = "azurevirtualmachines";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiVersion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test.azure-servicemanagement.identity"), "test.azure-servicemanagement.identity");
      credential = checkNotNull(System.getProperty("test.azure-servicemanagement.credential"), "test.azure-servicemanagement.credential");
      endpoint = checkNotNull(System.getProperty("test.azure-servicemanagement.endpoint"), "test.azure-servicemanagement.endpoint");
      apiVersion = checkNotNull(System.getProperty("test.azure-servicemanagement.api-version"), "test.azure-servicemanagement.api-version");
   }
   
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
//      context = createContext(contextSpec(provider, endpoint, apiVersion, identity, credential,
//               AzureVirtualMachinesClient.class, AzureVirtualMachinesAsyncClient.class), ImmutableSet.<Module> of(new Log4JLoggingModule()));
   }

   @Test
   public void testList() throws Exception {
//      String response = context.getApi().list();
//      assertNotNull(response);
   }

   @Test
   public void testGet() throws Exception {
//      String response = context.getApi().get(1l);
//      assertNotNull(response);
   }

   /*
    * TODO: add tests for AzureVirtualMachines interface methods
    */
}
