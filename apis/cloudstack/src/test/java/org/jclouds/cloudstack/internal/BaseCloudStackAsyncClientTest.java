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
package org.jclouds.cloudstack.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.config.CloudStackRestClientModule;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.http.HttpRequest;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;

import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BaseCloudStackAsyncClientTest<T> extends BaseAsyncClientTest<T> {

      @ConfiguresRestClient
   public static class CloudStackRestClientModuleExtension extends CloudStackRestClientModule {

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), QuerySigner.class);
   }

   @Override
   protected Module createModule() {
      return new CloudStackRestClientModuleExtension();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      return  AnonymousProviderMetadata.forApiWithEndpoint(new CloudStackApiMetadata(),
            "http://localhost:8080/client/api");
   }

}
