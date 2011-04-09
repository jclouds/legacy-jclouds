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
package org.jclouds.vcloud.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class OvfEnvelope {
   private final VirtualSystem virtualSystem;

   public OvfEnvelope(VirtualSystem virtualSystem) {
      this.virtualSystem = checkNotNull(virtualSystem, "virtualSystem");
   }

   public VirtualSystem getVirtualSystem() {
      return virtualSystem;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((virtualSystem == null) ? 0 : virtualSystem.hashCode());
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
      OvfEnvelope other = (OvfEnvelope) obj;
      if (virtualSystem == null) {
         if (other.virtualSystem != null)
            return false;
      } else if (!virtualSystem.equals(other.virtualSystem))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[virtualSystem=" + virtualSystem + "]";
   }
}