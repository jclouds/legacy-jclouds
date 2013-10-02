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
package org.jclouds.compute.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNullTest")
public class GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNullTest {

   public void testWhenCredentialsNotPresentReturnsNull() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = null;

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn(null);
      expect(config.apply("jclouds.image.login-user")).andReturn(null);
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndProviderPropertyHasUser() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = new Credentials("ubuntu", null);

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn("ubuntu");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn(null);
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndJcloudsPropertyHasUser() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = new Credentials("ubuntu", null);

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn(null);
      expect(config.apply("jclouds.image.login-user")).andReturn("ubuntu");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn(null);
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsAlreadyPresentReturnsSame() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = new Credentials("root", null);

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(true);
      expect(credstore.get("image")).andReturn(expected);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndProviderPropertyHasUserAndPassword() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = new Credentials("ubuntu", "password");

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn("ubuntu:password");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn(null);
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndJcloudsPropertyHasUserAndPassword() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      Credentials expected = new Credentials("ubuntu", "password");

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn(null);
      expect(config.apply("jclouds.image.login-user")).andReturn("ubuntu:password");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn(null);
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndJcloudsPropertyHasUserAndPasswordAndSudo() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      LoginCredentials expected = LoginCredentials.builder().user("ubuntu").password("password").authenticateSudo(true)
            .build();

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn(null);
      expect(config.apply("jclouds.image.login-user")).andReturn("ubuntu:password");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn(null);
      expect(config.apply("jclouds.image.authenticate-sudo")).andReturn("true");
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

   public void testWhenCredentialsNotPresentAndProviderPropertyHasUserAndPasswordAndSudo() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      LoginCredentials expected = LoginCredentials.builder().user("ubuntu").password("password").authenticateSudo(true)
            .build();

      ValueOfConfigurationKeyOrNull config = createMock(ValueOfConfigurationKeyOrNull.class);

      expect(credstore.containsKey("image")).andReturn(false);
      expect(config.apply("provider.image.login-user")).andReturn("ubuntu:password");
      expect(config.apply("provider.image.authenticate-sudo")).andReturn("true");
      expect(credstore.put("image", expected)).andReturn(null);

      replay(config);
      replay(credstore);

      GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull fn = new GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull(
            "provider", config, credstore);
      assertEquals(fn.get(), expected);

      verify(config);
      verify(credstore);

   }

}
