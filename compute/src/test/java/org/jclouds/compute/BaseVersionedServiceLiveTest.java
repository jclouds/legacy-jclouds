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
package org.jclouds.compute;

import java.util.Properties;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.logging.LoggingModules;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.rest.BaseRestClientLiveTest;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author Jason King
 */
public abstract class BaseVersionedServiceLiveTest extends BaseRestClientLiveTest {

   protected String imageId;
   protected String loginUser;
   protected String authenticateSudo;
   protected LoginCredentials loginCredentials = LoginCredentials.builder().user("root").build();

   @Override
   protected Properties setupProperties() {
      Properties overrides= super.setupProperties();   
      if (imageId != null)
         overrides.setProperty(provider + ".image-id", imageId);
      if (loginUser != null)
         overrides.setProperty(provider + ".image.login-user", loginUser);
      if (authenticateSudo != null)
         overrides.setProperty(provider + ".image.authenticate-sudo", authenticateSudo);
      return overrides;
   }

   @Override
   @BeforeClass
   protected void setupCredentials() {
      super.setupCredentials();

      imageId = System.getProperty("test." + provider + ".image-id");
      loginUser = System.getProperty("test." + provider + ".image.login-user");
      authenticateSudo = System.getProperty("test." + provider + ".image.authenticate-sudo");

      if (loginUser != null){
         Iterable<String> userPass = Splitter.on(':').split(loginUser);
         Builder loginCredentialsBuilder = LoginCredentials.builder();
         loginCredentialsBuilder.user(Iterables.get(userPass, 0));
         if (Iterables.size(userPass) == 2)
            loginCredentialsBuilder.password(Iterables.get(userPass, 1));
         if (authenticateSudo != null)
            loginCredentialsBuilder.authenticateSudo(Boolean.valueOf(authenticateSudo));
         loginCredentials = loginCredentialsBuilder.build();
      }
   }

   protected LoggingModule getLoggingModule() {
      return LoggingModules.firstOrJDKLoggingModule();
   }

}
