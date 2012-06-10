/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0.parse;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneParserModule;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.gson.TypeAdapterFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseApiMetadataTest")
public class ParseRackspaceApiMetadataTest extends BaseItemParserTest<ApiMetadata> {

   @Override
   public String resource() {
      return "/raxVersion.json";
   }

   // http://docs.openstack.org/api/openstack-identity-service/2.0/content/Versions-d1e472.html
   @Override
   @SelectJson("version")
   @Consumes(MediaType.APPLICATION_JSON)
   public ApiMetadata expected() {
      return ApiMetadata.builder().id("v2.0")
            .links(ImmutableSet.of(Link.builder().relation(Link.Relation.SELF).href(URI.create("https://identity.api.rackspacecloud.com/v2.0")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("application/pdf").href(URI.create("http://docs.rackspacecloud.com/auth/api/v2.0/auth-client-devguide-latest.pdf")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("application/vnd.sun.wadl+xml").href(URI.create("http://docs.rackspacecloud.com/auth/api/v2.0/auth.wadl")).build()
            ))
            .status("BETA")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-01-21T11:33:21-06:00"))
            .mediaTypes(ImmutableSet.of(
                  org.jclouds.openstack.keystone.v2_0.domain.MediaType.builder().base("application/xml").type("application/vnd.openstack.identity+xml;version=2.0").build(),
                  org.jclouds.openstack.keystone.v2_0.domain.MediaType.builder().base("application/json").type("application/vnd.openstack.identity+json;version=2.0").build()
            ))
            .build();
   }

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule(), new KeystoneParserModule());
   }
}
