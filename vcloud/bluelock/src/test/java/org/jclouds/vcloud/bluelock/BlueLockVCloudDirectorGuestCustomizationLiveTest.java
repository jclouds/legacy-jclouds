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

package org.jclouds.vcloud.bluelock;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.VCloudGuestCustomizationLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "vcloud.BlueLockVCloudDirectorGuestCustomizationLiveTest")
public class BlueLockVCloudDirectorGuestCustomizationLiveTest extends VCloudGuestCustomizationLiveTest {

   @Override
   protected void setupCredentials() {
      provider = "bluelock-vclouddirector";
      identity = checkNotNull(System.getProperty("bluelock-vclouddirector.identity"),
               "bluelock-vclouddirector.identity");
      credential = checkNotNull(System.getProperty("bluelock-vclouddirector.credential"),
               "bluelock-vclouddirector.credential");
   }

}