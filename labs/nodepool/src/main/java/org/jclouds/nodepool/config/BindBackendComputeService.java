/*
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
package org.jclouds.nodepool.config;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Provider;
import org.jclouds.nodepool.Backend;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Exposed;
import com.google.inject.Module;
import com.google.inject.Provides;

public class BindBackendComputeService extends BindJcloudsModules {

  
   @Provides
   @Singleton
   @Backend
   protected String provideBackendProvider(@Named(NodePoolProperties.BACKEND_PROVIDER) String provider){
      return provider;
   }
   
   // things wrapped in suppliers are intentional. They can cause network i/o
   // and shouldn't be invoked until after the injector is created.
   
   @Provides
   @Singleton
   @Backend
   @Exposed
   protected Supplier<ComputeService> makeBackendComputeService(@Backend final String provider,
         @Backend final Set<Module> modules, @Provider final Credentials creds,
         @Backend final Supplier<Properties> overrides, final Closer closer) {

      return Suppliers.memoize(new Supplier<ComputeService>() {

         @Override
         public ComputeService get() {
            ComputeServiceContext ctx = ContextBuilder.newBuilder(provider)
                                                      .credentials(creds.identity, creds.credential)
                                                      .overrides(overrides.get())
                                                      .modules(modules)
                                                      .buildView(ComputeServiceContext.class);
            closer.addToClose(ctx);
            return ctx.getComputeService();
         }

      });
   }

   private static final Predicate<String> keys = new Predicate<String>() {

      @Override
      public boolean apply(String input) {
         return !input.startsWith("jclouds.nodepool") && !input.startsWith("nodepool");
      }

   };

   @Provides
   @Singleton
   @Backend
   protected Supplier<Properties> propertiesFor(final FilterStringsBoundToInjectorByName filterStringsBoundByName,
         @Backend final String provider, @Provider final Supplier<URI> endpoint, @ApiVersion final String apiVersion,
         @BuildVersion final String buildVersion) {
      return Suppliers.memoize(new Supplier<Properties>() {

         @Override
         public Properties get() {
            Properties props = new Properties();
            props.putAll(filterStringsBoundByName.apply(keys));
            props.put(provider + ".endpoint", endpoint.get().toASCIIString());
            props.put(provider + ".api-version", apiVersion);
            props.put(provider + ".build-version", buildVersion);
            return props;
         }

      });
   }

   @Provides
   @Singleton
   @Backend
   @Exposed
   protected Supplier<Template> makeBackendTemplate(@Backend Supplier<ComputeService> compute) {
      return Suppliers.memoize(Suppliers2.compose(new Function<ComputeService, Template>() {

         @Override
         public Template apply(ComputeService input) {
            return input.templateBuilder().build();
         }

      }, compute));
   }
}
