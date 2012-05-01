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
package org.jclouds.openstack.keystone.v2_0.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseApiMetadataTest")
public class ParseApiMetadataTest extends BaseItemParserTest<ApiMetadata> {

   @Override
   public String resource() {
      return "/apiMetadataResponse.json";
   }

   @Override
   @SelectJson("version")
   @Consumes(MediaType.APPLICATION_JSON)
   public ApiMetadata expected() {
      return ApiMetadata.builder().id("v2.0")
            .links(ImmutableSet.of(Link.builder().relation(Link.Relation.SELF).href(URI.create("http://172.16.89.140:5000/v2.0/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("text/html").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/content/")).build(),
                  Link.builder().relation(Link.Relation.DESCRIBEDBY).type("application/pdf").href(URI.create("http://docs.openstack.org/api/openstack-identity-service/2.0/identity-dev-guide-2.0.pdf")).build()
            ))
            .status("beta")
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-11-19T00:00:00Z"))
            .mediaTypes(ImmutableSet.of(
                  org.jclouds.openstack.keystone.v2_0.domain.MediaType.builder().base("application/json").type("application/vnd.openstack.identity-v2.0+json").build(),
                  org.jclouds.openstack.keystone.v2_0.domain.MediaType.builder().base("application/xml").type("application/vnd.openstack.identity-v2.0+xml").build()
            ))
            .build();
   }
}
