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
package org.jclouds.scriptbuilder.statements.login;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.crypto.Sha512Crypt;
import org.jclouds.crypto.SshKeys;
import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;
import org.jclouds.util.PasswordGenerator;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class DefaultConfiguration implements Configuration {

   private final Supplier<String> defaultAdminUsername = Suppliers.ofInstance(System.getProperty("user.name"));
   private final Supplier<Map<String, String>> defaultAdminSshKeys = new Supplier<Map<String, String>>() {

      @Override
      public Map<String, String> get() {
         try {
            return ImmutableMap.of(
                  "public", Files.toString(new File(System.getProperty("user.home") + "/.ssh/id_rsa.pub"), UTF_8), 
                  "private", Files.toString(new File(System.getProperty("user.home") + "/.ssh/id_rsa"), UTF_8));
         } catch (IOException e) {
            return SshKeys.generate();
         }
      }
   };
   private final Supplier<String> passwordGenerator = PasswordGenerator.INSTANCE;
   private final Function<String, String> cryptFunction = Sha512Crypt.function();

   @Override
   public Supplier<String> defaultAdminUsername() {
      return defaultAdminUsername;
   }

   @Override
   public Supplier<Map<String, String>> defaultAdminSshKeys() {
      return defaultAdminSshKeys;
   }

   @Override
   public Supplier<String> passwordGenerator() {
      return passwordGenerator;
   }

   @Override
   public Function<String, String> cryptFunction() {
      return cryptFunction;
   }
}
