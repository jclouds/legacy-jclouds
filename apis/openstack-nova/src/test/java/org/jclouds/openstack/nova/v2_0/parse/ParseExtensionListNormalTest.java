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
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseExtensionListNormalTest")
public class ParseExtensionListNormalTest extends BaseSetParserTest<Extension> {

   @Override
   public String resource() {
      return "/extension_list_normal.json";
   }

   @Override
   @SelectJson("extensions")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Extension> expected() {
      return ImmutableSet.of(
            Extension.builder().alias("os-keypairs").name("Keypairs")
                  .namespace(URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00"))
                  .description("Keypair Support").build(),
            Extension.builder().alias("os-volumes").name("Volumes")
                  .namespace(URI.create("http://docs.openstack.org/ext/volumes/api/v1.1"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-03-25T00:00:00+00:00"))
                  .description("Volumes support").build(),
            Extension.builder().alias("security_groups").name("SecurityGroups")
                  .namespace(URI.create("http://docs.openstack.org/ext/securitygroups/api/v1.1"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-07-21T00:00:00+00:00"))
                  .description("Security group support").build(),
            Extension.builder().alias("os-floating-ips").name("Floating_ips")
                  .namespace(URI.create("http://docs.openstack.org/ext/floating_ips/api/v1.1"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-06-16T00:00:00+00:00"))
                  .description("Floating IPs support").build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
