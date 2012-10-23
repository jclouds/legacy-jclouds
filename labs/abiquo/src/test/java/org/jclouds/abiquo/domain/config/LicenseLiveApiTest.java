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

package org.jclouds.abiquo.domain.config;

import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.fail;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link User} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "LicenseLiveApiTest")
public class LicenseLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testCreateRepeated() {
      License repeated = License.Builder.fromLicense(env.license).build();

      try {
         repeated.add();
         fail("Should not be able to create licenses with the same code");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "LICENSE-5");
      }
   }
}
