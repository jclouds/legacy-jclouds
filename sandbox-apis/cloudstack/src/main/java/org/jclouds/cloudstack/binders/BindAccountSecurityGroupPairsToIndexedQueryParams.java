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

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindAccountSecurityGroupPairsToIndexedQueryParams implements Binder {
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public BindAccountSecurityGroupPairsToIndexedQueryParams(Provider<UriBuilder> uriBuilderProvider) {
      this.uriBuilderProvider = checkNotNull(uriBuilderProvider, "uriBuilderProvider");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof Multimap<?, ?>, "this binder is only valid for Multimaps!");
      Multimap<String, String> pairs = (Multimap<String, String>) checkNotNull(input, "account group pairs");
      checkArgument(pairs.size() > 0, "you must specify at least one account, group pair");
      UriBuilder builder = uriBuilderProvider.get();
      builder.uri(request.getEndpoint());
      Builder<String, String> map = ImmutableMultimap.<String, String> builder().putAll(
            ModifyRequest.parseQueryToMap(request.getEndpoint().getQuery()));
      int i = 0;
      for (Entry<String, String> entry : pairs.entries())
         map.put(String.format("usersecuritygrouplist[%d].account", i), entry.getKey()).put(
               String.format("usersecuritygrouplist[%d].group", i++), entry.getValue());
      builder.replaceQuery(ModifyRequest.makeQueryLine(map.build(), null));
      return (R) request.toBuilder().endpoint(builder.build()).build();
   }
}
