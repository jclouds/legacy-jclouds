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
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseAdminAccessTest")
public class ParseAdminAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/adminAuth.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder().token(
            Token.builder().expires(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-08-01T13:08:52Z"))
                  .id("946b8ad1ede4422f87ab21dcba27896d").tenant(
                  Tenant.builder().id("2fdc88ae152948c690b97ba307acae9b").name("admin").build())
                  .build()).user(
            User.builder().id("b4d134cfe3cf43ad8ba0c2fc5b5d8f91").name("admin").roles(
                  Role.builder().name("admin").build(),
                  Role.builder().name("KeystoneServiceAdmin").build(),
                  Role.builder().name("KeystoneAdmin").build()).build())
            .serviceCatalog(
                  Service.builder().name("Compute Service").type("compute").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:8774/v2/2fdc88ae152948c690b97ba307acae9b"))
                              .internalURL(URI.create("http://10.0.1.13:8774/v2/2fdc88ae152948c690b97ba307acae9b"))
                              .publicURL(URI.create("http://10.0.1.13:8774/v2/2fdc88ae152948c690b97ba307acae9b"))
                              .region("RegionOne").build()).build(),
                  Service.builder().name("S3 Service").type("s3").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:3333"))
                              .internalURL(URI.create("http://10.0.1.13:3333"))
                              .publicURL(URI.create("http://10.0.1.13:3333"))
                              .region("RegionOne").build()).build(),
                  Service.builder().name("Image Service").type("image").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:9292"))
                              .internalURL(URI.create("http://10.0.1.13:9292"))
                              .publicURL(URI.create("http://10.0.1.13:9292"))
                              .region("RegionOne").build()).build(),
                  Service.builder().name("Volume Service").type("volume").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:8776/v1/2fdc88ae152948c690b97ba307acae9b"))
                              .internalURL(URI.create("http://10.0.1.13:8776/v1/2fdc88ae152948c690b97ba307acae9b"))
                              .publicURL(URI.create("http://10.0.1.13:8776/v1/2fdc88ae152948c690b97ba307acae9b"))
                              .region("RegionOne").build()).build(),
                  Service.builder().name("EC2 Service").type("ec2").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:8773/services/Admin"))
                              .internalURL(URI.create("http://10.0.1.13:8773/services/Cloud"))
                              .publicURL(URI.create("http://10.0.1.13:8773/services/Cloud"))
                              .region("RegionOne").build()).build(),
                  Service.builder().name("Identity Service").type("identity").endpoints(
                        Endpoint.builder()
                              .adminURL(URI.create("http://10.0.1.13:35357/v2.0"))
                              .internalURL(URI.create("http://10.0.1.13:5000/v2.0"))
                              .publicURL(URI.create("http://10.0.1.13:5000/v2.0"))
                              .region("RegionOne").build()).build()
            ).build();
   }
}
