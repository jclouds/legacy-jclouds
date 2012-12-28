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
package org.jclouds.rimuhosting.miro.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponseException;
import org.jclouds.json.Json;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.reflect.TypeToken;

/**
 * On non 2xx we have an error. RimuHosting using the same json base object.
 * 
 * TODO: map exceptions out into something that suits jclouds.
 * 
 * @author Ivan Meredith
 */
@Singleton
public class ParseRimuHostingException implements FutureFallback<Object> {
   private Json json;

   @Inject
   public ParseRimuHostingException(Json json) {
      this.json = json;
   }

   @Override
   public ListenableFuture<Object> create(Throwable t) {
      if (checkNotNull(t, "throwable") instanceof HttpResponseException) {
         HttpResponseException responseException = HttpResponseException.class.cast(t);
         if (responseException.getContent() != null) {
            Type setType = new TypeToken<Map<String, RimuHostingResponse>>() {
            }.getType();
            String test = responseException.getContent();
            Map<String, RimuHostingResponse> responseMap = json.fromJson(test, setType);
            RimuHostingResponse firstResponse = Iterables.get(responseMap.values(), 0);
            String errorClass = firstResponse.getErrorInfo().getErrorClass();
            if (errorClass.equals("PermissionException"))
               throw new AuthorizationException(firstResponse.getErrorInfo().getErrorMessage(), responseException);
            throw new RuntimeException(firstResponse.getErrorInfo().getErrorMessage(), t);
         }
      }
      throw Throwables.propagate(t);
   }
}
