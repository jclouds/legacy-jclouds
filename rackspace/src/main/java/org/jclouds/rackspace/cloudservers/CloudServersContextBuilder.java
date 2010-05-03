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
package org.jclouds.rackspace.cloudservers;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.cloudservers.compute.config.CloudServersComputeServiceContextModule;
import org.jclouds.rackspace.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.rackspace.config.RackspaceAuthenticationRestModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link CloudServersComputeServiceContext} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see CloudServersComputeServiceContext
 */
public class CloudServersContextBuilder extends
         ComputeServiceContextBuilder<CloudServersAsyncClient, CloudServersClient> {

   public CloudServersContextBuilder(String providerName, Properties props) {
      super(providerName, new TypeLiteral<CloudServersAsyncClient>() {
      }, new TypeLiteral<CloudServersClient>() {
      }, props);
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new RackspaceAuthenticationRestModule());
      modules.add(new CloudServersComputeServiceContextModule(providerName));
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new CloudServersRestClientModule());
   }

}
