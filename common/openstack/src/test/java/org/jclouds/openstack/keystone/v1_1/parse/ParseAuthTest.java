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
package org.jclouds.openstack.keystone.v1_1.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.domain.Endpoint;
import org.jclouds.openstack.keystone.v1_1.domain.Token;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseAuthTest")
public class ParseAuthTest extends BaseItemParserTest<Auth> {

   @Override
   public String resource() {
      return "/auth1_1.json";
   }

   @Override
   @SelectJson("auth")
   @Consumes(MediaType.APPLICATION_JSON)
   public Auth expected() {
      return Auth
            .builder()
            .token(
                  Token.builder()
                        .expires(new SimpleDateFormatDateService().iso8601DateParse("2012-01-30T02:30:54.000-06:00"))
                        .id("118fb907-0786-4799-88f0-9a5b7963d1ab").build())
            .serviceCatalog(
                  ImmutableMultimap.of(
                        "cloudFilesCDN",
                        Endpoint
                              .builder()
                              .region("LON")
                              .publicURL(
                                    URI.create("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953"))
                              .v1Default(true).build(),
                        "cloudFiles",
                        Endpoint
                              .builder()
                              .region("LON")
                              .publicURL(
                                    URI.create("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953"))
                              .v1Default(true)
                              .internalURL(
                                    URI.create("https://snet-storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953"))
                              .build(),
                        "cloudServers",
                        Endpoint.builder()
                              .publicURL(URI.create("https://lon.servers.api.rackspacecloud.com/v1.0/10001786"))
                              .v1Default(true).build())).build();

   }
}
