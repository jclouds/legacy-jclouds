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
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerListTest")
public class ParseServerListTest extends BaseSetParserTest<Resource> {

   @Override
   public String resource() {
      return "/server_list.json";
   }

   @Override
   @SelectJson("servers")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Resource> expected() {
      return ImmutableSet
            .of(Resource
                  .builder()
                  .id("52415800-8b69-11e0-9b19-734f6af67565")
                  .name("sample-server")
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://servers.api.openstack.org/v1.1/1234/servers/52415800-8b69-11e0-9b19-734f6af67565")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://servers.api.openstack.org/1234/servers/52415800-8b69-11e0-9b19-734f6af67565")))
                  .build(),
                  Resource
                        .builder()
                        .id("52415800-8b69-11e0-9b19-734f1f1350e5")
                        .name("sample-server2")
                        .links(
                              Link.create(
                                    Relation.SELF,
                                    URI.create("http://servers.api.openstack.org/v1.1/1234/servers/52415800-8b69-11e0-9b19-734f1f1350e5")),
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://servers.api.openstack.org/1234/servers/52415800-8b69-11e0-9b19-734f1f1350e5")))
                        .build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
