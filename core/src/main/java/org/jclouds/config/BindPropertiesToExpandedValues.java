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
package org.jclouds.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.base.Predicates;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * expands properties.
 * 
 * @author Adrian Cole
 */
public class BindPropertiesToExpandedValues extends AbstractModule {
   private final Properties resolved;

   public BindPropertiesToExpandedValues(Properties resolved) {
      this.resolved = checkNotNull(resolved, "resolved");
   }

   @Override
   protected void configure() {
      install(Rocoto.expandVariables(new ConfigurationModule() {

         @Override
         protected void bindConfigurations() {
            bindProperties(resolved);
         }

         @Provides
         @Singleton
         protected Properties expanded(FilterStringsBoundToInjectorByName filterStringsBoundByName) {
            Properties props = new Properties();
            props.putAll(filterStringsBoundByName.apply(Predicates.<String> alwaysTrue()));
            return props;
         }
      }));
   }

}
