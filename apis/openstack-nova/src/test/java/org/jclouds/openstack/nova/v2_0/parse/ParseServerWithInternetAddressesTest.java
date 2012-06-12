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

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerTest")
public class ParseServerWithInternetAddressesTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/server_details_trystack.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
            .builder()
            .id("1459")
            .uuid("2443c9c7-9791-412e-ac09-a6d55ec25335")
            .tenantId("37")
            .userId("508151008")
            .name("mygroup-72c")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-23T01:30:26Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-23T01:30:26Z"))
            .hostId("881706597197955ac7cc4b353bc7ec884e13fa280de9cc82057796cb")
            .status(Status.ACTIVE)
            .image(
                  Resource
                        .builder()
                        .id("14")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://nova-api.trystack.org:9774/37/images/14")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("1")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://nova-api.trystack.org:9774/37/flavors/1")))
                        .build())
            .links(
                  Link.create(
                         Relation.SELF,
                         URI.create("https://nova-api.trystack.org:9774/v1.1/37/servers/1459")),
                  Link.create(
                         Relation.BOOKMARK,
                         URI.create("https://nova-api.trystack.org:9774/37/servers/1459")))
            .addresses(ImmutableMultimap.of("internet", Address.createV4("8.21.28.47"))).build();
   }
  

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
