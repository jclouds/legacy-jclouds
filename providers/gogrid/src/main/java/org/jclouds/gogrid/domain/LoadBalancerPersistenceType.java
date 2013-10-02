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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oleksiy Yarmula
 */
public enum LoadBalancerPersistenceType {

   NONE("None"), SSL_STICKY("SSL Sticky"), SOURCE_ADDRESS("Source Address"), UNRECOGNIZED("Unknown");

   String type;

   LoadBalancerPersistenceType(String type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return type;
   }

   public static LoadBalancerPersistenceType fromValue(String type) {
      for (LoadBalancerPersistenceType persistenceType : values()) {
         if (persistenceType.type.equals(checkNotNull(type)))
            return persistenceType;
      }
      return UNRECOGNIZED;
   }
}
