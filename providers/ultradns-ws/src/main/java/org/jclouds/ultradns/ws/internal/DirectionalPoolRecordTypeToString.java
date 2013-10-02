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
package org.jclouds.ultradns.ws.internal;

import java.util.EnumSet;

import javax.inject.Inject;

import org.jclouds.ultradns.ws.domain.DirectionalPool.RecordType;

import com.google.common.base.Function;

/**
 * pools use the address type also for cnames.
 */
public class DirectionalPoolRecordTypeToString implements Function<Object, String> {
   @Inject
   private DirectionalPoolRecordTypeToString() {
   }

   public String apply(Object in) {
      int rrType = Integer.class.cast(in);
      for (RecordType type : EnumSet.allOf(RecordType.class)) {
         if (type.getCode() == rrType) {
            switch (type) {
            case IPV4:
               return "A";
            case IPV6:
               return "AAAA";
            default:
               return type.toString();
            }
         }
      }
      throw new IllegalArgumentException("unsupported rrType " + rrType + " please see " + RecordType.class.getName());
   }
}
