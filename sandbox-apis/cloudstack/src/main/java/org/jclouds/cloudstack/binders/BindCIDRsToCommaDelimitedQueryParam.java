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
package org.jclouds.cloudstack.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindCIDRsToCommaDelimitedQueryParam implements Binder {
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public BindCIDRsToCommaDelimitedQueryParam(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = checkNotNull(uriBuilderProvider, "uriBuilderProvider");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof Iterable<?>, "this binder is only valid for Iterables!");
      Iterable<String> cidrs = (Iterable<String>) checkNotNull(input, "cidr list");
      checkArgument(Iterables.size(cidrs) > 0, "you must specify at least one cidr range");
      return ModifyRequest.addQueryParam(request, "cidrlist", Joiner.on(',').join(cidrs), uriBuilderProvider.get());
   }
}
