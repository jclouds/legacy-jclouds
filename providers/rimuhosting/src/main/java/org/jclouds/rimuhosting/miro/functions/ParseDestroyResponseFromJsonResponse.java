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
package org.jclouds.rimuhosting.miro.functions;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Ivan Meredith
 * @author Adrian Cole
 */
@Singleton
public class ParseDestroyResponseFromJsonResponse implements
      Function<HttpResponse, List<String>> {

   private final ParseJson<Map<String, DestroyResponse>> json;

   @Inject
   ParseDestroyResponseFromJsonResponse(
         ParseJson<Map<String, DestroyResponse>> json) {
      this.json = json;
   }

   @Override
   public List<String> apply(HttpResponse arg0) {
      return Iterables.get(json.apply(arg0).values(), 0).cancel_messages;
   }

   private static class DestroyResponse extends RimuHostingResponse {
      private List<String> cancel_messages;
   }

}
