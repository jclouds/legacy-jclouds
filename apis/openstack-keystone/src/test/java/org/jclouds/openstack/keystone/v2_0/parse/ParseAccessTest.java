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
package org.jclouds.openstack.keystone.v2_0.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseAccessTest")
public class ParseAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/keystoneAuthResponse.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder().token(
               Token.builder().expires(new SimpleDateFormatDateService().iso8601DateParse("2012-01-18T21:35:59.050Z"))
                        .id("Auth_4f173437e4b013bee56d1007").tenant(
                                 Tenant.builder().id("40806637803162").name("user@jclouds.org-default-tenant").build())
                        .build()).user(
               User.builder().id("36980896575174").name("user@jclouds.org").roles(
                        Role.builder().id("00000000004022").serviceId("110").name("Admin").tenantId("40806637803162")
                                 .build(),
                        Role.builder().id("00000000004024").serviceId("140").name("user").tenantId("40806637803162")
                                 .build(),
                        Role.builder().id("00000000004004").serviceId("100").name("domainuser").build(),
                        Role.builder().id("00000000004016").serviceId("120").name("netadmin")
                                 .tenantId("40806637803162").build()).build()).serviceCatalog(

               Service.builder().name("Object Storage").type("object-store").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://objects.jclouds.org/v1.0/40806637803162"))
                                 .adminURL(URI.create("https://objects.jclouds.org/v1.0/"))
                                 .region("region-a.geo-1").versionId("1.0").build()).build(),

               Service.builder().name("Identity").type("identity").endpoints(
                        Endpoint.builder().publicURL(URI.create("https://csnode.jclouds.org/v2.0/"))
                                 .adminURL(URI.create("https://csnode.jclouds.org:35357/v2.0/"))
                                 .region("region-a.geo-1").versionId("2.0").build()).build(),

               Service.builder().name("Image Management").type("image").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://glance.jclouds.org:9292/v1.0")).region("az-1.region-a.geo-1")
                                 .versionId("1.0").build()).build(),

               Service.builder().name("Compute").type("compute").endpoints(
                        Endpoint.builder()
                                .tenantId("3456")
                                .publicURL(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456"))
                                .region("az-1.region-a.geo-1")
                                .versionId("1.1")
                                .versionInfo(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/"))
                                .versionList(URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com")).build(),
                        Endpoint.builder()
                                .tenantId("3456")
                                .publicURL(URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456"))
                                .region("az-2.region-a.geo-1")
                                .versionId("1.1")
                                .versionInfo(URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/"))
                                .versionList(URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com")).build(),
                        Endpoint.builder()
                                .tenantId("3456")
                                .publicURL(URI.create("https://az-3.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456"))
                                .region("az-3.region-a.geo-1")
                                .versionId("1.1")
                                .versionInfo(URI.create("https://az-3.region-a.geo-1.compute.hpcloudsvc.com/v1.1/"))
                                .versionList(URI.create("https://az-3.region-a.geo-1.compute.hpcloudsvc.com")).build()).build(),

            Service.builder().name("Quantum Service").type("network").endpoints(
                  Endpoint.builder()
                        .tenantId("3456")
                        .publicURL(URI.create("https://csnode.jclouds.org:9696/v1.0/tenants/3456"))
                        .internalURL(URI.create("https://csnode.jclouds.org:9696/v1.0/tenants/3456"))
                        .adminURL(URI.create("https://csnode.jclouds.org:9696/v1.0"))
                        .region("region-a.geo-1")
                        .versionId("1.0").build()
                     ).build())
            
            .build();
   }

}
