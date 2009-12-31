/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.config.VCloudRestClientModule;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link RestContext} for {@link VCloudAsyncClient} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see RestContext
 * @see VCloudAsyncClient
 */
public class VCloudContextBuilder extends RestContextBuilder<VCloudAsyncClient, VCloudClient> {

   public VCloudContextBuilder(Properties props) {
      super(new TypeLiteral<VCloudAsyncClient>() {
      }, new TypeLiteral<VCloudClient>() {
      }, props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new VCloudRestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new VCloudContextModule());
   }

   @Override
   public VCloudContextBuilder withExecutorService(ExecutorService service) {
      return (VCloudContextBuilder) super.withExecutorService(service);
   }

   @Override
   public VCloudContextBuilder withModules(Module... modules) {
      return (VCloudContextBuilder) super.withModules(modules);
   }

}
