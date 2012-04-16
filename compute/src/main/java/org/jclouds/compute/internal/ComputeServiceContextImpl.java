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

import java.io.Closeable;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.Utils;
import org.jclouds.domain.Credentials;
import org.jclouds.internal.BaseWrapper;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;

import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class ComputeServiceContextImpl extends BaseWrapper implements ComputeServiceContext {
   private final ComputeService computeService;
   private final Utils utils;
   private final Map<String, Credentials> credentialStore;

   @Inject
   public ComputeServiceContextImpl(@Provider Closeable wrapped, @Provider TypeToken<? extends Closeable> wrappedType,
            ComputeService computeService, Map<String, Credentials> credentialStore, Utils utils) {
      super(wrapped, wrappedType);
      this.credentialStore = credentialStore;
      this.utils = utils;
      this.computeService = checkNotNull(computeService, "computeService");
   }

   public ComputeService getComputeService() {
      return computeService;
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
   public Map<String, Credentials> getCredentialStore() {
      return credentialStore;
   }

   @Override
   public Map<String, Credentials> credentialStore() {
      return credentialStore;
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
