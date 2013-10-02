/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.strategy.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReturnCredentialsBoundToImageTest")
public class ReturnCredentialsBoundToImageTest {
   LoginCredentials creds = LoginCredentials.builder().user("ubuntu").password("foo").build();

   public void testDefaultIsToReturnConfiguredCredential() {
      Image image = createMock(Image.class);
      replay(image);

      assertEquals(new ReturnCredentialsBoundToImage(creds, ImmutableMap.<String, Credentials> of(), ImmutableMap
               .<OsFamily, LoginCredentials> of()).apply(image), creds);

      verify(image);

   }

   public void testDefaultIsToReturnConfiguredCredentialInStore() {
      Image image = createMock(Image.class);
      expect(image.getId()).andReturn("1").times(2);
      replay(image);

      assertEquals(new ReturnCredentialsBoundToImage(null, ImmutableMap.<String, Credentials> of("image#1", creds),
               ImmutableMap.<OsFamily, LoginCredentials> of()).apply(image), creds);

      verify(image);

   }

   public void testReturnLoginCredentialAssociatedToOsFamily() {
      Image image = createMock(Image.class);
      expect(image.getId()).andReturn("1");
      expect(image.getOperatingSystem()).andReturn(
               OperatingSystem.builder().family(OsFamily.WINDOWS).description("foo").build()).atLeastOnce();
      replay(image);

      Credentials creds = new Credentials("Administrator", null);
      assertEquals(new ReturnCredentialsBoundToImage(null, ImmutableMap.<String, Credentials> of(), ImmutableMap.of(
               OsFamily.WINDOWS, LoginCredentials.builder().user("Administrator").build())).apply(image), creds);

      verify(image);

   }

   public void testReturnRootWhenNotOnWindows() {
      Image image = createMock(Image.class);
      expect(image.getId()).andReturn("1");
      expect(image.getOperatingSystem()).andReturn(
               OperatingSystem.builder().family(OsFamily.LINUX).description("foo").build()).atLeastOnce();
      replay(image);

      Credentials creds = new Credentials("root", null);
      assertEquals(new ReturnCredentialsBoundToImage(null, ImmutableMap.<String, Credentials> of(), ImmutableMap
               .<OsFamily, LoginCredentials> of()).apply(image), creds);

      verify(image);

   }

}
