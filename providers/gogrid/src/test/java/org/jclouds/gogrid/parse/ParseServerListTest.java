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
package org.jclouds.gogrid.parse;

import java.util.Date;
import java.util.Set;

import org.jclouds.gogrid.config.GoGridParserModule;
import org.jclouds.gogrid.domain.BillingToken;
import org.jclouds.gogrid.domain.Customer;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.domain.ServerState;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerListTest")
public class ParseServerListTest extends BaseSetParserTest<Server> {

   @Override
   public String resource() {
      return "/test_get_server_list.json";
   }

   @Override
   @SelectJson("list")
   public Set<Server> expected() {
      Option dc = new Option(1l, "US-West-1", "US West 1 Datacenter");
      Option centOs = new Option(13L, "CentOS 5.2 (32-bit)", "CentOS 5.2 (32-bit)");
      Option webServer = new Option(1L, "Web Server", "Web or Application Server");
      return ImmutableSet.of(new Server(75245L, dc, false, "PowerServer", "server to test the api. created by Alex",
               ServerState.ON, webServer, new Option(1L, "512MB", "Server with 512MB RAM"), centOs, new Ip(1313079L,
                        "204.51.240.178", "204.51.240.176/255.255.255.240", true, IpState.ASSIGNED, dc),
               new ServerImage(1946L, "GSI-f8979644-e646-4711-ad58-d98a5fa3612c", "BitNami Gallery 2.3.1-0",
                        "http://bitnami.org/stack/gallery", centOs, null, ServerImageType.WEB_APPLICATION_SERVER,
                        ServerImageState.AVAILABLE, 0.0, "24732/GSI-f8979644-e646-4711-ad58-d98a5fa3612c.img", true,
                        true, new Date(1261504577971L), new Date(1262649582180L), ImmutableSortedSet.of(
                                 new BillingToken(38L, "CentOS 5.2 32bit", 0.0), new BillingToken(56L,
                                          "BitNami: Gallery", 0.0)), new Customer(24732L, "BitRock"))));

   }

   protected Injector injector() {
      return Guice.createInjector(new GoGridParserModule(), new GsonModule());
   }

}
