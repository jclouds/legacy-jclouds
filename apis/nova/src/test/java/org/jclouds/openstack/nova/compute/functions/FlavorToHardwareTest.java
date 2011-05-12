/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.openstack.nova.compute.functions;

import com.google.common.collect.ImmutableList;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.functions.ParseFlavorFromJsonResponseTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FlavorToHardwareTest {
   Location provider = new LocationBuilder().scope(LocationScope.ZONE).id("dallas").description("description").build();

   @Test
   public void test() throws UnknownHostException, URISyntaxException {
      Hardware flavor = convertFlavor();
      Hardware tempFlavor = new HardwareBuilder().ids("1").name("256 MB Server")
            .processors(ImmutableList.of(new Processor(1.0, 1.0)))
            .ram(256)
            .volumes(ImmutableList.of(
                  new VolumeBuilder().type(Volume.Type.LOCAL).size(10.0f).durable(true).bootDevice(true).build()))
            .uri(new URI("http://servers.api.openstack.org/1234/flavors/1"))
            .build();
      assertEquals(flavor, tempFlavor);
   }

   public static Hardware convertFlavor() {
      Flavor flavor = ParseFlavorFromJsonResponseTest.parseFlavor();

      FlavorToHardware parser = new FlavorToHardware();

      return parser.apply(flavor);
   }
}
