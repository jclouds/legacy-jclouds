/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.opscodeplatform;

import java.util.List;
import java.util.Properties;

import org.jclouds.ohai.OhaiContextBuilder;
import org.jclouds.opscodeplatform.functions.OpscodePlatformRestClientModule;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class OpscodePlatformContextBuilder extends OhaiContextBuilder<OpscodePlatformClient, OpscodePlatformAsyncClient> {

   public OpscodePlatformContextBuilder(Properties props) {
      super(OpscodePlatformClient.class, OpscodePlatformAsyncClient.class, props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new OpscodePlatformRestClientModule());
   }

   @Override
   public Injector buildInjector() {
      addOhaiModuleIfNotPresent();
      return super.buildInjector();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OpscodePlatformContextBuilder withModules(Iterable<Module> modules) {
      return (OpscodePlatformContextBuilder) super.withModules(modules);
   }

   @SuppressWarnings("unchecked")
   @Override
   public OpscodePlatformContext buildContext() {
      Injector injector = buildInjector();
      return injector.getInstance(OpscodePlatformContext.class);
   }
}