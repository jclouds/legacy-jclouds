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
package org.jclouds.openstack.keystone.v2_0.parse;

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

import static org.jclouds.openstack.v2_0.ServiceType.BLOCK_STORAGE;
import static org.jclouds.openstack.v2_0.ServiceType.COMPUTE;
import static org.jclouds.openstack.v2_0.ServiceType.DATABASE_SERVICE;
import static org.jclouds.openstack.v2_0.ServiceType.IDENTITY;
import static org.jclouds.openstack.v2_0.ServiceType.IMAGE;
import static org.jclouds.openstack.v2_0.ServiceType.NETWORK;
import static org.jclouds.openstack.v2_0.ServiceType.OBJECT_STORE;


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
      return Access.builder()
                   .token(Token.builder()
                               .expires(new SimpleDateFormatDateService().iso8601DateParse("2012-01-18T21:35:59.050Z"))
                               .id("Auth_4f173437e4b013bee56d1007")
                               .tenant(Tenant.builder().id("40806637803162").name("user@jclouds.org-default-tenant").build()).build())
                   .user(User.builder()
                             .id("36980896575174").name("user@jclouds.org")
                             .role(Role.builder().id("00000000004022").serviceId("110").name("Admin").tenantId("40806637803162").build())
                             .role(Role.builder().id("00000000004024").serviceId("140").name("user").tenantId("40806637803162").build())
                             .role(Role.builder().id("00000000004004").serviceId("100").name("domainuser").build())
                             .role(Role.builder().id("00000000004016").serviceId("120").name("netadmin").tenantId("40806637803162").build()).build())
                   .service(Service.builder().name("Object Storage").type(OBJECT_STORE)
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://objects.jclouds.org/v1.0/40806637803162")
                                                     .adminURL("https://objects.jclouds.org/v1.0/")
                                                     .id("1.0")
                                                     .region("region-a.geo-1").build()).build())
                   .service(Service.builder().name("Identity").type(IDENTITY)
                                   .endpoint(Endpoint.builder()
                                                     .publicURL("https://csnode.jclouds.org/v2.0/")
                                                     .adminURL("https://csnode.jclouds.org:35357/v2.0/")
                                                     .region("region-a.geo-1")
                                                     .id("2.0")
                                                     .versionId("2.0").build()).build())
                   .service(Service.builder().name("Image Management").type(IMAGE)
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://glance.jclouds.org:9292/v1.0")
                                                     .region("az-1.region-a.geo-1")
                                                     .id("1.0").build()).build())
                   .service(Service.builder().name("Compute").type(COMPUTE)
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("3456")
                                                     .publicURL("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456")
                                                     .region("az-1.region-a.geo-1")
                                                     .versionId("1.1")
                                                     .versionInfo("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/")
                                                     .versionList("https://az-1.region-a.geo-1.compute.hpcloudsvc.com").build())
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("3456")
                                                     .publicURL("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456")
                                                     .region("az-2.region-a.geo-1")
                                                     .versionId("1.1")
                                                     .versionInfo("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/")
                                                     .versionList("https://az-2.region-a.geo-1.compute.hpcloudsvc.com").build())
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("3456")
                                                     .publicURL("https://az-3.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456")
                                                     .region("az-3.region-a.geo-1")
                                                     .versionId("1.1")
                                                     .versionInfo("https://az-3.region-a.geo-1.compute.hpcloudsvc.com/v1.1/")
                                                     .versionList("https://az-3.region-a.geo-1.compute.hpcloudsvc.com").build()).build())
                   .service(Service.builder().name("Quantum Service").type(NETWORK)
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("3456")
                                                     .publicURL("https://csnode.jclouds.org:9696/v1.0/tenants/3456")
                                                     .internalURL("https://csnode.jclouds.org:9696/v1.0/tenants/3456")
                                                     .adminURL("https://csnode.jclouds.org:9696/v1.0")
                                                     .region("region-a.geo-1")
                                                     .versionId("1.0").build()).build())
                  .service(Service.builder().name("cinder").type(BLOCK_STORAGE)
                        .endpoint(Endpoint.builder()
                                          .id("08330c2dcbfc4c6c8dc7a0949fbf5da7")
                                          .publicURL("http://172.16.0.1:8776/v1/50cdb4c60374463198695d9f798fa34d")
                                          .internalURL("http://10.0.2.15:8776/v1/50cdb4c60374463198695d9f798fa34d")
                                          .adminURL("http://10.0.2.15:8776/v1/50cdb4c60374463198695d9f798fa34d")
                                          .region("RegionOne").build()).build())
                  .service(Service.builder().name("reddwarf").type(DATABASE_SERVICE)
                        .endpoint(Endpoint.builder()
                                          .publicURL("http://172.16.0.1:8776/v1/3456")
                                          .tenantId("123123")
                                          .region("RegionOne").build()).build())
                  .service(Service.builder().name("dns").type("dns")
                        .endpoint(Endpoint.builder()
                                          .publicURL("http://172.16.0.1:8776/v1/3456")
                                          .tenantId("3456")
                                          .build()).build()).build();
   }

}
