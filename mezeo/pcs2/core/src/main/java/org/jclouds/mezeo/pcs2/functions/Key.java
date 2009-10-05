/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.mezeo.pcs2.functions;

public class Key {
   private final String container;
   private final String key;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getContainer() == null) ? 0 : getContainer().hashCode());
      result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Key other = (Key) obj;
      if (getContainer() == null) {
         if (other.getContainer() != null)
            return false;
      } else if (!getContainer().equals(other.getContainer()))
         return false;
      if (getKey() == null) {
         if (other.getKey() != null)
            return false;
      } else if (!getKey().equals(other.getKey()))
         return false;
      return true;
   }

   public Key(String container, String key) {
      super();
      this.container = container;
      this.key = key;
   }

   public String getContainer() {
      return container;
   }

   public String getKey() {
      return key;
   }
}