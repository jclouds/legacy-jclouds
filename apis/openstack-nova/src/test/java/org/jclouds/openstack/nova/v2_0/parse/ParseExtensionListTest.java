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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.domain.Link;
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
@Test(groups = "unit", testName = "ParseExtensionListTest")
public class ParseExtensionListTest extends BaseSetParserTest<Extension> {

   @Override
   public String resource() {
      return "/extension_list.json";
   }

   @Override
   @SelectJson("extensions")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Extension> expected() {
      return ImmutableSet
            .of(Extension
                  .builder()
                  .alias("RAX-PIE")
                  .name("Public Image Extension")
                  .namespace(URI.create("http://docs.rackspacecloud.com/servers/api/ext/pie/v1.0"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-01-22T13:25:27-06:00"))
                  .description("Adds the capability to share an image with other users.")
                  .links(
                        ImmutableSet.of(
                              Link.create(Relation.DESCRIBEDBY, "application/pdf",
                                    URI.create("http://docs.rackspacecloud.com/servers/api/ext/cs-pie-20111111.pdf")),
                              Link.create(Relation.DESCRIBEDBY, "application/vnd.sun.wadl+xml",
                                    URI.create("http://docs.rackspacecloud.com/servers/api/ext/cs-pie.wadl")))).build(),
                  Extension
                        .builder()
                        .alias("RAX-CBS")
                        .name("Cloud Block Storage")
                        .namespace(URI.create("http://docs.rackspacecloud.com/servers/api/ext/cbs/v1.0"))
                        .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-01-12T11:22:33-06:00"))
                        .description("Allows mounting cloud block storage volumes.")
                        .links(
                              ImmutableSet.of(Link.create(Relation.DESCRIBEDBY, "application/pdf",
                                    URI.create("http://docs.rackspacecloud.com/servers/api/ext/cs-cbs-20111201.pdf")),
                                    Link.create(Relation.DESCRIBEDBY, "application/vnd.sun.wadl+xml",
                                          URI.create("http://docs.rackspacecloud.com/servers/api/ext/cs-cbs.wadl"))))
                        .build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
