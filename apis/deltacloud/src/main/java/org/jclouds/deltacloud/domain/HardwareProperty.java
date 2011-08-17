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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Adrian Cole
 */
public interface HardwareProperty {
   public static enum Kind {
      /**
       * only the value specified in the property is available
       */
      FIXED,
      /**
       * a list of available values is provided
       */
      ENUM,
      /**
       * available values are described by a numeric range
       */
      RANGE,
      /**
       * type returned as something besides the above.
       */
      UNRECOGNIZED;

      public static HardwareProperty.Kind fromValue(String kind) {
         try {
            return valueOf(checkNotNull(kind, "kind").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   /**
    * 
    * @return describes the values to chose from.
    */
   HardwareProperty.Kind getKind();

   /**
    * 
    * @return the type of the property: e.g. memory or storage
    */
   String getName();

   /**
    * 
    * @return the units in which the value is specified: MB, GB, count or label
    */
   String getUnit();

   /**
    * 
    * @return the actual value of the property. It depends on the specified unit: 1024, 2 on x86_64
    */
   Object getValue();
}
