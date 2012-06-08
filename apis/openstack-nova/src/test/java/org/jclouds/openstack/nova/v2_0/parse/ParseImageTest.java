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
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Image.Status;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Jeremy Daggett
 */
@Test(groups = "unit", testName = "ParseImageTest")
public class ParseImageTest extends BaseItemParserTest<Image> {

   @Override
   public String resource() {
      return "/image_details.json";
   }

   @Override
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   public Image expected() {
      return Image
            .builder()
            .id("52415800-8b69-11e0-9b19-734f5736d2a2")
            .name("My Server Backup")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-10-10T12:00:00Z"))
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-08-10T12:00:00Z"))
            .tenantId("12345")
            .userId("joe")
            .status(Status.SAVING)
            .progress(80)
            .minDisk(5)
            .minRam(256)
            .metadata(
                  new ImmutableMap.Builder<String, String>().put("ImageType", "Gold").put("ImageVersion", "1.5")
                        .build())
            .server(
                  Resource
                        .builder()
                        .id("52415800-8b69-11e0-9b19-734f335aa7b3")
                        .name("null")
                        .links(
                              Link.create(
                                    Relation.SELF,
                                    URI.create("http://servers.api.openstack.org/v1.1/1234/servers/52415800-8b69-11e0-9b19-734f335aa7b3")),
                              Link.create(
                                    Relation.BOOKMARK,
                                    URI.create("http://servers.api.openstack.org/1234/servers/52415800-8b69-11e0-9b19-734f335aa7b3")))
                        .build())
            .links(
                  ImmutableSet.of(
                        Link.create(
                              Relation.SELF,
                              URI.create("http://servers.api.openstack.org/v1.1/1234/images/52415800-8b69-11e0-9b19-734f5736d2a2")),
                        Link.create(
                              Relation.BOOKMARK,
                              URI.create("http://servers.api.openstack.org/1234/images/52415800-8b69-11e0-9b19-734f5736d2a2"))))
            .build();
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
