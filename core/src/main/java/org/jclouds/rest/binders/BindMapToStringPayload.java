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
package org.jclouds.rest.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.UriTemplates.expand;
import static org.jclouds.util.Strings2.urlDecode;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.reflect.Invokable;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindMapToStringPayload implements MapBinder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkNotNull(postParams, "postParams");
      GeneratedHttpRequest r = GeneratedHttpRequest.class.cast(checkNotNull(request, "request"));
      Invokable<?, ?> invoked = r.getInvocation().getInvokable();
      checkArgument(invoked.isAnnotationPresent(Payload.class),
            "method %s must have @Payload annotation to use this binder", invoked);
      String payload = invoked.getAnnotation(Payload.class).value();
      if (postParams.size() > 0) {
         payload = urlDecode(expand(payload, postParams));
      }
      return (R) request.toBuilder().payload(payload).build();
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      throw new IllegalArgumentException("this is a map binder");
   }

}
