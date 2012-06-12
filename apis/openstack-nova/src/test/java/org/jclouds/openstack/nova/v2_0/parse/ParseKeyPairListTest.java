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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ParseKeyPairListTest")
public class ParseKeyPairListTest extends BaseItemParserTest<Set<Map<String, KeyPair>>> {

   @Override
   public String resource() {
      return "/keypair_list.json";
   }

   @Override
   @SelectJson("keypairs")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Map<String, KeyPair>> expected() {
      Map<String, KeyPair> kp1 = new HashMap<String, KeyPair>();
      kp1.put(
            "keypair",
            KeyPair
                  .builder()
                  .publicKey(
                        "ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQCy9EC3O7Ff80vPEfAHDQob61PGwcpYc5KE7tEZnZhrB9n0NyHPRm0E0M+ls3fcTa04HDi+R0DzmRwoyhHQJyI658v8kWZZcuvFjKCcsgsSh/dzdX0xTreLIzSOzt5U7RnZYfshP5cmxtF99yrEY3M/swdin0L+fXsTSkR1B42STQ== nova@nv-aw2az1-api0001")
                  .name("default").fingerprint("ab:0c:f4:f3:54:c0:5d:3f:ed:62:ad:d3:94:7c:79:7c").build());
      Map<String, KeyPair> kp2 = new HashMap<String, KeyPair>();
      kp2.put(
            "keypair",
            KeyPair
                  .builder()
                  .publicKey(
                        "ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001")
                  .name("testkeypair").fingerprint("d2:1f:c9:2b:d8:90:77:5f:15:64:27:e3:9f:77:1d:e4").build());
      return ImmutableSet.of(kp1, kp2);
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }

}
