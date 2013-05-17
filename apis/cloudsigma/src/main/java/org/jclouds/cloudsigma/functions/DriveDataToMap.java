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
package org.jclouds.cloudsigma.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveData;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DriveDataToMap implements Function<DriveData, Map<String, String>> {
   private final BaseDriveToMap baseDriveToMap;

   @Inject
   public DriveDataToMap(BaseDriveToMap baseDriveToMap) {
      this.baseDriveToMap = baseDriveToMap;
   }

   @Override
   public Map<String, String> apply(DriveData from) {
      return renameKey(baseDriveToMap.apply(from), "use", "use");
   }
   
   /**
    * If the supplied map contains the key {@code k1}, its value will be assigned to the key {@code
    * k2}. Note that this doesn't modify the input map.
    * 
    * @param <V>
    *           type of value the map holds
    * @param in
    *           the map you wish to make a copy of
    * @param k1
    *           old key
    * @param k2
    *           new key
    * @return copy of the map with the value of the key re-routed, or the original, if it {@code k1}
    *         wasn't present.
    */
   @VisibleForTesting
   static <V> Map<String, V> renameKey(Map<String, V> in, String k1, String k2) {
      if (checkNotNull(in, "input map").containsKey(checkNotNull(k1, "old key"))) {
         Builder<String, V> builder = ImmutableMap.builder();
         builder.putAll(filterKeys(in, not(equalTo(k1))));
         V tags = in.get(k1);
         builder.put(checkNotNull(k2, "new key"), tags);
         in = builder.build();
      }
      return in;
   }

}
