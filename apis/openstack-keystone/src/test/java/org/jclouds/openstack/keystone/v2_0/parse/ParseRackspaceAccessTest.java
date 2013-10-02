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

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseRackspaceAccessTest")
public class ParseRackspaceAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/raxAuth.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder()
                   .token(Token.builder()
                               .expires(new SimpleDateFormatDateService().iso8601DateParse("2012-06-06T20:56:47.000-05:00"))
                               .id("Auth_4f173437e4b013bee56d1007")
                               .tenant(Tenant.builder().id("40806637803162").name("40806637803162").build()).build())
                   .user(User.builder()
                             .id("54321")
                             .name("joe")
                             .role(Role.builder()
                                       .id("3")
                                       .name("identity:user-admin")
                                       .description("User Admin Role.").build()).build())
                   .service(Service.builder().name("cloudDatabases").type("rax:database")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://dfw.databases.api.rackspacecloud.com/v1.0/40806637803162")
                                                     .region("DFW").build())
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://ord.databases.api.rackspacecloud.com/v1.0/40806637803162")
                                                     .region("ORD").build()).build())
                   .service(Service.builder().name("cloudServers").type("compute")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://servers.api.rackspacecloud.com/v1.0/40806637803162")
                                                     .versionId("1.0")
                                                     .versionInfo("https://servers.api.rackspacecloud.com/v1.0")
                                                     .versionList("https://servers.api.rackspacecloud.com/").build()).build())
                   .service(Service.builder().name("cloudFiles").type("object-store")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
                                                     .publicURL("https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
                                                     .internalURL("https://snet-storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
                                                     .region("DFW").build()).build())
                   .service(Service.builder().name("cloudServersOpenStack").type("compute")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://dfw.servers.api.rackspacecloud.com/v2/40806637803162")
                                                     .versionInfo("https://dfw.servers.api.rackspacecloud.com/v2")
                                                     .versionList("https://dfw.servers.api.rackspacecloud.com/")
                                                     .versionId("2")
                                                     .region("DFW").build()).build())
                   .service(Service.builder().name("cloudLoadBalancers").type("rax:load-balancer")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://ord.loadbalancers.api.rackspacecloud.com/v1.0/40806637803162")
                                                     .region("ORD").build())
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/40806637803162")
                                                     .region("DFW").build()).build())
                   .service(Service.builder().name("cloudBlockStorage").type("volume")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://dfw.blockstorage.api.rackspacecloud.com/v1/40806637803162")
                                                     .region("DFW").build())
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://ord.blockstorage.api.rackspacecloud.com/v1/40806637803162")
                                                     .region("ORD").build()).build())
                   .service(Service.builder().name("cloudMonitoring").type("rax:monitor")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://monitoring.api.rackspacecloud.com/v1.0/40806637803162").build()).build())
                   .service(Service.builder().name("cloudDNS").type("rax:dns")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("40806637803162")
                                                     .publicURL("https://dns.api.rackspacecloud.com/v1.0/40806637803162").build()).build())
                   .service(Service.builder().name("cloudFilesCDN").type("rax:object-cdn")
                                   .endpoint(Endpoint.builder()
                                                     .tenantId("MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
                                                     .publicURL("https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
                                                     .region("DFW").build()).build()).build();
   }
}
