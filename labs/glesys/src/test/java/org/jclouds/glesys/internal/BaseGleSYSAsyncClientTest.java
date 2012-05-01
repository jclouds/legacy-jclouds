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
package org.jclouds.glesys.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.glesys.GleSYSProviderMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;

/**
 * @author Adrian Cole
 */
public abstract class BaseGleSYSAsyncClientTest<T> extends BaseAsyncClientTest<T> {

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   public ProviderMetadata createProviderMetadata() {
     return new GleSYSProviderMetadata();   
   }
}
