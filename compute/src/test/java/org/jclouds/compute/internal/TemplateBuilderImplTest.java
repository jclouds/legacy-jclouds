/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TemplateBuilderImplTest {

   @Test
   public void testImageIdNullsEverythingElse() {
      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), new LocationImpl(
                        LocationScope.REGION, " id", "description", null), new TemplateOptions());
      template.architecture(Architecture.X86_32);
      template.imageDescriptionMatches("imageDescriptionMatches");
      template.imageNameMatches("imageNameMatches");
      template.imageVersionMatches("imageVersionMatches");
      template.osDescriptionMatches("osDescriptionMatches");
      template.osFamily(OsFamily.CENTOS);

      assertEquals(template.arch, Architecture.X86_32);
      assertEquals(template.imageDescription, "imageDescriptionMatches");
      assertEquals(template.imageName, "imageNameMatches");
      assertEquals(template.imageVersion, "imageVersionMatches");
      assertEquals(template.osDescription, "osDescriptionMatches");
      assertEquals(template.os, OsFamily.CENTOS);
      assertEquals(template.imageId, null);

      template.imageId("myid");
      assertEquals(template.arch, null);
      assertEquals(template.imageDescription, null);
      assertEquals(template.imageName, null);
      assertEquals(template.imageVersion, null);
      assertEquals(template.osDescription, null);
      assertEquals(template.os, null);
      assertEquals(template.imageId, "myid");

   }

}
