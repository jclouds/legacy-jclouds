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
package org.jclouds.loadbalancer;

import java.util.Properties;

import org.jclouds.loadbalancer.internal.LoadBalancerServiceContextImpl;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Key;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public abstract class LoadBalancerServiceContextBuilder<S, A> extends RestContextBuilder<S, A> {

   public LoadBalancerServiceContextBuilder(Class<S> syncClientType, Class<A> asyncClientType) {
      this(syncClientType, asyncClientType, new Properties());
   }

   public LoadBalancerServiceContextBuilder(Class<S> syncClientType, Class<A> asyncClientType,
            Properties properties) {
      super(syncClientType, asyncClientType, properties);

   }

   public LoadBalancerServiceContext buildLoadBalancerServiceContext() {
      // need the generic type information
      return (LoadBalancerServiceContext) buildInjector().getInstance(
               Key.get(Types.newParameterizedType(LoadBalancerServiceContextImpl.class, syncClientType,
                        asyncClientType)));
   }
}