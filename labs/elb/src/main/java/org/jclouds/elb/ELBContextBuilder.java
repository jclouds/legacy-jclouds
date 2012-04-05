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
package org.jclouds.elb;

import java.util.List;

import org.jclouds.elb.config.ELBRestClientModule;
import org.jclouds.elb.loadbalancer.config.ELBLoadBalancerContextModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.LoadBalancerServiceContextBuilder;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link ELBContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see ELBContext
 */
public class ELBContextBuilder<S extends ELBClient, A extends ELBAsyncClient> extends
      LoadBalancerServiceContextBuilder<S, A, LoadBalancerServiceContext<S, A>, ELBApiMetadata<S, A>> {

   public ELBContextBuilder(ProviderMetadata<S, A, LoadBalancerServiceContext<S, A>, ELBApiMetadata<S, A>> providerMetadata) {
      super(providerMetadata);
   }

   public ELBContextBuilder(ELBApiMetadata<S, A> apiMetadata) {
      super(apiMetadata);
   }
   
   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new ELBLoadBalancerContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new ELBRestClientModule());
   }
}
