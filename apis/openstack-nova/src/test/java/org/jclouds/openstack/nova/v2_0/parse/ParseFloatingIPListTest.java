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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ParseFloatingIPListTest")
public class ParseFloatingIPListTest extends BaseSetParserTest<FloatingIP> {

   @Override
   public String resource() {
      return "/floatingip_list.json";
   }

   @Override
   @SelectJson("floating_ips")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<FloatingIP> expected() {
      return ImmutableSet.of(FloatingIP.builder().id("1").instanceId("12").ip("10.0.0.3").fixedIp("11.0.0.1").build(),
            FloatingIP.builder().id("2").instanceId(null).ip("10.0.0.5").fixedIp(null).build());
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
