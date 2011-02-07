/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.config.CloudStackRestClientModule;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;

import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BaseCloudStackAsyncClientTest<T> extends RestClientTest<T> {

   @RequiresHttp
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
   public RestContextSpec<CloudStackClient, CloudStackAsyncClient> createContextSpec() {
      Properties props = new Properties();
      props.setProperty("cloudstack.endpoint", "http://localhost:8080/client/api");
      return new RestContextFactory().createContextSpec("cloudstack", "apiKey", "secretKey", props);
   }

}