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
package org.jclouds.openstack.keystone.v2_0.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Configuration properties and constants used in Keystone connections.
 * 
 * @author Adrian Cole
 */
public class CredentialTypes {

   public static final String API_ACCESS_KEY_CREDENTIALS = "apiAccessKeyCredentials";

   public static final String PASSWORD_CREDENTIALS = "passwordCredentials";

   public static <T> String credentialTypeOf(T input) {
      Class<?> authenticationType = input.getClass();
      checkArgument(authenticationType.isAnnotationPresent(CredentialType.class),
               "programming error: %s should have annotation %s", authenticationType, CredentialType.class.getName());
      return authenticationType.getAnnotation(CredentialType.class).value();
   }

   public static <T> Map<String, T> indexByCredentialType(Iterable<T> iterable) {
      return Maps.uniqueIndex(iterable, new Function<T, String>() {

         @Override
         public String apply(T input) {
            return credentialTypeOf(input);
         }

      });
   }
}
