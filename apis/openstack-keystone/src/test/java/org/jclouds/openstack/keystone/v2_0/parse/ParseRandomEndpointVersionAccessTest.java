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
package org.jclouds.openstack.keystone.v2_0.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseRandomEndpointVersionAccessTest")
public class ParseRandomEndpointVersionAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/access_version_uids.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder()
                   .token(Token.builder()
                               .expires(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-09-29T19:53:45Z"))
                               .id("b267e2e240624b108b1ed5bba6e5882e")
                               .tenant(Tenant.builder()
                                              //  "enabled": true,
                                             .id("82d8d2f865484776a1daf1e2245d3317")
                                             .name("demo").build()).build())
                    .service(Service.builder().type("compute").name("nova")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:8774/v2/82d8d2f865484776a1daf1e2245d3317")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:8774/v2/82d8d2f865484776a1daf1e2245d3317")
                                                      .id("bb3ce9ccdc5045909882688b90cc3ff0")
                                                      .publicURL("http://10.10.10.10:8774/v2/82d8d2f865484776a1daf1e2245d3317").build()).build())
                    .service(Service.builder().type("s3").name("s3")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:3333")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:3333")
                                                      .id("9646263f31ea4f499732c5e1370ecf5e")
                                                      .publicURL("http://10.10.10.10:3333").build()).build())
                    .service(Service.builder().type("image").name("glance")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:9292")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:9292")
                                                      .id("aa5d0b2574824ba097dc07faacf3be65")
                                                      .publicURL("http://10.10.10.10:9292").build()).build())
                    .service(Service.builder().type("volume").name("cinder")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:8776/v1/82d8d2f865484776a1daf1e2245d3317")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:8776/v1/82d8d2f865484776a1daf1e2245d3317")
                                                      .id("7679065b1405447eb5f1a38a6b99ccc0")
                                                      .publicURL("http://10.10.10.10:8776/v1/82d8d2f865484776a1daf1e2245d3317").build()).build())
                    .service(Service.builder().type("ec2").name("ec2")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:8773/services/Admin")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:8773/services/Cloud")
                                                      .id("22b007f023fb4c42be094916eb2bf18b")
                                                      .publicURL("http://10.10.10.10:8773/services/Cloud").build()).build())
                    .service(Service.builder().type("identity").name("keystone")
                                    .endpoint(Endpoint.builder()
                                                      .adminURL("http://10.10.10.10:35357/v2.0")
                                                      .region("RegionOne")
                                                      .internalURL("http://10.10.10.10:5000/v2.0")
                                                      .id("57ee5fb4f9a840f3b965909681d0fc53")
                                                      .publicURL("http://10.10.10.10:5000/v2.0").build()).build())
                   .user(User.builder()
                             .id("ca248caf55844c14a4876c22112bbbb9")
                             .name("demo")
//                             .username("demo")
                             .role(Role.builder().name("Member").build()).build()).build();
//                   "metadata": {
//                       "is_admin": 0,
//                       "roles": ["1f697d8e3ace4f5a80f7701e554ee5d9"]
//                   }

   }
}
