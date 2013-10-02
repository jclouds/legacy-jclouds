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
package org.jclouds.scriptbuilder.statements.login;

import java.util.Map;

import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public enum TestConfiguration implements Configuration {
   INSTANCE;
   int pwCount = 0;

   public TestConfiguration reset() {
      pwCount = 0;
      return this;
   }

   private final Supplier<String> defaultAdminUsername = Suppliers.ofInstance("defaultAdminUsername");
   private final Supplier<Map<String, String>> defaultAdminSshKeys = Suppliers
         .<Map<String, String>> ofInstance(ImmutableMap.of("public", "publicKey", "private", "privateKey"));
   private final Supplier<String> passwordGenerator = new Supplier<String>() {

      @Override
      public String get() {
         return pwCount++ + "";
      }

   };

   private final Function<String, String> cryptFunction = new Function<String, String>() {

      @Override
      public String apply(String input) {
         return String.format("crypt(%s)", input);
      }

   };

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
