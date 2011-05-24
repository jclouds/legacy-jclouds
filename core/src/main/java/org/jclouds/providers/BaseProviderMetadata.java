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
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * The BaseProviderMetadata class is an abstraction of {@link ProviderMetadata} to be extended by
 * those implementing ProviderMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader} to work properly.
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
      URI docs = getApiDocumentation();
      String id = getId();
      String name = getName();
      String identityName = getIdentityName();
      String credentialName = getCredentialName();
      String type = getType();
      Set<String> linkedServices = getLinkedServices();
      Set<String> iso3166Codes = getIso3166Codes();

      result = prime * result + ((console == null) ? 0 : console.hashCode());
      result = prime * result + ((homepage == null) ? 0 : homepage.hashCode());
      result = prime * result + ((docs == null) ? 0 : docs.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((identityName == null) ? 0 : identityName.hashCode());
      result = prime * result + ((credentialName == null) ? 0 : credentialName.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((linkedServices == null) ? 0 : linkedServices.hashCode());
      result = prime * result + ((iso3166Codes == null) ? 0 : iso3166Codes.hashCode());

      return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      URI tConsole = getConsole();
      URI tHomepage = getHomepage();
      URI tDocs = getApiDocumentation();
      String tId = getId();
      String tName = getName();
      String tIdentityName = getIdentityName();
      String tCredentialName = getCredentialName();
      String tType = getType();
      Set<String> tLinkedServices = getLinkedServices();
      Set<String> tIso3166Codes = getIso3166Codes();

      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;

      ProviderMetadata other = (ProviderMetadata) obj;
      URI oConsole = other.getConsole();
      URI oHomepage = other.getHomepage();
      URI oDocs = other.getApiDocumentation();
      String oId = other.getId();
      String oName = other.getName();
      String oIdentityName = other.getIdentityName();
      String oCredentialName = other.getCredentialName();
      String oType = other.getType();
      Set<String> oLinkedServices = other.getLinkedServices();
      Set<String> oIso3166Codes = other.getIso3166Codes();

      if (tConsole == null) {
         if (oConsole != null)
            return false;
      } else if (!tConsole.equals(oConsole))
         return false;
      if (tDocs == null) {
         if (oDocs != null)
            return false;
      } else if (!tDocs.equals(oDocs))
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
      if (tIdentityName == null) {
         if (oIdentityName != null)
            return false;
      } else if (!tIdentityName.equals(oIdentityName))
         return false;
      if (tCredentialName == null) {
         if (oCredentialName != null)
            return false;
      } else if (!tCredentialName.equals(oCredentialName))
         return false;
      if (tType == null) {
         if (oType != null)
            return false;
      } else if (!tType.equals(oType))
         return false;
      if (tLinkedServices == null) {
         if (oLinkedServices != null)
            return false;
      } else if (!tLinkedServices.equals(oLinkedServices))
         return false;
      if (tIso3166Codes == null) {
         if (oIso3166Codes != null)
            return false;
      } else if (!tIso3166Codes.equals(oIso3166Codes))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", type=" + getType() + ", name=" + getName() + ", identityName=" + getIdentityName()
               + ", credentialName=" + getCredentialName() + ", homePage=" + getHomepage() + ", console="
               + getConsole() + ", apiDocs=" + getApiDocumentation() + ", linkedServices=" + getLinkedServices() +
               ", iso3166Codes=" + getIso3166Codes() + "]";
   }

   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of(getId());
   }
}
