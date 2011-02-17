/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rest.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.InternetDomainName.fromLenient;
import static com.google.common.net.InternetDomainName.isValidLenient;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.net.InternetDomainName;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindAsHostPrefix implements Binder {

   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public BindAsHostPrefix(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = uriBuilderProvider;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkNotNull(payload, "hostprefix");
      checkArgument(isValidLenient(request.getEndpoint().getHost()), "this is only valid for hostnames: " + request);
      UriBuilder builder = uriBuilderProvider.get().uri(request.getEndpoint());
      InternetDomainName name = fromLenient(request.getEndpoint().getHost()).child(payload.toString());
      builder.host(name.name());
      return (R) request.toBuilder().endpoint(builder.build()).build();
   }
}
