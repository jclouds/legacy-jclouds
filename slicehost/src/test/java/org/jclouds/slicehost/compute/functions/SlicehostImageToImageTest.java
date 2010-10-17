/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.slicehost.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.slicehost.xml.ImageHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.SlicehostImageToImageTest")
public class SlicehostImageToImageTest {
   Location provider = new LocationImpl(LocationScope.ZONE, "dallas", "description", null);

   @Test
   public void testApplyWhereImageNotFound() throws UnknownHostException {
      assertEquals(
            convertImage(),
            new ImageBuilder()
                  .name("CentOS 5.2")
                  .operatingSystem(
                        new OperatingSystemBuilder().family(OsFamily.CENTOS).description("CentOS 5.2").is64Bit(true)
                              .build()).description("CentOS 5.2").defaultCredentials(new Credentials("root", null))
                  .ids("2").build());
   }

   public static Image convertImage() {
      org.jclouds.slicehost.domain.Image image = ImageHandlerTest.parseImage();

      SlicehostImageToImage parser = new SlicehostImageToImage(new SlicehostImageToOperatingSystem());

      return parser.apply(image);
   }
}
