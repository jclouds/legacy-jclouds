/**
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
package org.jclouds.compute.strategy.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReturnCredentialsBoundToImageTest")
public class ReturnCredentialsBoundToImageTest {

   public void testDefaultIsToReturnConfiguredCredential() {
      Image image = createMock(Image.class);
      replay(image);

      LoginCredentials creds = new LoginCredentials("ubuntu", "foo", null, false);
      assertEquals(new ReturnCredentialsBoundToImage(creds).execute(image), creds);

      verify(image);

   }

   public void testReturnAdministratorOnWindows() {
      Image image = createMock(Image.class);
      expect(image.getOperatingSystem()).andReturn(
            OperatingSystem.builder().family(OsFamily.WINDOWS).description("foo").build()).atLeastOnce();
      replay(image);

      Credentials creds = new Credentials("Administrator", null);
      assertEquals(new ReturnCredentialsBoundToImage(null).execute(image), creds);

      verify(image);

   }

   public void testReturnRootWhenNotOnWindows() {
      Image image = createMock(Image.class);
      expect(image.getOperatingSystem()).andReturn(
            OperatingSystem.builder().family(OsFamily.LINUX).description("foo").build()).atLeastOnce();
      replay(image);

      Credentials creds = new Credentials("root", null);
      assertEquals(new ReturnCredentialsBoundToImage(null).execute(image), creds);

      verify(image);

   }

}
