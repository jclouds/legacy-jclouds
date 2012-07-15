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
package org.jclouds.elb.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * Binds the Iterable<String> to form parameters named with Instances.member.index.InstanceId
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindInstanceIdsToIndexedFormParams implements Binder {
   
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Iterable<?> values = Iterable.class.cast(checkNotNull(input, "instanceIds"));
      Builder<String, String> builder = ImmutableMultimap.builder();
      int i = 0;
      for (Object o : values) {
         builder.put("Instances.member." + (i++ + 1) + ".InstanceId", o.toString());
      }
      ImmutableMultimap<String, String> forms = builder.build();
      return (R) (forms.size() == 0 ? request : request.toBuilder().replaceFormParams(forms).build());
   }

}
