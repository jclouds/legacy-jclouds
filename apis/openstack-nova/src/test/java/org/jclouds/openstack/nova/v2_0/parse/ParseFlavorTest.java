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

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "ParseFlavorTest")
public class ParseFlavorTest extends BaseItemParserTest<Flavor> {

   @Override
   public String resource() {
      return "/flavor_details.json";
   }

   @Override
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   public Flavor expected() {
      return Flavor
            .builder()
            .id("52415800-8b69-11e0-9b19-734f1195ff37")
            .name("256 MB Server")
            .links(
                  ImmutableSet.of(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://servers.api.openstack.org/v1.1/1234/flavors/52415800-8b69-11e0-9b19-734f1195ff37")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://servers.api.openstack.org/1234/flavors/52415800-8b69-11e0-9b19-734f1195ff37"))))
            .ram(256).disk(10).vcpus(1).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
