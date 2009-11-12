/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adrian Cole
 */
public enum VAppStatus {
   CREATING, OFF, ON;

   public String value() {
      switch (this) {
         case CREATING:
            return "0";
         case OFF:
            return "2";
         case ON:
            return "4";
         default:
            throw new IllegalArgumentException("invalid status:" + this);
      }
   }

   public static VAppStatus fromValue(String status) {
      return fromValue(Integer.parseInt(checkNotNull(status, "status")));
   }

   public static VAppStatus fromValue(int v) {
      switch (v) {
         case 0:
            return CREATING;
         case 2:
            return OFF;
         case 4:
            return ON;
         default:
            throw new IllegalArgumentException("invalid status:" + v);
      }
   }

}