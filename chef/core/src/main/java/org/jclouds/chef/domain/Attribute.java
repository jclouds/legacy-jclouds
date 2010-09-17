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

import java.util.List;
import java.util.Set;

import org.jclouds.domain.JsonBall;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

/**
 * Cookbook object.
 * 
 * @author Adrian Cole
 */
public class Attribute {

   private String required;
   private boolean calculated;
   private List<String> choice = Lists.newArrayList();
   @SerializedName("default")
   private JsonBall defaultValue;
   private String type;
   private List<String> recipes = Lists.newArrayList();
   @SerializedName("display_name")
   private String displayName;
   private String description;

   public Attribute(String required, boolean calculated, Set<String> choice, JsonBall defaultValue, String type,
         List<String> recipes, String displayName, String description) {
      this.required = required;
      this.calculated = calculated;
      Iterables.addAll(this.choice, choice);
      this.defaultValue = defaultValue;
      this.type = type;
      Iterables.addAll(this.recipes, recipes);
      this.displayName = displayName;
      this.description = description;
   }

   public Attribute() {
   }

   public String getRequired() {
      return required;
   }

   public boolean isCalculated() {
      return calculated;
   }

   public List<String> getChoice() {
      return choice;
   }

   public JsonBall getDefaultValue() {
      return defaultValue;
   }

   public String getType() {
      return type;
   }

   public List<String> getRecipes() {
      return recipes;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (calculated ? 1231 : 1237);
      result = prime * result + ((choice == null) ? 0 : choice.hashCode());
      result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
      result = prime * result + ((required == null) ? 0 : required.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      Attribute other = (Attribute) obj;
      if (calculated != other.calculated)
         return false;
      if (choice == null) {
         if (other.choice != null)
            return false;
      } else if (!choice.equals(other.choice))
         return false;
      if (defaultValue == null) {
         if (other.defaultValue != null)
            return false;
      } else if (!defaultValue.equals(other.defaultValue))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (recipes == null) {
         if (other.recipes != null)
            return false;
      } else if (!recipes.equals(other.recipes))
         return false;
      if (required == null) {
         if (other.required != null)
            return false;
      } else if (!required.equals(other.required))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Attribute [calculated=" + calculated + ", choice=" + choice + ", defaultValue=" + defaultValue
            + ", description=" + description + ", displayName=" + displayName + ", recipes=" + recipes + ", required="
            + required + ", type=" + type + "]";
   }

}