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

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.config.AdminAccessConfiguration.Default;
import org.jclouds.compute.functions.Sha512Crypt;
import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;
import org.jclouds.ssh.SshKeys;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(Default.class)
public interface AdminAccessConfiguration extends Configuration {
   @Singleton
   static class Default implements AdminAccessConfiguration {

      private final Supplier<String> defaultAdminUsername = Suppliers.ofInstance(System.getProperty("user.name"));
      private final Supplier<Map<String, String>> defaultAdminSshKeys = new Supplier<Map<String, String>>() {
         public Map<String, String> get() {
            try {
               return ImmutableMap.of("public",
                     Files.toString(new File(System.getProperty("user.home") + "/.ssh/id_rsa.pub"), UTF_8), "private",
                     Files.toString(new File(System.getProperty("user.home") + "/.ssh/id_rsa"), UTF_8));
            } catch (IOException e) {
               return SshKeys.generate();
            }
         }
      };

      /**
       * Cheap, lightweight, low-security password generator.
       * 
       * @see <a href=
       *      "http://www.java-forums.org/java-lang/7355-how-create-lightweight-low-security-password-generator.html" />
       */
      enum PasswordGenerator implements Supplier<String> {

         INSTANCE;

         /** Minimum length for a decent password */
         public static final int MIN_LENGTH = 10;

         /** The random number generator. */
         protected static final SecureRandom r = new SecureRandom();

         /*
          * Set of characters that is valid. Must be printable, memorable, and "won't break HTML" (i.e., not ' <', '>',
          * '&', '=', ...). or break shell commands (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave
          * out, as are numeric zero and one.
          */
         public static final char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q',
               'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N',
               'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-',
               '@', };

         @Override
         public String get() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < MIN_LENGTH; i++) {
               sb.append(goodChar[r.nextInt(goodChar.length)]);
            }
            return sb.toString();
         }
      }

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
         return PasswordGenerator.INSTANCE;
      }

      @Override
      public Function<String, String> cryptFunction() {
         return cryptFunction;
      }
   }
}
