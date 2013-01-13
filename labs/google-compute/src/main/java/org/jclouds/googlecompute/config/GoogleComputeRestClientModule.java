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
package org.jclouds.googlecompute.config;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.GoogleComputeAsyncApi;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.features.DiskApi;
import org.jclouds.googlecompute.features.DiskAsyncApi;
import org.jclouds.googlecompute.features.KernelApi;
import org.jclouds.googlecompute.features.KernelAsyncApi;
import org.jclouds.googlecompute.features.MachineTypeApi;
import org.jclouds.googlecompute.features.MachineTypeAsyncApi;
import org.jclouds.googlecompute.features.OperationApi;
import org.jclouds.googlecompute.features.OperationAsyncApi;
import org.jclouds.googlecompute.features.ProjectApi;
import org.jclouds.googlecompute.features.ProjectAsyncApi;
import org.jclouds.googlecompute.features.ZoneApi;
import org.jclouds.googlecompute.features.ZoneAsyncApi;
import org.jclouds.googlecompute.handlers.GoogleComputeErrorHandler;
import org.jclouds.googlecompute.predicates.OperationDonePredicate;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.config.RestClientModule;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkState;

/**
 * Configures the GoogleCompute connection.
 *
 * @author David Alves
 */
@ConfiguresRestClient
public class GoogleComputeRestClientModule extends RestClientModule<GoogleComputeApi, GoogleComputeAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
           .put(DiskApi.class, DiskAsyncApi.class)
           .put(KernelApi.class, KernelAsyncApi.class)
           .put(MachineTypeApi.class, MachineTypeAsyncApi.class)
           .put(OperationApi.class, OperationAsyncApi.class)
           .put(ProjectApi.class, ProjectAsyncApi.class)
           .put(ZoneApi.class, ZoneAsyncApi.class)
           .build();

   public GoogleComputeRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {}).to(OperationDonePredicate.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GoogleComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GoogleComputeErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GoogleComputeErrorHandler.class);
   }

   @Provides
   @UserProject
   public String provideProject(@Identity String identity) {
      checkState(identity.indexOf("@") != 1, "identity should be in project_id@developer.gserviceaccount.com format");
      return Iterables.get(Splitter.on("@").split(identity), 0);
   }

}
