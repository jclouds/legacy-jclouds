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
package org.jclouds.rimuhosting.miro.functions;

import static org.jclouds.util.Utils.propagateOrNull;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * On non 2xx we have an error. RimuHosting using the same json base object.
 * 
 * TODO: map exceptions out into something that suits jclouds.
 * 
 * @author Ivan Meredith
 */
@Singleton
public class ParseRimuHostingException implements Function<Exception, Object> {
   private Gson gson;

   @Inject
   public ParseRimuHostingException(Gson gson) {
      this.gson = gson;
   }

   @Override
   public Object apply(Exception e) {
      if (e instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) e;
         if (responseException.getContent() != null) {

            Type setType = new TypeToken<Map<String, RimuHostingResponse>>() {
            }.getType();
            String test = responseException.getContent();
            Map<String, RimuHostingResponse> responseMap = gson.fromJson(test, setType);
            throw new RuntimeException(responseMap.values().iterator().next().getErrorInfo()
                     .getErrorClass());
         }
      }
      return propagateOrNull(e);
   }
}
