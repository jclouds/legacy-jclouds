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
package org.jclouds.cloudwatch.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_GetMetricStatistics.html?r=5424"/>
 * 
 * @author Adrian Cole, Andrei Savu
 */
public enum Unit {
   SECONDS,
   MICROSECONDS,
   MILLISECONDS,
   BYTES,
   KILOBYTES,
   MEGABYTES,
   GIGABYTES,
   TERABYTES,
   BITS,
   KILOBITS,
   MEGABITS,
   GIGABITS,
   TERABITS,
   PERCENT,
   COUNT,
   BYTES_PER_SECOND,
   KILOBYTES_PER_SECOND,
   MEGABYTES_PER_SECOND,
   GIGABYTES_PER_SECOND,
   TERABYTES_PER_SECOND,
   BITS_PER_SECOND,
   KILOBITS_PER_SECOND,
   MEGABITS_PER_SECOND,
   GIGABITS_PER_SECOND,
   TERABITS_PER_SECOND,
   COUNT_PER_SECOND,
   NONE,
   UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name()).replace("PerSecond", "/Second");
   }

   @Override
   public String toString() {
      return value();
   }

   public static Unit fromValue(String value) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(value, "value").replace(
                  "/", "Per")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}
