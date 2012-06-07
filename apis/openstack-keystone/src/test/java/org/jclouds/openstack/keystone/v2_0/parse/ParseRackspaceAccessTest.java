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
public class ParseRackspaceAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/raxAuth.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder().token(
               Token.builder().expires(new SimpleDateFormatDateService().iso8601DateParse("2012-06-06T20:56:47.000-05:00"))
                        .id("Auth_4f173437e4b013bee56d1007").tenant(
                                 Tenant.builder().id("40806637803162").name("40806637803162").build())
                        .build()).user(
               User.builder().id("54321").name("joe").roles(
                        Role.builder().id("3").name("identity:user-admin").description("User Admin Role.")
                                 .build()).build()).serviceCatalog(

               Service.builder().name("cloudDatabases").type("rax:database").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://dfw.databases.api.rackspacecloud.com/v1.0/40806637803162"))
                                 .region("DFW").build(),
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://ord.databases.api.rackspacecloud.com/v1.0/40806637803162"))
                                 .region("ORD").build()).build(),

               Service.builder().name("cloudServers").type("compute").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://servers.api.rackspacecloud.com/v1.0/40806637803162"))
                                 .versionId("1.0").versionInfo(URI.create("https://servers.api.rackspacecloud.com/v1.0"))
                                 .versionList(URI.create("https://servers.api.rackspacecloud.com/")).build()).build(),

               Service.builder().name("cloudFiles").type("object-store").endpoints(
                        Endpoint.builder().tenantId("MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22").publicURL(
                                 URI.create("https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22"))
                                 .internalURL(
                                 URI.create("https://snet-storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22"))
                                 .region("DFW").build()).build(),
                                 
               Service.builder().name("cloudServersOpenStack").type("compute").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://dfw.servers.api.rackspacecloud.com/v2/40806637803162"))
                                 .versionInfo(URI.create("https://dfw.servers.api.rackspacecloud.com/v2"))
                                 .versionList(URI.create("https://dfw.servers.api.rackspacecloud.com/"))
                                 .versionId("2")
                                 .region("DFW").build()).build(),                 

               Service.builder().name("cloudLoadBalancers").type("rax:load-balancer").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://ord.loadbalancers.api.rackspacecloud.com/v1.0/40806637803162"))
                                 .region("ORD").build(),
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/40806637803162"))
                                 .region("DFW").build()).build(),
                                 
               Service.builder().name("cloudMonitoring").type("rax:monitor").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://monitoring.api.rackspacecloud.com/v1.0/40806637803162")).build()).build(),
               
               Service.builder().name("cloudDNS").type("dnsextension:dns").endpoints(
                        Endpoint.builder().tenantId("40806637803162").publicURL(
                                 URI.create("https://dns.api.rackspacecloud.com/v1.0/40806637803162")).build()).build(),   
                                 
               Service.builder().name("cloudFilesCDN").type("rax:object-cdn").endpoints(
                        Endpoint.builder().tenantId("MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22").publicURL(
                                 URI.create("https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22"))
                                 .region("DFW").build()).build()
            ).build();
   }
}
