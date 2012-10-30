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
package org.jclouds.cloudstack.filters;

import static com.google.common.collect.Iterables.getOnlyElement;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.Queries;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * By default, jclouds controls encoding based on rules which are different
 *
 * @author Adrian Cole
 */
public class ReEncodeQueryWithDefaultURLEncoder implements HttpRequestFilter {
   private final Provider<UriBuilder> builders;

   @Inject
   public ReEncodeQueryWithDefaultURLEncoder(Provider<UriBuilder> builders) {
      this.builders = builders;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      UriBuilder builder = builders.get();
      builder.uri(request.getEndpoint());
      Multimap<String, String> map = Queries.parseQueryToMap(request.getEndpoint().getRawQuery());
      builder.replaceQuery("");
      for (String key : map.keySet())
         builder.queryParam(key, getOnlyElement(map.get(key)));
      return request.toBuilder().endpoint(builder.build()).build();
   }

}
