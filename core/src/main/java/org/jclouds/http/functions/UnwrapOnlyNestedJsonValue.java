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

package org.jclouds.http.functions;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class UnwrapOnlyNestedJsonValue<T> implements Function<HttpResponse, T> {

   private final ParseJson<Map<String, Map<String, T>>> json;

   @Inject
   UnwrapOnlyNestedJsonValue(ParseJson<Map<String, Map<String, T>>> json) {
      this.json = json;
   }

   @Override
   public T apply(HttpResponse arg0) {
      Map<String, Map<String, T>> map = json.apply(arg0);
      if (map == null || map.size() == 0)
         return null;
      Map<String, T> map1 = Iterables.getOnlyElement(map.values());
      if (map1 == null || map1.size() == 0)
         return null;
      return Iterables.getOnlyElement(map1.values());
   }
}