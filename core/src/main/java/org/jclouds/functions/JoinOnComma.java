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
package org.jclouds.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;

import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class JoinOnComma implements Function<Object, String> {

   public String apply(Object o) {
      checkNotNull(o, "input cannot be null");
      if (o.getClass().isArray()) {
         Builder<Object> builder = ImmutableList.builder();
         for (int i = 0; i < Array.getLength(o); i++)
            builder.add(Array.get(o, i));
         o = builder.build();
      }
      checkArgument(o instanceof Iterable<?>, "you must pass an iterable or array");
      Iterable<?> toJoin = (Iterable<?>) o;
      checkArgument(Iterables.size(toJoin) > 0, "you must pass an iterable or array with elements");
      return Joiner.on(',').join(toJoin);
   }
}
