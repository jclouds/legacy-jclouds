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
package org.jclouds.openstack.nova.v2_0.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedAttributes;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerWithAllExtensionsTest")
public class ParseServerWithAllExtensionsTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/server_details_devstack.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
            .builder()
            .id("141b775f-7ac1-45f0-9a95-146260f33a53")
            .tenantId("7f312675f9b84c97bff8f5054e181419")
            .userId("89c01b67395d4bea945f7f5bfd7f344a")
            .name("test")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-04T15:07:48Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-04T15:07:36Z"))
            .hostId("eab9a77d1c44b8833e4a3dc6d2d9d50de556e780a319f184d8c82d9b")
            .status(Status.PAUSED)
            .image(
                  Resource
                        .builder()
                        .id("8e6f5bc4-a210-45b2-841f-c510eae14300")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://172.16.89.149:8774/7f312675f9b84c97bff8f5054e181419/images/8e6f5bc4-a210-45b2-841f-c510eae14300")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("1")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://172.16.89.149:8774/7f312675f9b84c97bff8f5054e181419/flavors/1")))
                        .build())
            .links(
                  Link.create(
                        Relation.SELF,
                        URI.create("http://172.16.89.149:8774/v2/7f312675f9b84c97bff8f5054e181419/servers/141b775f-7ac1-45f0-9a95-146260f33a53")),
                  Link.create(
                        Relation.BOOKMARK,
                        URI.create("http://172.16.89.149:8774/7f312675f9b84c97bff8f5054e181419/servers/141b775f-7ac1-45f0-9a95-146260f33a53")))
            .addresses(ImmutableMultimap.of("private", Address.createV4("10.0.0.8")))
            .diskConfig("MANUAL")
            .extendedStatus(ServerExtendedStatus.builder().vmState("paused").powerState(3).build())
            .extendedAttributes(ServerExtendedAttributes.builder().instanceName("instance-00000014").hostName("ubuntu").build())
            .build();
   }
  

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
