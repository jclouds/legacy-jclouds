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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link ExtensionApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ExtensionApiLiveTest")
public class ExtensionApiLiveTest extends BaseNovaApiLiveTest {

    /**
     * Tests the listing of Extensions.
     * 
     * @throws Exception
     */
    @Test(description = "GET /v${apiVersion}/{tenantId}/extensions")
    public void testListExtensions() throws Exception {
       for (String zoneId : zones) {
          ExtensionApi extensionApi = api.getExtensionApiForZone(zoneId);
          Set<? extends Extension> response = extensionApi.list();
          assertNotNull(response);
          assertFalse(response.isEmpty());
           for (Extension extension : response) {
              assertNotNull(extension.getId());
              assertNotNull(extension.getName());
              assertNotNull(extension.getDescription());
              assertNotNull(extension.getNamespace());
              assertNotNull(extension.getUpdated());
              assertNotNull(extension.getLinks());
           }
       }
    }

    /**
     * Tests retrieval of Extensions using their alias.
     * 
     * @throws Exception
     */
    @Test(description = "GET /v${apiVersion}/{tenantId}/extensions/{alias}", dependsOnMethods = { "testListExtensions" })
    public void testGetExtensionByAlias() throws Exception {
       for (String zoneId : zones) {
           ExtensionApi extensionApi = api.getExtensionApiForZone(zoneId);
           Set<? extends Extension> response = extensionApi.list();
           for (Extension extension : response) {
              Extension details = extensionApi.get(extension.getId());
              assertNotNull(details);
              assertEquals(details.getId(), extension.getId());
              assertEquals(details.getName(), extension.getName());
              assertEquals(details.getDescription(), extension.getDescription());
              assertEquals(details.getNamespace(), extension.getNamespace());
              assertEquals(details.getUpdated(), extension.getUpdated());
              assertEquals(details.getLinks(), extension.getLinks());
           }
        }
    }

}
