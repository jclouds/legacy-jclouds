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
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.http.utils.Queries.queryParser;

import java.net.URI;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
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

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof Multimap<?, ?>, "this binder is only valid for Multimaps!");
      Multimap<String, String> pairs = (Multimap<String, String>) checkNotNull(input, "account group pairs");
      checkArgument(pairs.size() > 0, "you must specify at least one account, group pair");

      Multimap<String, String> existingParams = queryParser().apply(request.getEndpoint().getQuery());
      Builder<String, String> map = ImmutableMultimap.<String, String> builder().putAll(existingParams);
      int i = 0;
      for (Entry<String, String> entry : pairs.entries())
         map.put(String.format("usersecuritygrouplist[%d].account", i), entry.getKey()).put(
               String.format("usersecuritygrouplist[%d].group", i++), entry.getValue());
      URI endpoint = uriBuilder(request.getEndpoint()).query(map.build()).build();
      return (R) request.toBuilder().endpoint(endpoint).build();
   }
}
