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
package org.jclouds.cloudstack.binders;

import org.jclouds.cloudstack.domain.SnapshotPolicySchedule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindSnapshotPolicyScheduleToQueryParam implements Binder {
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public BindSnapshotPolicyScheduleToQueryParam(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = checkNotNull(uriBuilderProvider, "uriBuilderProvider");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof SnapshotPolicySchedule, "this binder is only valid for SnapshotPolicySchedule");
      SnapshotPolicySchedule schedule = (SnapshotPolicySchedule) input;
      R modifiedResult = ModifyRequest.addQueryParam(request, "intervaltype", schedule.getInterval(), uriBuilderProvider.get());
      modifiedResult = ModifyRequest.addQueryParam(modifiedResult, "schedule", schedule.getTime(), uriBuilderProvider.get());
      return modifiedResult;
   }
}
