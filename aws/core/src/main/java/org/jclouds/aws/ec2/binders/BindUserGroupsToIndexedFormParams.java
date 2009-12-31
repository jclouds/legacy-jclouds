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
package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.collect.Iterables;

/**
 * Binds the Iterable to query parameters named with UserGroup.index
 * 
 * @author Adrian Cole
 */
public class BindUserGroupsToIndexedFormParams implements Binder {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>,
               "this binder is only valid for Iterable<?>: " + input.getClass());
      checkValidUserGroup(input);
      EC2Utils.indexIterableToFormValuesWithPrefix((GeneratedHttpRequest<?>) request, "UserGroup",
               input);
   }

   private void checkValidUserGroup(Object input) {
      Iterable<?> values = (Iterable<?>) input;
      long size = Iterables.size(values);
      checkArgument(size == 0 || (size == 1 && Iterables.getOnlyElement(values).equals("all")),
               "only supported UserGroup is 'all'");
   }

}