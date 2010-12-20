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

package org.jclouds.cloudsigma.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.cloudsigma.domain.ClaimType;
import org.jclouds.cloudsigma.domain.DriveData;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class DriveDataToMapTest {

   private static final DriveDataToMap BASEDRIVE_TO_MAP = Guice.createInjector().getInstance(DriveDataToMap.class);

   public void testBasics() {
      assertEquals(BASEDRIVE_TO_MAP.apply(new DriveData.Builder().name("foo").size(100l).build()),
            ImmutableMap.of("name", "foo", "size", "100"));
   }

   public void testComplete() throws IOException {
      DriveData one = new DriveData.Builder().name("Ubuntu 10.10 Server Edition Linux 64bit Preinstalled System")
      //
            .size(8589934592l)//
            .claimType(ClaimType.SHARED)//
            .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))//
            .use(ImmutableSet.of("tag1", "tag2"))//
            .build();
      assertEquals(
            BASEDRIVE_TO_MAP.apply(one),
            ImmutableMap.builder().put("name", "Ubuntu 10.10 Server Edition Linux 64bit Preinstalled System")
                  .put("size", "8589934592").put("claim:type", "shared")
                  .put("readers", "ffffffff-ffff-ffff-ffff-ffffffffffff").put("use", "tag1 tag2").build()

      );

   }
}