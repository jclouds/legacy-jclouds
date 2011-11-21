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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.cloudstack.domain.ResourceLimit;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

/**
 * @author Adrian Cole
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/domain_admin/updateResourceLimit.html"
 *      />
 */
public class ResourceLimitToQueryParams implements Binder {
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public ResourceLimitToQueryParams(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = checkNotNull(uriBuilderProvider, "uriBuilderProvider");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof ResourceLimit, "this binder is only valid for ResourceLimit");
      ResourceLimit limit = (ResourceLimit) input;
      request = ModifyRequest.addQueryParam(request, "resourcetype", limit.getResourceType().getCode(),
            uriBuilderProvider.get());
      request = ModifyRequest.addQueryParam(request, "account", limit.getAccount(), uriBuilderProvider.get());
      request = ModifyRequest.addQueryParam(request, "domainid", limit.getDomainId(), uriBuilderProvider.get());
      request = ModifyRequest.addQueryParam(request, "max", limit.getMax(), uriBuilderProvider.get());
      return request;
   }
}
