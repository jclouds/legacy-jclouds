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
package org.jclouds.rest.internal;

import java.util.Set;

import javax.ws.rs.Consumes;

import org.jclouds.reflect.Invocation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

class GetAcceptHeaders implements Function<Invocation, Set<String>> {

   @Override
   public Set<String> apply(Invocation invocation) {
      Optional<Consumes> accept = Optional.fromNullable(invocation.getInvokable().getAnnotation(Consumes.class)).or(
            Optional.fromNullable(invocation.getInvokable().getOwnerType().getRawType().getAnnotation(Consumes.class)));
      return (accept.isPresent()) ? ImmutableSet.copyOf(accept.get().value()) : ImmutableSet.<String> of();
   }
}
