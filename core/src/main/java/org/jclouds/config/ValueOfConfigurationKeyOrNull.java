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
package org.jclouds.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.named;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ValueOfConfigurationKeyOrNull implements Function<String, String> {
   protected final Injector injector;

   @Inject
   public ValueOfConfigurationKeyOrNull(Injector injector) {
      this.injector = checkNotNull(injector, "injector");
   }

   @Override
   public String apply(String configurationKey) {
      checkNotNull(configurationKey, "configurationKey");
      try {
         return injector.getInstance(Key.get(String.class, named(configurationKey)));
      } catch (ConfigurationException e) {
         return null;
      }
   }

}
