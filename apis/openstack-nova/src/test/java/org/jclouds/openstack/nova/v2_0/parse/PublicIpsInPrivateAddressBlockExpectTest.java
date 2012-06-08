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
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseCreatedServerTest")
public class PublicIpsInPrivateAddressBlockExpectTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/server_public_ip_in_private.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
            .builder()
            .id("59662")
            .hostId("cb0d9e5004bef8c21270a0b09f7624f4c387f3a523f3aaa4c5694a61")
            .uuid("7aed8e05-1daf-476a-87b2-640e8f7dcafd")
            .tenantId("37936628937291")
            .userId("54297837463082")
            .name("hpcloud-computes-38d")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-22T22:11:55Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-22T22:11:19Z"))
            .status(Status.ACTIVE)
            .keyName("jclouds_hpcloud-computes_77")
            .image(
                  Resource
                        .builder()
                        .id("229")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/images/229")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("100")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/flavors/100")))
                        .build())
            .metadata(ImmutableMap.of("Name", "hpcloud-computes"))
            .addresses(ImmutableMultimap.<String, Address>builder()
                  .putAll("private", Address.createV4("10.6.39.189"), Address.createV4("15.185.181.94")).build())
            .links(
                     Link.create(Relation.SELF, URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/servers/59662")),
                     Link.create(Relation.BOOKMARK, URI.create("https://az-2.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/servers/59662"))).build();

   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
