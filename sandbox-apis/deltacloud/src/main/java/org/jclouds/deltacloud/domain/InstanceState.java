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

package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * Indicator of the instance's current state
 * 
 * @author Adrian Cole
 * 
 */
public enum InstanceState {
   /**
    * initial state, before instance is created.
    */
   START,
   /**
    * the instance is in the process of being launched
    */
   PENDING,
   /**
    * the instance launched (although the boot process might not be completed)
    */
   RUNNING,
   /**
    * the instance is shutting down
    */
   SHUTTING_DOWN,
   /**
    * the instance is stopped
    */
   STOPPED,
   /**
    * the instance is terminated
    */
   FINISH,
   /**
    * state returned as something besides the above.
    */
   UNRECOGNIZED;

   public static InstanceState fromValue(String state) {
      try {
         return valueOf(checkNotNull(state, "state").toUpperCase());
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}