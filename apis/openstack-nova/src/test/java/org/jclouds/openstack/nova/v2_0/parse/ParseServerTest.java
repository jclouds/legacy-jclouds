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
@Test(groups = "unit", testName = "ParseServerTest")
public class ParseServerTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/server_details.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
            .builder()
            .id("52415800-8b69-11e0-9b19-734f000004d2")
            .tenantId("1234")
            .userId("5678")
            .name("sample-f352")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-10-10T12:00:00Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-08-10T12:00:00Z"))
            .hostId("e4d909c290d0fb1ca068ffaddf22cbd0")
            .accessIPv4("67.23.10.132")
            .accessIPv6("::babe:67.23.10.132")
            .status(Status.BUILD)
            .image(
                  Resource
                        .builder()
                        .id("52415800-8b69-11e0-9b19-734f6f006e54")
                        .name("null")
                        .links(
                              Link.create(
                                    Relation.SELF,
                                    URI.create("http://servers.api.openstack.org/v1.1/1234/images/52415800-8b69-11e0-9b19-734f6f006e54")),
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://servers.api.openstack.org/1234/images/52415800-8b69-11e0-9b19-734f6f006e54")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("52415800-8b69-11e0-9b19-734f216543fd")
                        .name("null")
                        .links(
                              Link.create(
                                    Relation.SELF,
                                    URI.create("http://servers.api.openstack.org/v1.1/1234/flavors/52415800-8b69-11e0-9b19-734f216543fd")),
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://servers.api.openstack.org/1234/flavors/52415800-8b69-11e0-9b19-734f216543fd")))
                        .build())
            .metadata(
                  new ImmutableMap.Builder<String, String>().put("Server Label", "Web Head 1")
                        .put("Image Version", "2.1").build())
            .addresses(ImmutableMultimap.<String, Address>builder()
                  .putAll("public", Address.createV4("67.23.10.132"), Address.createV6("::babe:67.23.10.132"),
                  Address.createV4("67.23.10.131"), Address.createV6("::babe:4317:0A83"))
                  .putAll("private", Address.createV4("10.176.42.16"), Address.createV6("::babe:10.176.42.16"))
                  .build()).build();

   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
