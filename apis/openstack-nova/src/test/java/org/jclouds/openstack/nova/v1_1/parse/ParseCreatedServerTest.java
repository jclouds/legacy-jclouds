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
package org.jclouds.openstack.nova.v1_1.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.domain.Link.Relation;
import org.jclouds.openstack.nova.v1_1.config.NovaParserModule;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.domain.Server.Status;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseCreatedServerTest")
public class ParseCreatedServerTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/new_server.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public Server expected() {
      return Server
            .builder()
            .id("71752")
            .uuid("47491020-6a78-4f63-9475-23195ac4515c")
            .tenantId("37936628937291")
            .userId("54297837463082")
            .name("test-e92")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-19T06:21:13Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-03-19T06:21:13Z"))
            .status(Status.BUILD)
            .adminPass("ZWuHcmTMQ7eXoHeM")
            .image(
                  Resource
                        .builder()
                        .id("1241")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/images/1241")))
                        .build())
            .flavor(
                  Resource
                        .builder()
                        .id("100")
                        .links(
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/flavors/100")))
                        .build())
            .links(
                     Link.create(Relation.SELF, URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/37936628937291/servers/71752")),
                     Link.create(Relation.BOOKMARK, URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/37936628937291/servers/71752"))).build();

   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
