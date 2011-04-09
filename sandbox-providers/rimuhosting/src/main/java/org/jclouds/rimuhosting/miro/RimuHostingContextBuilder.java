/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rimuhosting.miro.compute.config.RimuHostingComputeServiceContextModule;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link RimuHostingComputeServiceContext} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see RimuHostingComputeServiceContext
 */
public class RimuHostingContextBuilder extends
         ComputeServiceContextBuilder<RimuHostingClient, RimuHostingAsyncClient> {

   public RimuHostingContextBuilder(Properties props) {
      super(RimuHostingClient.class, RimuHostingAsyncClient.class, props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new RimuHostingComputeServiceContextModule());
   }

   @Override
   public ComputeServiceContext buildComputeServiceContext() {
      // need the generic type information
      return (ComputeServiceContext) this
               .buildInjector()
               .getInstance(
                        Key
                                 .get(new TypeLiteral<ComputeServiceContextImpl<RimuHostingClient, RimuHostingAsyncClient>>() {
                                 }));
   }
}
