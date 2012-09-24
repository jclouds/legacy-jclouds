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
package org.jclouds.virtualbox.util;

import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IMediumAttachment;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class IMediumAttachments {
   static enum ToMedium implements Function<IMediumAttachment, IMedium> {
      INSTANCE;
      @Override
      public IMedium apply(IMediumAttachment arg0) {
         return arg0.getMedium();
      }

      @Override
      public String toString() {
         return "toMedium()";
      }
   };

   public static Function<IMediumAttachment, IMedium> toMedium() {
      return ToMedium.INSTANCE;
   }
}
