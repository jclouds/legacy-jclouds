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
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.Utils;
import org.jclouds.domain.Credentials;
import org.jclouds.internal.BaseWrapper;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class ComputeServiceContextImpl extends BaseWrapper implements ComputeServiceContext {
   private final ComputeService computeService;
   private final Utils utils;

   @Inject
   public ComputeServiceContextImpl(@Provider Context wrapped, @Provider TypeToken<? extends Context> wrappedType,
            ComputeService computeService, Utils utils) {
      super(wrapped, wrappedType);
      this.computeService = checkNotNull(computeService, "computeService");
      this.utils = checkNotNull(utils, "utils");
   }
   
   @Override
   public ComputeService getComputeService() {
      return computeService;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <S, A> RestContext<S, A> getProviderSpecificContext() {
      return (RestContext<S, A>) delegate();
   }

   @Override
   public void close() {
      delegate().close();
   }

   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }

   @Override
   public Map<String, Credentials> credentialStore() {
      return utils().credentialStore();
   }

   @Override
   public Map<String, Credentials> getCredentialStore() {
      return utils().credentialStore();
   }

}
