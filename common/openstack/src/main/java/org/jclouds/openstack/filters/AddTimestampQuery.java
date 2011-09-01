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
package org.jclouds.openstack.filters;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;

import com.google.common.base.Supplier;

/**
 * Adds a timestamp to the query line so that cache is invalidated.
 * 
 * @author Mike Mayo
 * 
 */
@Singleton
public class AddTimestampQuery implements HttpRequestFilter {
   private final Supplier<Date> dateProvider;
   private final Provider<UriBuilder> builder;

   @Inject
   public AddTimestampQuery(@TimeStamp Supplier<Date> dateProvider, Provider<UriBuilder> builder) {
      this.builder = builder;
      this.dateProvider = dateProvider;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.addQueryParam(request, "now", dateProvider.get().getTime() + "", builder.get());
   }

}