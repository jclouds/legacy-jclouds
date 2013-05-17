/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.parse;

import java.util.Date;

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
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerTest")
public class ParseServerTest extends BaseItemParserTest<Server> {

   @Override
   public String resource() {
      return "/test_get_server_list.json";
   }

   @Override
   @SelectJson("list")
   @OnlyElement
   public Server expected() {
      Option dc = Option.createWithIdNameAndDescription(1l, "US-West-1", "US West 1 Datacenter");
      Option centOs = Option.createWithIdNameAndDescription(13L, "CentOS 5.2 (32-bit)", "CentOS 5.2 (32-bit)");
      Option webServer = Option.createWithIdNameAndDescription(1L, "Web Server", "Web or Application Server");
      return Server.builder().id(75245L).datacenter(dc).isSandbox(false).name("PowerServer").description("server to test the api. created by Alex")
            .state(ServerState.ON).type(webServer).ram(Option.createWithIdNameAndDescription(1L, "512MB", "Server with 512MB RAM"))
            .os(centOs).ip(Ip.builder().id(1313079L).ip("204.51.240.178").subnet("204.51.240.176/255.255.255.240").isPublic(true).state(IpState.ASSIGNED).datacenter(dc).build())
            .image(ServerImage.builder().id(1946L).name("GSI-f8979644-e646-4711-ad58-d98a5fa3612c").friendlyName("BitNami Gallery 2.3.1-0")
                  .description("http://bitnami.org/stack/gallery").os(centOs).type(ServerImageType.WEB_APPLICATION_SERVER)
                  .state(ServerImageState.AVAILABLE).location("24732/GSI-f8979644-e646-4711-ad58-d98a5fa3612c.img").isPublic(true)
                  .isActive(true).createdTime(new Date(1261504577971L)).updatedTime(new Date(1262649582180L)).billingTokens(
                        BillingToken.builder().id(38L).name("CentOS 5.2 32bit").build(),
                        BillingToken.builder().id(56L).name("BitNami: Gallery").build()).owner(
                        Customer.builder().id(24732L).name("BitRock").build()).build()).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new GoGridParserModule(), new GsonModule());
   }
}
