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
package org.jclouds.vcloud.compute.config;

import java.util.EnumSet;

import org.jclouds.vcloud.domain.Status;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VCloudExpressComputeServiceContextModuleTest {

   @SuppressWarnings("static-access")
   public void testAllStatusCovered() {

      for (Status state : EnumSet.allOf(Status.class).complementOf(
               EnumSet.of(Status.PENDING_DESCRIPTOR, Status.PENDING_CONTENTS, Status.COPYING, Status.QUARANTINED,
                        Status.QUARANTINE_EXPIRED))) {
         assert VCloudExpressComputeServiceContextModule.vAppStatusToNodeState.containsKey(state) : state;
      }

   }
}
