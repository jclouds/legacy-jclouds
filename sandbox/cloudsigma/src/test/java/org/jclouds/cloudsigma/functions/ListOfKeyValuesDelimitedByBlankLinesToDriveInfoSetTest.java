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

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSetTest {

   private static final ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet FN = Guice.createInjector().getInstance(
         ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class);

   public void testNone() {
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newStringPayload(""))), ImmutableSet.<DriveInfo> of());
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newStringPayload("\n\n"))), ImmutableSet.<DriveInfo> of());
      assertEquals(FN.apply(new HttpResponse(200, "", null)), ImmutableSet.<DriveInfo> of());
   }

   public void testOne() {
      assertEquals(FN.apply(new HttpResponse(200, "", Payloads.newInputStreamPayload(MapToDriveInfoTest.class
            .getResourceAsStream("/drive.txt")))), ImmutableSet.<DriveInfo> of(MapToDriveInfoTest.ONE));
   }
}
