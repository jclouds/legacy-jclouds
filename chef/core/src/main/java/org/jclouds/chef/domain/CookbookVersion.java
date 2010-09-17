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

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * Cookbook object.
 * 
 * @author Adrian Cole
 */
public class CookbookVersion {

   private String name;
   private Set<Resource> definitions = Sets.newLinkedHashSet();
   private Set<Attribute> attributes = Sets.newLinkedHashSet();
   private Set<Resource> files = Sets.newLinkedHashSet();
   private Metadata metadata = new Metadata();
   private Set<Resource> providers = Sets.newLinkedHashSet();
   @SerializedName("cookbook_name")
   private String cookbookName;
   private Set<Resource> resources = Sets.newLinkedHashSet();
   private Set<Resource> templates = Sets.newLinkedHashSet();
   private Set<Resource> libraries = Sets.newLinkedHashSet();
   private String version;
   private Set<Resource> recipes = Sets.newLinkedHashSet();
   @SerializedName("root_files")
   private Set<Resource> rootFiles = Sets.newLinkedHashSet();

   // internal
   @SuppressWarnings("unused")
   @SerializedName("json_class")
   private String _jsonClass = "Chef::CookbookVersion";
   @SerializedName("chef_type")
   @SuppressWarnings("unused")
   private String _chefType = "cookbook_version";

   public CookbookVersion(String cookbookName, String version) {
      this.cookbookName = cookbookName;
      this.version = version;
      this.name = cookbookName + "-" + version;
   }

   public CookbookVersion(String name, Set<Resource> definitions, Set<Attribute> attributes, Set<Resource> files,
            Metadata metadata, Set<Resource> providers, String cookbookName, Set<Resource> resources,
            Set<Resource> templates, Set<Resource> libraries, String version, Set<Resource> recipes,
            Set<Resource> rootFiles) {
      this.name = name;
      Iterables.addAll(this.definitions, definitions);
      Iterables.addAll(this.attributes, attributes);
      Iterables.addAll(this.files, files);
      this.metadata = metadata;
      Iterables.addAll(this.providers, providers);
      this.cookbookName = cookbookName;
      Iterables.addAll(this.resources, resources);
      Iterables.addAll(this.templates, templates);
      Iterables.addAll(this.libraries, libraries);
      this.version = version;
      Iterables.addAll(this.recipes, recipes);
      Iterables.addAll(this.rootFiles, rootFiles);
   }

   // hidden but needs to be here for json deserialization to work
   CookbookVersion() {

   }

   public String getName() {
      return name;
   }

   public Set<Resource> getDefinitions() {
      return definitions;
   }

   public Set<Attribute> getAttributes() {
      return attributes;
   }

   public Set<Resource> getFiles() {
      return files;
   }

   public Metadata getMetadata() {
      return metadata;
   }

   public Set<Resource> getSuppliers() {
      return providers;
   }

   public String getCookbookName() {
      return cookbookName;
   }

   public Set<Resource> getResources() {
      return resources;
   }

   public Set<Resource> getTemplates() {
      return templates;
   }

   public Set<Resource> getLibraries() {
      return libraries;
   }

   public String getVersion() {
      return version;
   }

   public Set<Resource> getRecipes() {
      return recipes;
   }

   public Set<Resource> getRootFiles() {
      return rootFiles;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
      result = prime * result + ((cookbookName == null) ? 0 : cookbookName.hashCode());
      result = prime * result + ((definitions == null) ? 0 : definitions.hashCode());
      result = prime * result + ((files == null) ? 0 : files.hashCode());
      result = prime * result + ((libraries == null) ? 0 : libraries.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((providers == null) ? 0 : providers.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((resources == null) ? 0 : resources.hashCode());
      result = prime * result + ((rootFiles == null) ? 0 : rootFiles.hashCode());
      result = prime * result + ((templates == null) ? 0 : templates.hashCode());
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
      CookbookVersion other = (CookbookVersion) obj;
      if (attributes == null) {
         if (other.attributes != null)
            return false;
      } else if (!attributes.equals(other.attributes))
         return false;
      if (cookbookName == null) {
         if (other.cookbookName != null)
            return false;
      } else if (!cookbookName.equals(other.cookbookName))
         return false;
      if (definitions == null) {
         if (other.definitions != null)
            return false;
      } else if (!definitions.equals(other.definitions))
         return false;
      if (files == null) {
         if (other.files != null)
            return false;
      } else if (!files.equals(other.files))
         return false;
      if (libraries == null) {
         if (other.libraries != null)
            return false;
      } else if (!libraries.equals(other.libraries))
         return false;
      if (metadata == null) {
         if (other.metadata != null)
            return false;
      } else if (!metadata.equals(other.metadata))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (providers == null) {
         if (other.providers != null)
            return false;
      } else if (!providers.equals(other.providers))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (resources == null) {
         if (other.resources != null)
            return false;
      } else if (!resources.equals(other.resources))
         return false;
      if (rootFiles == null) {
         if (other.rootFiles != null)
            return false;
      } else if (!rootFiles.equals(other.rootFiles))
         return false;
      if (templates == null) {
         if (other.templates != null)
            return false;
      } else if (!templates.equals(other.templates))
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
      return "Cookbook [attributes=" + attributes + ", cookbookName=" + cookbookName + ", definitions=" + definitions
               + ", files=" + files + ", libraries=" + libraries + ", metadata=" + metadata + ", name=" + name
               + ", providers=" + providers + ", recipes=" + recipes + ", resources=" + resources + ", rootFiles="
               + rootFiles + ", templates=" + templates + ", version=" + version + "]";
   }

}
