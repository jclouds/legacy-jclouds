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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "ParseKeyPairTest")
public class ParseKeyPairTest extends BaseItemParserTest<KeyPair> {

   @Override
   public String resource() {
      return "/keypair_created.json";
   }

   @Override
   @SelectJson("keypair")
   @Consumes(MediaType.APPLICATION_JSON)
   public KeyPair expected() {
      return KeyPair
            .builder()
            .publicKey(
                  "ssh-rsa AAAXB3NzaC1yc2EAAAADAQABAAAAgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABACE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumADSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQ== nova@nv-aw2az1-api0001\n")
            .privateKey(
                  "-----BEGIN RSA PRIVATE KEY-----\nMIICXQIAAAKBgQDFNyGjgs6c9akgmZ2ou/fJf7Pdrc23hC95/gM/33OrG4GZABAC\nE4DTioa/PGN+7rHv9YUavUCtXrWayhGniKq/wCuI5fo5TO4AmDNv7/sCGHIHFumA\nDSIoLx0vFhGJIetXEWxL9r0lfFC7//6yZM2W3KcGjbMtlPXqBT9K9PzdyQIDAQAB\nAoGAW8Ww+KbpQK8smcgCTr/RqcmsSI8VeL2hXjJvDq0L5WbyYuFdkanDvCztUVZn\nsmyfDtwAqZXB4Ct/dN1tY7m8QpdyRaKRW4Q+hghGCAQpsG7rYDdvwdEyvMaW5RA4\ntucQyajMNyQ/tozU3wMx/v8A7RvGcE9tqoG0WK1C3kBu95UCQQDrOd+joYDkvccz\nFIVu5gNPMXEh3fGGzDxk225UlvESquYLzfz4TfmuUjH4Z1BL3wRiwfJsrrjFkm33\njIidDE8PAkEA1qHjxuaIS1yz/rfzErmcOVNlbFHMP4ihjGTTvh1ZctXlNeLwzENQ\nEDaQV3IpUY1KQR6rxcWb5AXgfF9D9PYFpwJBANucAqGAbRgh3lJgPFtXP4u2O0tF\nLPOOxmvbOdybt6KYD4LB5AXmts77SlACFMNhCXUyYaT6UuOSXDyb5gfJsB0CQQC3\nFaGXKU9Z+doQjhlq/6mjvN/nZl80Uvh7Kgb1RVPoAU1kihGeLE0/h0vZTCiyyDNv\nGRqtucMg32J+tUTi0HpBAkAwHiCZMHMeJWHUwIwlRQY/dnR86FWobRl98ViF2rCL\nDHkDVOeIser3Q6zSqU5/m99lX6an5g8pAh/R5LqnOQZC\n-----END RSA PRIVATE KEY-----\n")
            .name("testkeypair").userId("65649731189278")
            .fingerprint("d2:1f:c9:2b:d8:90:77:5f:15:64:27:e3:9f:77:1d:e4").build();
   }

   protected Injector injector() {
      return Guice.createInjector(new NovaParserModule(), new GsonModule());
   }
}
