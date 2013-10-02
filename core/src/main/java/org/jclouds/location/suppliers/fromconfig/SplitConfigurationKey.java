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
package org.jclouds.location.suppliers.fromconfig;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class SplitConfigurationKey implements Supplier<Set<String>> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final ValueOfConfigurationKeyOrNull config;
   protected final String provider;
   protected final String configKey;

   public SplitConfigurationKey(ValueOfConfigurationKeyOrNull config, @Provider String provider,
            @Assisted String configKey) {
      this.config = config;
      this.provider = provider;
      this.configKey = configKey;
   }

   @Override
   public Set<String> get() {
      String regionString = config.apply(configKey);
      if (regionString == null) {
         logger.debug("no %s configured for provider %s", configKey, provider);
         return ImmutableSet.of();
      } else {
         return ImmutableSet.copyOf(Splitter.on(',').split(regionString));
      }
   }

   @Override
   public String toString() {
      return "splitConfigurationKey(" + configKey + ")";
   }

}
