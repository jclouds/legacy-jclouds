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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.util.IpPermissions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

/**
 * @author Adrian Cole
 */
public class BindIpPermissionsToIndexedFormParams implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable,
            "this binder is only valid for Iterable<IpPermission>");
      Builder<String, String> formBuilder = ImmutableMultimap.builder();
      int index = 0;
      for (IpPermission perm : (Iterable<IpPermission>) input)
         formBuilder.putAll(IpPermissions.buildFormParametersForIndex(index++, perm));
      ImmutableMultimap<String, String> forms = formBuilder.build();
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

}
