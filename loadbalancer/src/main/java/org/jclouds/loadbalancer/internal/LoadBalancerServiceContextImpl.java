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
package org.jclouds.loadbalancer.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.internal.BaseWrapper;
import org.jclouds.loadbalancer.LoadBalancerService;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;

import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class LoadBalancerServiceContextImpl extends BaseWrapper implements LoadBalancerServiceContext {
   private final LoadBalancerService loadBalancerService;
   private final Utils utils;

   @Inject
   public LoadBalancerServiceContextImpl(@Provider Closeable wrapped,
            @Provider TypeToken<? extends Closeable> wrappedType, LoadBalancerService loadBalancerService, Utils utils) {
      super(wrapped, wrappedType);
      this.utils = utils;
      this.loadBalancerService = checkNotNull(loadBalancerService, "loadBalancerService");
   }

   @Override
   public LoadBalancerService getLoadBalancerService() {
      return loadBalancerService;
   }

   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <S, A> RestContext<S, A> getProviderSpecificContext() {
      return (RestContext<S, A>) getWrapped();
   }

   @Override
   public void close() {
      Closeables.closeQuietly(getWrapped());
   }

   public int hashCode() {
      return getWrapped().hashCode();
   }

   @Override
   public String toString() {
      return getWrapped().toString();
   }

   @Override
   public boolean equals(Object obj) {
      return getWrapped().equals(obj);
   }

}
