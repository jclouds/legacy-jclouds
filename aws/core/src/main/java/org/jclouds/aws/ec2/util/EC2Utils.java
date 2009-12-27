/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Utils {
   public static void indexStringArrayToFormValuesWithPrefix(GeneratedHttpRequest<?> request,
            String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof String[],
               "this binder is only valid for String[] : " + input.getClass());
      String[] values = (String[]) input;
      for (int i = 0; i < values.length; i++) {
         request.addFormParam(prefix + "." + (i + 1), checkNotNull(values[i], prefix.toLowerCase()
                  + "s[" + i + "]"));
      }
   }

   public static void indexIterableToFormValuesWithPrefix(GeneratedHttpRequest<?> request,
            String prefix, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Iterable<?>,
               "this binder is only valid for Iterable<?>: " + input.getClass());
      Iterable<?> values = (Iterable<?>) input;
      int i = 0;
      for (Object o : values) {
         request.addFormParam(prefix + "." + (i++ + 1), checkNotNull(o.toString(), prefix
                  .toLowerCase()
                  + "s[" + i + "]"));
      }
   }
}