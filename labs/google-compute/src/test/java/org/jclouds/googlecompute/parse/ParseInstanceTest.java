/*
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

package org.jclouds.googlecompute.parse;

import com.google.common.collect.ImmutableMap;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
public class ParseInstanceTest extends BaseGoogleComputeParseTest<Instance> {

   @Override
   public String resource() {
      return "/instance_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Instance expected() {
      return Instance.builder()
              .id("13051190678907570425")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T23:48:20.758"))
              .selfLink(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/myproject/instances/test-instance"))
              .description("desc")
              .name("test-instance")
              .image(URI.create("https://www.googleapis" +
                      ".com/compute/v1beta13/projects/google/images/ubuntu-12-04-v20120912"))
              .machineType(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject/machineTypes/n1" +
                      "-standard-1"))
              .status(Instance.Status.RUNNING)
              .zone(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject/zones/us-central1-a"))
              .addNetworkInterface(
                      Instance.NetworkInterface.builder()
                              .name("nic0")
                              .networkIP("10.240.121.115")
                              .network(URI.create("https://www.googleapis" +
                                      ".com/compute/v1beta13/projects/myproject/networks/default"))
                              .build()
              )
              .addDisk(
                      Instance.PersistentAttachedDisk.builder()
                              .index(0)
                              .mode(Instance.PersistentAttachedDisk.Mode.READ_WRITE)
                              .deviceName("test")
                              .source(URI.create("https://www.googleapis" +
                                      ".com/compute/v1beta13/projects/myproject/disks/test"))
                              .build()
              )
              .addTag("aTag")
              .metadata(ImmutableMap.of("aKey", "aValue"))
              .addServiceAccount(Instance.ServiceAccount.builder().email("default").addScopes("myscope").build())
              .build();
   }
}
