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
package org.jclouds.openstack.v2_0.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ExtensionToNameSpaceTest")
public class ExtensionToNameSpaceTest {
   private final ExtensionToNameSpace fn = new ExtensionToNameSpace();

   public void testReturnsNamespace() {
      URI ns = URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1");
      assertEquals(fn.apply(Extension.builder().alias("os-keypairs").name("Keypairs").namespace(ns).updated(
               new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00")).description(
               "Keypair Support").build()), ns);
   }

   public void testChangesHttpsToHttp() {
      assertEquals(fn.apply(Extension.builder().alias("security_groups").name("SecurityGroups").namespace(
               URI.create("https://docs.openstack.org/ext/securitygroups/api/v1.1")).updated(
               new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-07-21T00:00:00+00:00")).description(
               "Security group support").build()), URI.create("http://docs.openstack.org/ext/securitygroups/api/v1.1"));
   }
}
