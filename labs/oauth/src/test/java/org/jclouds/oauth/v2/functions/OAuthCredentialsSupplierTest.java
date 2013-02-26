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

package org.jclouds.oauth.v2.functions;

import com.google.common.base.Suppliers;
import org.jclouds.domain.Credentials;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.jclouds.oauth.v2.functions.OAuthCredentialsSupplier.OAuthCredentialsForCredentials;
import static org.testng.Assert.assertNotNull;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class OAuthCredentialsSupplierTest {

   @Test(expectedExceptions = AuthorizationException.class)
   public void testAuthorizationExceptionIsThrownOnBadKeys() {
      OAuthCredentialsSupplier supplier = new OAuthCredentialsSupplier(Suppliers.ofInstance(new Credentials("MOMMA",
              "MIA")), new OAuthCredentialsForCredentials("RS256"), "RS256");
      supplier.get();
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testGSEChildExceptionsPropagateAsAuthorizationException() {
      OAuthCredentialsSupplier supplier = new OAuthCredentialsSupplier(Suppliers.ofInstance(new Credentials("MOMMA",
              "MIA")), new OAuthCredentialsForCredentials("MOMMA"), "MOMMA");
      supplier.get();
   }

   public void testCredentialsAreLoadedOnRightAlgoAndCredentials() {
      Properties propertied = OAuthTestUtils.defaultProperties(new Properties());
      Credentials validCredentials = new Credentials(propertied.getProperty("oauth.identity"),
              propertied.getProperty("oauth.credential"));
      OAuthCredentialsSupplier supplier = new OAuthCredentialsSupplier(Suppliers.ofInstance(validCredentials),
              new OAuthCredentialsForCredentials("RS256"), "RS256");
      assertNotNull(supplier.get());
   }
}
