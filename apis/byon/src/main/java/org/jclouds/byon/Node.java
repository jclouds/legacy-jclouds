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
package org.jclouds.byon;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
      private boolean os64Bit;
      private int loginPort = 22;
      private String group;
      private Set<String> tags = ImmutableSet.of();
      private Map<String, String> metadata = ImmutableMap.<String, String>of();
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

      public Builder loginPort(int loginPort) {
         this.loginPort = loginPort;
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

      public Builder os64Bit(boolean os64Bit) {
         this.os64Bit = os64Bit;
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
      
      public Builder metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(metadata);
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
                  os64Bit, loginPort, group, tags, metadata, username, credential, credentialUrl, sudoPassword);
      }
   }

   public Node(String id, String name, String description, String hostname, String locationId, String osArch,
         String osFamily, String osDescription, String osVersion, boolean os64Bit, int loginPort, String group,
         Iterable<String> tags, Map<String, String> metadata, String username, String credential, URI credentialUrl,
         String sudoPassword) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.hostname = hostname;
      this.locationId = locationId;
      this.osArch = osArch;
      this.osFamily = osFamily;
      this.osDescription = osDescription;
      this.osVersion = osVersion;
      this.os64Bit = os64Bit;
      this.loginPort = loginPort;
      this.group = group;
      this.tags = ImmutableSet.copyOf(tags);
      this.metadata = ImmutableMap.copyOf(metadata);
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
   private final int loginPort;
   private final boolean os64Bit;
   private final String group;
   private final Set<String> tags;
   private final Map<String, String> metadata;
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

   public boolean isOs64Bit() {
      return os64Bit;
   }
   
   public int getLoginPort() {
      return loginPort;
   }
   
   public Set<String> getTags() {
      Set<String> tagSet = Sets.newHashSet();
      for (String tag : tags)
         tagSet.add(tag);
      return tagSet;
   }

   public Map<String, String> getMetadata() {
      return Maps.newLinkedHashMap(this.metadata);
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

   public String getSudoPassword() {
      return sudoPassword;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("id", id).add("name", name).add("description", description)
            .add("locationId", locationId).add("hostname", hostname).add("osArch", osArch).add("osFamily", osFamily)
            .add("osDescription", osDescription).add("osVersion", osVersion).add("os64Bit", os64Bit)
            .add("group", group).add("loginPort", loginPort).add("tags", tags).add("metadata", metadata)
            .add("username", username).add("hasCredential", credential != null || credentialUrl != null)
            .add("hasSudoPassword", sudoPassword != null).toString();
   }

}
