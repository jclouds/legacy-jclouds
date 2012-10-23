/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain;

import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.fail;

import java.util.Properties;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoLiveApiTest;
import org.testng.annotations.Test;

/**
 * Live integration tests for the Abiquo versioning support.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "AbiquoVersionLiveApiTest")
public class AbiquoVersionLiveApiTest extends BaseAbiquoLiveApiTest {
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty("abiquo.api-version", "0.0");
      return overrides;
   }

   public void testUnsupportedVersion() {
      try {
         view.getAdministrationService().getCurrentUser();
         fail("Unsupported versions in mime types should not be allowed");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.NOT_ACCEPTABLE, "406-NOT-ACCEPTABLE");
      }
   }
}
