/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudsigma.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.ClaimType;
import org.jclouds.cloudsigma.domain.Drive;

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
      if (from.getUse().size() != 0)
         builder.put("use", Joiner.on(' ').join(from.getUse()));
      return builder.build();
   }
}