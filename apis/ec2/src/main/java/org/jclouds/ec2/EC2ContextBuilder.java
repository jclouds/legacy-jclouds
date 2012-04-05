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
package org.jclouds.ec2;

import java.util.List;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.ec2.compute.EC2ComputeServiceContext;
import org.jclouds.ec2.compute.config.EC2ComputeServiceContextModule;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link EC2ComputeServiceContext} or {@link Injector} instances based
 * on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or
 * Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default
 * {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be
 * installed.
 * 
 * @author Adrian Cole
 * @see EC2ComputeServiceContext
 */
public class EC2ContextBuilder<S extends EC2Client, A extends EC2AsyncClient, C extends EC2ComputeServiceContext<S, A>, M extends EC2ApiMetadata<S, A, C, M>>
      extends ComputeServiceContextBuilder<S, A, C, M> {

   public EC2ContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      super(providerMetadata);
   }

   public EC2ContextBuilder(M apiMetadata) {
      super(apiMetadata);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new EC2ComputeServiceContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new EC2RestClientModule<S, A>(apiMetadata.getApi(), apiMetadata.getAsyncApi(),
            EC2RestClientModule.DELEGATE_MAP));
   }

   @Override
   protected void addImageResolutionModule() {
      modules.add(new EC2ResolveImagesModule());
   }
}
