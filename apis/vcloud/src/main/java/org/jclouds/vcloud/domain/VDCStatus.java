/**
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
package org.jclouds.vcloud.domain;

/**
 * The creation status of the vDC
 * 
 * @see VDC#getStatus
 */
public enum VDCStatus {

   CREATION_FAILED, NOT_READY, READY, UNKNOWN, UNRECOGNIZED;

   public int value() {
      switch (this) {
         case CREATION_FAILED:
            return -1;
         case NOT_READY:
            return 0;
         case READY:
            return 1;
         case UNKNOWN:
            return 2;
         default:
            return 3;
      }
   }

   public static VDCStatus fromValue(int status) {
      switch (status) {
         case -1:
            return CREATION_FAILED;
         case 0:
            return NOT_READY;
         case 1:
            return READY;
         case 2:
            return UNKNOWN;
         default:
            return UNRECOGNIZED;
      }
   }
}