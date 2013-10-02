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
package org.jclouds.cloudservers.domain;

/**
 * In-flight images will have the status attribute set to SAVING and the conditional progress
 * element (0- 100% completion) will also be returned. Other possible values for the status
 * attribute include: UNKNOWN, PREPARING, ACTIVE QUEUED, FAILED. Images with an ACTIVE status are
 * available for install.
 * 
 * @author Adrian Cole
 */
public enum ImageStatus {

   UNRECOGNIZED, UNKNOWN, ACTIVE, SAVING, PREPARING, QUEUED, FAILED;

   public String value() {
      return name();
   }

   public static ImageStatus fromValue(String v) {
      try {
         return valueOf(v);
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
