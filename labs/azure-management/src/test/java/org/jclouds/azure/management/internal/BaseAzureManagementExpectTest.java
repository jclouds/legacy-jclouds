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
package org.jclouds.azure.management.internal;

import java.util.Properties;

import org.jclouds.azure.management.config.AzureManagementProperties;
import org.jclouds.azure.management.config.AzureManagementRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseAzureManagementExpectTest<T> extends BaseRestApiExpectTest<T> {
   protected String subscriptionId = "a01234b5c-d6e7-8f9g-h0123-4567i890j1k";
   
   public BaseAzureManagementExpectTest() {
      provider = "azure-management";
      // self-signed dummy cert:
      // keytool -genkey -alias test -keyalg RSA -keysize 1024 -validity 5475 -dname "CN=localhost" -keystore azure-test.p12 -storepass azurepass -storetype pkcs12
      identity = this.getClass().getResource("/azure-test.p12").getFile();
      credential = "azurepass";
   }
   
   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(AzureManagementProperties.SUBSCRIPTION_ID, subscriptionId);
      return props;
   }
   
   @ConfiguresRestClient
   private static final class TestAzureManagementRestClientModule extends AzureManagementRestClientModule {

   }

   @Override
   protected Module createModule() {
      return new TestAzureManagementRestClientModule();
   }
}
