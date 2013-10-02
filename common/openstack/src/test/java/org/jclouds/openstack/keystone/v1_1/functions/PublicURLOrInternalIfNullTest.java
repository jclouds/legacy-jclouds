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
package org.jclouds.openstack.keystone.v1_1.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.openstack.keystone.v1_1.domain.Endpoint;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PublicURLOrInternalIfNullTest")
public class PublicURLOrInternalIfNullTest {
   private final PublicURLOrInternalIfNull fn = new PublicURLOrInternalIfNull();

   public void testPublicURLNotNullReturnsPublicURL() {
      assertEquals(fn.apply(
               Endpoint.builder().region("LON").publicURL(
                        URI.create("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953"))
                        .v1Default(true).build()).get(), URI
               .create("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953"));
   }

   public void testPublicURLNullReturnsInternalURL() {
      assertEquals(fn.apply(Endpoint.builder().internalURL(URI.create("https://192.168.1.1")).v1Default(true).build())
               .get(), URI.create("https://192.168.1.1"));
   }

}
