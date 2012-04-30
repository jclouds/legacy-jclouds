/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.net.URI;
import java.util.Set;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.MediaType;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests UserClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "UserClientLiveTest")
public class UserClientLiveTest extends BaseKeystoneClientLiveTest {

   public void testGetApiMetaData() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         ApiMetadata result = keystoneContext.getApi().getUserClientForRegion(regionId).getApiMetadata();
         assertNotNull(result);
         assertNotNull(result.getId());
         assertNotNull(result.getStatus());
         assertNotNull(result.getUpdated());
      }
   }

   public void testListTenants() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         Set<Tenant> result = keystoneContext.getApi().getUserClientForRegion(regionId).listTenants();
         assertNotNull(result);
         assertFalse(result.isEmpty());

         for (Tenant tenant : result) {
            assertNotNull(tenant.getId());
         }
      }
   }
}
