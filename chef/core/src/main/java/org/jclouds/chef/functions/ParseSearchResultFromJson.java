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

package org.jclouds.chef.functions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.domain.SearchResult;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseSearchResultFromJson<T> implements Function<HttpResponse, SearchResult<T>> {

   private final ParseJson<Response<T>> json;

   static class Response<T> {
      long start;
      List<T> rows;
   }

   @Inject
   ParseSearchResultFromJson(ParseJson<Response<T>> json) {
      this.json = json;
   }

   @Override
   public SearchResult<T> apply(HttpResponse arg0) {
      Response<T> returnVal = json.apply(arg0);
      return new SearchResult<T>(returnVal.start, returnVal.rows);
   }
}