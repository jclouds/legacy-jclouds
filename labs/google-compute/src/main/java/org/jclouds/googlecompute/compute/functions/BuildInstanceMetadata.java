/*
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

package org.jclouds.googlecompute.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.jclouds.compute.options.TemplateOptions;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Prepares metadata from the provided TemplateOptions
 *
 * @author David Alves
 */
@Singleton
public class BuildInstanceMetadata implements Function<TemplateOptions, ImmutableMap.Builder<String, String>> {

   @Override
   public ImmutableMap.Builder apply(TemplateOptions input) {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      if (input.getPublicKey() != null) {
         builder.put("sshKeys", format("%s:%s %s@localhost", checkNotNull(input.getLoginUser(),
                 "loginUser cannot be null"), input.getPublicKey(), input.getLoginUser()));
      }
      builder.putAll(input.getUserMetadata());
      return builder;
   }

}
