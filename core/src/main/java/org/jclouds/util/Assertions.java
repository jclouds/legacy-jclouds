/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.util;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class Assertions {

   public static boolean eventuallyTrue(Supplier<Boolean> assertion, long inconsistencyMillis)
         throws InterruptedException {
   
      for (int i = 0; i < 30; i++) {
         if (!assertion.get()) {
            Thread.sleep(inconsistencyMillis / 30);
            continue;
         }
         return true;
      }
      return false;
   }


  
}
