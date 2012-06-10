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
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "ParseImageListTest")
public class ParseImageListTest extends BaseSetParserTest<Resource> {

   @Override
   public String resource() {
      return "/image_list.json";
   }

   @Override
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Resource> expected() {
      return ImmutableSet
            .of(Resource
                  .builder()
                  .id("52415800-8b69-11e0-9b19-734f6f006e54")
                  .name("CentOS 5.2")
                  .links(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://servers.api.openstack.org/v1.1/1234/images/52415800-8b69-11e0-9b19-734f6f006e54")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://servers.api.openstack.org/1234/images/52415800-8b69-11e0-9b19-734f6f006e54")))
                  .build(),
                  Resource
                        .builder()
                        .id("52415800-8b69-11e0-9b19-734f5736d2a2")
                        .name("My Server Backup")
                        .links(
                              Link.create(
                                    Relation.SELF,
                                    URI.create("http://servers.api.openstack.org/v1.1/1234/images/52415800-8b69-11e0-9b19-734f5736d2a2")),
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://servers.api.openstack.org/1234/images/52415800-8b69-11e0-9b19-734f5736d2a2")))
                        .build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
