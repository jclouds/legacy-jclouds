/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.nirvanix.sdn.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.addQueryParamTo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.Lists;

@Singleton
public class BindMetadataToQueryParams implements Binder {
   private final Provider<UriBuilder> builder;

   @Inject
   BindMetadataToQueryParams(Provider<UriBuilder> builder) {
      this.builder = builder;
   }

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map,
               "this binder is only valid for Maps!");
      Map<String, String> userMetadata = (Map<String, String>) input;
      List<String> metadata = Lists.newArrayList();
      for (Entry<String, String> entry : userMetadata.entrySet()) {
         metadata.add(String.format("%s:%s", entry.getKey().toLowerCase(), entry.getValue()));
      }
      addQueryParamTo(request, "metadata", metadata, builder.get());
   }
}
