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
package org.jclouds.rackspace.filters;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * Adds a timestamp to the query line so that cache is invalidated.
 * 
 * @author Mike Mayo
 * 
 */
@Singleton
public class AddTimestampQuery implements HttpRequestFilter {
   private final Provider<Date> dateProvider;

   @Inject
   public AddTimestampQuery(Provider<Date> dateProvider) {
      this.dateProvider = dateProvider;
   }

   public void filter(HttpRequest in) throws HttpException {
      GeneratedHttpRequest<?> request = (GeneratedHttpRequest<?>) in;
      request.addQueryParam("now", dateProvider.get().getTime() + "");
   }

}