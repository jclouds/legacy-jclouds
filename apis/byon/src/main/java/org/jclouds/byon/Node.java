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
package org.jclouds.byon;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Node {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String name;
      private String description;
      private String hostname;
      private String locationId;
      private String osArch;
      private String osFamily;
      private String osDescription;
      private String osVersion;
      private String group;
      private Set<String> tags = ImmutableSet.of();
      private String username;
      private String credential;
      private URI credentialUrl;
      private String sudoPassword;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder locationId(String locationId) {
         this.locationId = locationId;
         return this;
      }

      public Builder osArch(String osArch) {
         this.osArch = osArch;
         return this;
      }

      public Builder osFamily(String osFamily) {
         this.osFamily = osFamily;
         return this;
      }

      public Builder osDescription(String osDescription) {
         this.osDescription = osDescription;
         return this;
      }

      public Builder osVersion(String osVersion) {
         this.osVersion = osVersion;
         return this;
      }

      public Builder group(String group) {
         this.group = group;
         return this;
      }

      public Builder tags(Iterable<String> tags) {
         this.tags = ImmutableSet.copyOf(tags);
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder credential(String credential) {
         this.credential = credential;
         return this;
      }

      public Builder credentialUrl(URI credentialUrl) {
         this.credentialUrl = credentialUrl;
         return this;
      }

      public Builder sudoPassword(String sudoPassword) {
         this.sudoPassword = sudoPassword;
         return this;
      }

      public Node build() {
         return new Node(id, name, description, hostname, locationId, osArch, osFamily, osDescription, osVersion,
                  group, tags, username, credential, credentialUrl, sudoPassword);
      }
   }

   public Node(String id, String name, String description, String hostname, String locationId, String osArch,
            String osFamily, String osDescription, String osVersion, String group, Iterable<String> tags,
            String username, String credential, URI credentialUrl, String sudoPassword) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.hostname = hostname;
      this.locationId = locationId;
      this.osArch = osArch;
      this.osFamily = osFamily;
      this.osDescription = osDescription;
      this.osVersion = osVersion;
      this.group = group;
      this.tags = ImmutableSet.copyOf(tags);
      this.username = username;
      this.credential = credential;
      this.credentialUrl = credentialUrl;
      this.sudoPassword = sudoPassword;
   }

   private final String id;
   private final String name;
   private final String description;
   private final String hostname;
   private final String locationId;
   private final String osArch;
   private final String osFamily;
   private final String osDescription;
   private final String osVersion;
   private final String group;
   private final Set<String> tags;
   private final String username;
   private final String credential;
   private final URI credentialUrl;
   private final String sudoPassword;

   public String getId() {
      return id;
   }

   public String getLocationId() {
      return locationId;
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
      return osArch;
   }

   public String getOsFamily() {
      return osFamily;
   }

   public String getOsDescription() {
      return osDescription;
   }

   public String getOsVersion() {
      return osVersion;
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
      return credentialUrl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   public String getSudoPassword() {
      return sudoPassword;
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
               "locationId", locationId).add("hostname", hostname).add("osArch", osArch).add("osFamily", osFamily).add(
               "osDescription", osDescription).add("osVersion", osVersion).add("group", group).add("tags", tags).add(
               "username", username).add("hasCredential", credential != null || credentialUrl != null).add(
               "hasSudoPassword", sudoPassword != null).toString();
   }

}
