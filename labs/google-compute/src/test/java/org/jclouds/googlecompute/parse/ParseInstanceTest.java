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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceAttachedDisk;
import org.jclouds.googlecompute.domain.InstanceNetworkInterface;
import org.jclouds.json.BaseItemParserTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author David Alves
 */
public class ParseInstanceTest extends BaseItemParserTest<Instance> {

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
              .selfLink("https://www.googleapis.com/compute/v1beta13/projects/myproject/instances/test-instance")
              .name("test-instance")
              .image("https://www.googleapis.com/compute/v1beta13/projects/google/images/ubuntu-12-04-v20120912")
              .machineType("https://www.googleapis.com/compute/v1beta13/projects/myproject/machineTypes/n1-standard-1")
              .status(Instance.Status.RUNNING)
              .zone("https://www.googleapis.com/compute/v1beta13/projects/myproject/zones/us-central1-a")
              .addNetworkInterface(
                      InstanceNetworkInterface.builder()
                              .name("nic0")
                              .network("https://www.googleapis" +
                                      ".com/compute/v1beta13/projects/myproject/networks/default")
                              .networkIP("10.240.95.85")
                              .build()
              )
              .addDisk(
                      InstanceAttachedDisk.builder()
                              .type(InstanceAttachedDisk.Type.EPHEMERAL)
                              .mode(InstanceAttachedDisk.Mode.READ_WRITE)
                              .index(0)
                              .build()
              )
              .build();
   }
}
