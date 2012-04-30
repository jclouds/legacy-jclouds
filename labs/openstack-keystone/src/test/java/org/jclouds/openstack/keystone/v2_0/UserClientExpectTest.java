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
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of UserClient
 *
 * @author Adam Lowe
 */
public class UserClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {

   public void testGetApiMetaData() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            HttpRequest.builder().method("GET").endpoint(URI.create(endpoint + "/v2.0/")).
            headers(ImmutableMultimap.of("Accept", APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).
                  payload(payloadFromResourceWithContentType("/api_metadata.json", APPLICATION_JSON)).build())
            .getUserClientForRegion("region-a.geo-1");
      ApiMetadata metadata = client.getApiMetadata();
      assertNotNull(metadata);
      assertEquals(metadata.getId(), "v2.0");

      ApiMetadata expected = ApiMetadata.builder().id("v2.0")
            .links(ImmutableSet.of(Link.builder().relation(Link.Relation.SELF).href(URI.create("http://172.16.89.140:5000/v2.0/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("text/html").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/content/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("application/pdf").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/identity-dev-guide-2.0.pdf")).build()
            ))
            .status("beta")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-19T00:00:00Z"))
            .mediaTypes(ImmutableSet.of(
                  MediaType.builder().base("application/json").type("application/vnd.openstack.identity-v2.0+json").build(),
                  MediaType.builder().base("application/xml").type("application/vnd.openstack.identity-v2.0+xml").build()
            ))
            .build();

      assertEquals(metadata, expected);
   }

   public void testListTenants() {
      UserClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            HttpRequest.builder().method("GET").endpoint(URI.create(endpoint + "/v2.0/tenants")).
                  headers(ImmutableMultimap.of("Accept", APPLICATION_JSON, "X-Auth-Token", authToken)).build(),
            HttpResponse.builder().statusCode(200).
                  payload(payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
            .getUserClientForRegion("region-a.geo-1");
      Set<Tenant> tenants = client.listTenants();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      Set<Tenant> expected = ImmutableSet.of(
            Tenant.builder().name("demo").id("05d1dc7af71646deba64cfc17b81bec0").build(),
            Tenant.builder().name("admin").id("7aa2e17ec29f44d193c48feaba0852cc").build()
      );

      assertEquals(tenants, expected);
   }
}
