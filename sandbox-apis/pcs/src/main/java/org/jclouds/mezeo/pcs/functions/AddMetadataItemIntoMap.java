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
package org.jclouds.mezeo.pcs.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class AddMetadataItemIntoMap implements Function<HttpResponse, Void>, InvocationContext<AddMetadataItemIntoMap> {
   ReturnStringIf2xx returnIf200;
   private GeneratedHttpRequest<?> request;

   @Inject
   private AddMetadataItemIntoMap(ReturnStringIf2xx returnIf200) {
      this.returnIf200 = returnIf200;
   }

   @SuppressWarnings("unchecked")
   public Void apply(HttpResponse from) {
      checkState(request.getArgs() != null, "args should be initialized at this point");
      Map<String, String> map = null;
      String key = null;
      for (Object arg : request.getArgs()) {
         if (arg instanceof Map)
            map = (Map<String, String>) arg;
         else if (arg instanceof String)
            key = arg.toString();
      }
      checkState(map != null, "No Map found in args, improper method declarations");
      checkState(key != null, "No String found in args, improper method declarations");

      map.put(key, returnIf200.apply(from).trim());
      return null;
   }

   @Override
   public AddMetadataItemIntoMap setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest<?>, "note this handler requires a GeneratedHttpRequest");
      this.request = (GeneratedHttpRequest<?>) request;
      return this;
   }

}
