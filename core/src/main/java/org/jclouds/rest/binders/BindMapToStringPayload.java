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

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payloads;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindMapToStringPayload implements MapBinder {
   protected final Provider<UriBuilder> uriBuilders;

   @Inject
   public BindMapToStringPayload(Provider<UriBuilder> uriBuilders) {
      this.uriBuilders = uriBuilders;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      GeneratedHttpRequest<?> r = GeneratedHttpRequest.class.cast( request);
      UriBuilder builder = uriBuilders.get();
      builder.path(r.getJavaMethod().getAnnotation(Payload.class).value());
      URI fake = builder.buildFromMap(postParams);
      return (R) request.toBuilder().payload(Payloads.newStringPayload(fake.getPath())).build();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      throw new IllegalArgumentException("this is a map binder");
   }

}