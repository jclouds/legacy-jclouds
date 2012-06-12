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
 * Unless required by applicable law or agreed to in writing)
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.v2_0.parse;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerExtendedStatus;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerDetailsEssexTest")
public class ParseServerDetailsEssexTest extends BaseSetParserTest<Server> {

   @Override
   public String resource() {
      return "/server_list_details_essex.json";
   }

   @Override
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Server> expected() {
      return ImmutableSet.<Server>of(
            Server.builder()
                  .addresses(ImmutableMultimap.<String, Address>builder()
                        .putAll("Net TenantA Front-Middle", Address.createV4("172.16.11.5"))
                        .putAll("Public network", Address.createV4("172.16.1.13"), Address.createV4("10.193.112.119")).build())
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://nova:8774/v1.1/8d10e6646d5d4585937395b04839a353/servers/0c80b392-db30-4736-ae02-4480090f1207")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/servers/0c80b392-db30-4736-ae02-4480090f1207")))
                  .image(
                        Resource.builder()
                              .id("416af940-2d3c-4a7c-977c-a9030685ad5e")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/images/416af940-2d3c-4a7c-977c-a9030685ad5e"))).build())
                  .flavor(
                        Resource.builder()
                              .id("1")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/flavors/1"))).build())
                  .id("0c80b392-db30-4736-ae02-4480090f1207")
                  .userId("df13814f6c354d00a8acf66502836323")
                  .status(Status.ACTIVE)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:21:33Z"))
                  .hostId("03d796ebb52b1b555e5f6d9262f7dbd52b3f7c181e3aa89b34ca5408")
                  .name("VM proxy")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:21:23Z"))
                  .tenantId("8d10e6646d5d4585937395b04839a353")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("active").powerState(1).build())
                  .diskConfig("MANUAL").build(),
            Server.builder()
                  .addresses(ImmutableMultimap.<String, Address>builder()
                        .putAll("Net TenantA Front-Middle", Address.createV4("172.16.11.4"))
                        .putAll("Net TenantA Middle-Back", Address.createV4("172.16.12.5")).build())
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://nova:8774/v1.1/8d10e6646d5d4585937395b04839a353/servers/b332b5cd-535e-4677-b68e-fc8badc13236")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/servers/b332b5cd-535e-4677-b68e-fc8badc13236")))
                  .image(
                        Resource.builder()
                              .id("416af940-2d3c-4a7c-977c-a9030685ad5e")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/images/416af940-2d3c-4a7c-977c-a9030685ad5e"))).build())
                  .flavor(
                        Resource.builder()
                              .id("1")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/flavors/1"))).build())
                  .id("b332b5cd-535e-4677-b68e-fc8badc13236")
                  .userId("df13814f6c354d00a8acf66502836323")
                  .status(Status.ACTIVE)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:18:58Z"))
                  .hostId("e5bbff80bebacfe1db63951e787b5341427060a602d33abfefb6a1bc")
                  .name("VM blog")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:18:48Z"))
                  .tenantId("8d10e6646d5d4585937395b04839a353")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("active").powerState(1).build())
                  .diskConfig("MANUAL").build(),
            Server.builder()
                  .addresses(ImmutableMultimap.<String, Address>builder()
                        .putAll("Net TenantA Middle-Back", Address.createV4("172.16.12.4")).build())
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://nova:8774/v1.1/8d10e6646d5d4585937395b04839a353/servers/f9d43436-4572-4c9b-9b74-5fa6890a2f21")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/servers/f9d43436-4572-4c9b-9b74-5fa6890a2f21")))
                  .image(
                        Resource.builder()
                              .id("416af940-2d3c-4a7c-977c-a9030685ad5e")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/images/416af940-2d3c-4a7c-977c-a9030685ad5e"))).build())
                  .flavor(
                        Resource.builder()
                              .id("1")
                              .links(
                                    Link.create(
                                          Relation.BOOKMARK,
                                          URI.create("http://nova:8774/8d10e6646d5d4585937395b04839a353/flavors/1"))).build())
                  .id("f9d43436-4572-4c9b-9b74-5fa6890a2f21")
                  .userId("df13814f6c354d00a8acf66502836323")
                  .status(Status.ACTIVE)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:15:09Z"))
                  .hostId("03d796ebb52b1b555e5f6d9262f7dbd52b3f7c181e3aa89b34ca5408")
                  .name("VM MySQL")
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-04-12T11:14:56Z"))
                  .tenantId("8d10e6646d5d4585937395b04839a353")
                  .extendedStatus(ServerExtendedStatus.builder().vmState("active").powerState(1).build())
                  .diskConfig("MANUAL").build());
   }
  

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
