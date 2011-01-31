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

package org.jclouds.byon;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class Node {
   // public due to snakeyaml
   public Node() {
   }

   public Node(String id, String name, String description, String hostname, String osArch, String osFamily,
            String osDescription, String osVersion, String group, List<String> tags, String username,
            String credential, URI credentialUrl, String sudo_password) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.hostname = hostname;
      this.os_arch = osArch;
      this.os_family = osFamily;
      this.os_description = osDescription;
      this.os_version = osVersion;
      this.group = group;
      this.tags = ImmutableList.copyOf(tags);
      this.username = username;
      this.credential = credential;
      this.credential_url = credentialUrl != null ? credentialUrl.toASCIIString() : null;
      this.sudo_password = sudo_password;
   }

   // public due to snakeyaml
   public String id;
   public String name;
   public String description;
   public String hostname;
   public String os_arch;
   public String os_family;
   public String os_description;
   public String os_version;
   public String group;
   public List<String> tags;
   public String username;
   public String credential;
   public String credential_url;
   public String sudo_password;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public String getGroup() {
      return group;
   }

   public String getHostname() {
      return hostname;
   }

   public String getOsArch() {
      return os_arch;
   }

   public String getOsFamily() {
      return os_family;
   }

   public String getOsDescription() {
      return os_description;
   }

   public String getOsVersion() {
      return os_version;
   }

   public Set<String> getTags() {
      Set<String> tagSet = new HashSet<String>();
      for (String tag : tags)
         tagSet.add(tag);
      return tagSet;
   }

   public String getUsername() {
      return username;
   }

   public String getCredential() {
      return credential;
   }

   public URI getCredentialUrl() {
      return credential_url != null ? URI.create(credential_url) : null;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   public String getSudoPassword() {
      return sudo_password;
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("name", name).add("description", description).add(
               "hostname", hostname).add("osArch", os_arch).add("osFamily", os_family).add("osDescription",
               os_description).add("osVersion", os_version).add("group", group).add("tags", tags).add("username",
               username).add("hasCredential", credential != null || credential_url != null).add("hasSudoPassword",
               sudo_password != null).toString();
   }

}
