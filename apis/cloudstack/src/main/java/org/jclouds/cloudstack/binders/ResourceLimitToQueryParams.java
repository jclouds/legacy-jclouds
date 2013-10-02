/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.binders;

import static com.google.common.base.Preconditions.checkArgument;

import org.jclouds.cloudstack.domain.ResourceLimit;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * @author Adrian Cole
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/domain_admin/updateResourceLimit.html"
 *      />
 */
public class ResourceLimitToQueryParams implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof ResourceLimit, "this binder is only valid for ResourceLimit");
      ResourceLimit limit = (ResourceLimit) input;
      Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
      builder.put("resourcetype", limit.getResourceType().getCode() + "");
      builder.put("account", limit.getAccount());
      builder.put("domainid", limit.getDomainId());
      builder.put("max", limit.getMax() + "");
      return (R) request.toBuilder().replaceQueryParams(builder.build()).build();
   }
}
