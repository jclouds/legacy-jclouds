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

package org.jclouds.chef.domain;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * Cookbook object.
 * 
 * @author Adrian Cole
 */
public class Metadata {

   private String license;
   private String maintainer;
   private Map<String, String> suggestions = Maps.newLinkedHashMap();
   private Map<String, Set<String>> dependencies = Maps.newLinkedHashMap();
   @SerializedName("maintainer_email")
   private String maintainerEmail;
   private Map<String, Set<String>> conflicting = Maps.newLinkedHashMap();
   private String description;
   private Map<String, Set<String>> providing = Maps.newLinkedHashMap();
   private Map<String, Set<String>> platforms = Maps.newLinkedHashMap();
   private String version;
   private Map<String, String> recipes = Maps.newLinkedHashMap();
   private Map<String, Set<String>> replacing = Maps.newLinkedHashMap();
   private String name;
   private Map<String, String> groupings = Maps.newLinkedHashMap();
   @SerializedName("long_description")
   private String longDescription;
   private Map<String, Attribute> attributes = Maps.newLinkedHashMap();
   private Map<String, String> recommendations = Maps.newLinkedHashMap();

   public Metadata(String license, String maintainer, Map<String, String> suggestions,
         Map<String, Set<String>> dependencies, String maintainerEmail, Map<String, Set<String>> conflicting,
         String description, Map<String, Set<String>> providing, Map<String, Set<String>> platforms, String version,
         Map<String, String> recipes, Map<String, Set<String>> replacing, String name, Map<String, String> groupings,
         String longDescription, Map<String, Attribute> attributes, Map<String, String> recommendations) {
      this.license = license;
      this.maintainer = maintainer;
      this.suggestions.putAll(suggestions);
      this.dependencies.putAll(dependencies);
      this.maintainerEmail = maintainerEmail;
      this.conflicting.putAll(conflicting);
      this.description = description;
      this.providing.putAll(providing);
      this.platforms.putAll(platforms);
      this.version = version;
      this.recipes.putAll(recipes);
      this.replacing.putAll(replacing);
      this.name = name;
      this.groupings.putAll(groupings);
      this.longDescription = longDescription;
      this.attributes.putAll(attributes);
      this.recommendations.putAll(recommendations);
   }

   public Metadata() {
   }

   public String getLicense() {
      return license;
   }

   public String getMaintainer() {
      return maintainer;
   }

   public Map<String, String> getSuggestions() {
      return suggestions;
   }

   public Map<String, Set<String>> getDependencies() {
      return dependencies;
   }

   public String getMaintainerEmail() {
      return maintainerEmail;
   }

   public Map<String, Set<String>> getConflicting() {
      return conflicting;
   }

   public String getDescription() {
      return description;
   }

   public Map<String, Set<String>> getProviding() {
      return providing;
   }

   public Map<String, Set<String>> getPlatforms() {
      return platforms;
   }

   public String getVersion() {
      return version;
   }

   public Map<String, String> getRecipes() {
      return recipes;
   }

   public Map<String, Set<String>> getReplacing() {
      return replacing;
   }

   public String getName() {
      return name;
   }

   public Map<String, String> getGroupings() {
      return groupings;
   }

   public String getLongDescription() {
      return longDescription;
   }

   public Map<String, Attribute> getAttributes() {
      return attributes;
   }

   public Map<String, String> getRecommendations() {
      return recommendations;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
      result = prime * result + ((conflicting == null) ? 0 : conflicting.hashCode());
      result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((groupings == null) ? 0 : groupings.hashCode());
      result = prime * result + ((license == null) ? 0 : license.hashCode());
      result = prime * result + ((longDescription == null) ? 0 : longDescription.hashCode());
      result = prime * result + ((maintainer == null) ? 0 : maintainer.hashCode());
      result = prime * result + ((maintainerEmail == null) ? 0 : maintainerEmail.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((platforms == null) ? 0 : platforms.hashCode());
      result = prime * result + ((providing == null) ? 0 : providing.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((recommendations == null) ? 0 : recommendations.hashCode());
      result = prime * result + ((replacing == null) ? 0 : replacing.hashCode());
      result = prime * result + ((suggestions == null) ? 0 : suggestions.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      Metadata other = (Metadata) obj;
      if (attributes == null) {
         if (other.attributes != null)
            return false;
      } else if (!attributes.equals(other.attributes))
         return false;
      if (conflicting == null) {
         if (other.conflicting != null)
            return false;
      } else if (!conflicting.equals(other.conflicting))
         return false;
      if (dependencies == null) {
         if (other.dependencies != null)
            return false;
      } else if (!dependencies.equals(other.dependencies))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (groupings == null) {
         if (other.groupings != null)
            return false;
      } else if (!groupings.equals(other.groupings))
         return false;
      if (license == null) {
         if (other.license != null)
            return false;
      } else if (!license.equals(other.license))
         return false;
      if (longDescription == null) {
         if (other.longDescription != null)
            return false;
      } else if (!longDescription.equals(other.longDescription))
         return false;
      if (maintainer == null) {
         if (other.maintainer != null)
            return false;
      } else if (!maintainer.equals(other.maintainer))
         return false;
      if (maintainerEmail == null) {
         if (other.maintainerEmail != null)
            return false;
      } else if (!maintainerEmail.equals(other.maintainerEmail))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (platforms == null) {
         if (other.platforms != null)
            return false;
      } else if (!platforms.equals(other.platforms))
         return false;
      if (providing == null) {
         if (other.providing != null)
            return false;
      } else if (!providing.equals(other.providing))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (recommendations == null) {
         if (other.recommendations != null)
            return false;
      } else if (!recommendations.equals(other.recommendations))
         return false;
      if (replacing == null) {
         if (other.replacing != null)
            return false;
      } else if (!replacing.equals(other.replacing))
         return false;
      if (suggestions == null) {
         if (other.suggestions != null)
            return false;
      } else if (!suggestions.equals(other.suggestions))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Metadata [attributes=" + attributes + ", conflicting=" + conflicting + ", dependencies=" + dependencies
            + ", description=" + description + ", groupings=" + groupings + ", license=" + license
            + ", longDescription=" + longDescription + ", maintainer=" + maintainer + ", maintainerEmail="
            + maintainerEmail + ", name=" + name + ", platforms=" + platforms + ", providing=" + providing
            + ", recipes=" + recipes + ", recommendations=" + recommendations + ", replacing=" + replacing
            + ", suggestions=" + suggestions + ", version=" + version + "]";
   }

}