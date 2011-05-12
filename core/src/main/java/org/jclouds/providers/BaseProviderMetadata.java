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
package org.jclouds.providers;

import java.net.URI;

/**
 * The BaseProviderMetadata class is an abstraction of {@link ProviderMetadata} to be extended
 * by those implementing ProviderMetadata.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public abstract class BaseProviderMetadata implements ProviderMetadata {

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      URI console = getConsole();
      URI homepage = getHomepage();
      String id = getId();
      String name = getName();
      String type = getType();

      result = prime * result + ((console == null) ? 0 : console.hashCode());
      result = prime * result + ((homepage == null) ? 0 : homepage.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());

      return result;
    }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      URI tConsole = getConsole();
      URI tHomepage = getHomepage();
      String tId = getId();
      String tName = getName();
      String tType = getType();

      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;

      ProviderMetadata other = (ProviderMetadata) obj;
      URI oConsole = other.getConsole();
      URI oHomepage = other.getHomepage();
      String oId = other.getId();
      String oName = other.getName();
      String oType = other.getType();

      if (tConsole == null) {
         if (oConsole != null)
            return false;
      } else if (!tConsole.equals(oConsole))
         return false;
      if (tHomepage == null) {
         if (oHomepage != null)
            return false;
      } else if (!tHomepage.equals(oHomepage))
         return false;
      if (tId == null) {
         if (oId != null)
            return false;
      } else if (!tId.equals(oId))
         return false;
      if (tName == null) {
         if (oName != null)
            return false;
      } else if (!tName.equals(oName))
         return false;
      if (tType == null) {
         if (oType != null)
            return false;
      } else if (!tType.equals(oType))
         return false;

      return true;
   }

}
