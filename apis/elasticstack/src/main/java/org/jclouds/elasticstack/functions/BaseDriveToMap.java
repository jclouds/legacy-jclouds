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
package org.jclouds.elasticstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.Drive;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BaseDriveToMap implements Function<Drive, Map<String, String>> {
   @Override
   public Map<String, String> apply(Drive from) {
      checkNotNull(from, "drive");
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      builder.put("name", from.getName());
      builder.put("size", from.getSize() + "");
      if (from.getClaimType() != ClaimType.EXCLUSIVE)
         builder.put("claim:type", from.getClaimType().toString());
      if (from.getReaders().size() != 0)
         builder.put("readers", Joiner.on(' ').join(from.getReaders()));
      if (from.getTags().size() != 0)
         builder.put("tags", Joiner.on(' ').join(from.getTags()));
      for (Entry<String, String> entry : from.getUserMetadata().entrySet())
         builder.put("user:" + entry.getKey(), entry.getValue());
      return builder.build();
   }
}
