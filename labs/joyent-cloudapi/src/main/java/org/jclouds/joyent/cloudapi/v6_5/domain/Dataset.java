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
package org.jclouds.joyent.cloudapi.v6_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.domain.JsonBall;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine.Type;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A dataset is the image of the software on your machine. It contains the software packages that
 * will be available on newly provisioned machines. In the case of virtual machines, the dataset
 * also includes the operating system.
 * 
 * @author Gerald Pereira
 * @see <a href= "http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#ListDatasets" >docs</a>
 */
public class Dataset implements Comparable<Dataset> {

   public static Builder builder() {
      return new Builder();
   }
   
   public Builder toBuilder() {
      return new Builder().fromDataset(this);
   }

   public static class Builder {
      private String id;
      private String urn;
      private String name;
      private String os;
      private Type type;
      private String description;
      private boolean isDefault;
      private ImmutableMap.Builder<String, JsonBall> requirements = ImmutableMap.<String, JsonBall>builder();
      private String version;
      private Date created;

      /**
       * @see Dataset#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Dataset#getUrn()
       */
      public Builder urn(String urn) {
         this.urn = urn;
         return this;
      }

      /**
       * @see Dataset#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }
      
      /**
       * @see Dataset#getOs()
       */
      public Builder os(String os) {
         this.os = os;
         return this;
      }

      /**
       * @see Dataset#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }
      
      /**
       * @see Dataset#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }
      
      /**
       * @see Dataset#isDefault()
       */
      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      
      /**
       * @see Dataset#getRequirements()
       */
      public Builder requirements(Map<String, JsonBall> requirements) {
         this.requirements = ImmutableMap.<String, JsonBall> builder();
         this.requirements.putAll(checkNotNull(requirements, "requirements"));
         return this;
      }
      
      /**
       * @see Dataset#getRequirements()
       */
      public Builder addRequirement(String name, JsonBall values) {
         this.requirements.put(checkNotNull(name, "name"), checkNotNull(values, "value of %s", name));
         return this;
      }
      
      /**
       * @see Dataset#getVersion()
       */
      public Builder version(String version) {
         this.version = version;
         return this;
      }
      
      /**
       * @see Dataset#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }
      
      public Dataset build() {
         return new Dataset(id, urn, name, os, type, description, isDefault, requirements.build(), version,
                  created);
      }

      public Builder fromDataset(Dataset in) {
         return id(in.getId()).urn(in.getUrn()).name(in.getName()).os(in.getOs()).type(in.getType()).description(in.getDescription())
                  .isDefault(in.isDefault()).requirements(in.requirements).version(in.getVersion()).created(in.getCreated());
      }
   }

   private final String id;
   private final String name;
   private final String os;
   private final String urn;
   private final Type type;
   private final String description;
   @Named("default")
   private final boolean isDefault;
   private final Map<String, JsonBall> requirements;
   private final String version;
   private final Date created;
   
   @ConstructorProperties({ "id", "urn", "name", "os", "type", "description", "default", "requirements", "version",
            "created" })
   public Dataset(String id, String urn, String name, String os, Type type, String description, boolean isDefault,
            Map<String, JsonBall> requirements, String version, Date created) {
      this.id = checkNotNull(id, "id");
      this.urn = checkNotNull(urn, "urn of dataset(%s)", id);
      this.name = checkNotNull(name, "name of dataset(%s)", id);
      this.os = checkNotNull(os, "os of dataset(%s)", id);
      this.type = checkNotNull(type, "type of dataset(%s)", id);
      this.description = checkNotNull(description, "description of dataset(%s)", id);
      this.isDefault = isDefault;
      this.requirements = ImmutableMap.copyOf(checkNotNull(requirements, "requirements of dataset(%s)", id));
      this.version = checkNotNull(version, "version of dataset(%s)", id);
      this.created = checkNotNull(created, "created of dataset(%s)", id);
   }

   /**
    * The globally unique id for this dataset
    */
   public String getId() {
      return id;
   }

   /**
    * The full URN for this dataset
    */
   public String getUrn() {
      return urn;
   }
   
   /**
    * The friendly name for this dataset
    */
   public String getName() {
      return name;
   }

   /**
    * The underlying operating system for this dataset
    */
   public String getOs() {
      return os;
   }

   /**
    * Whether this is a smartmachine or virtualmachine dataset
    */
   public Type getType() {
      return type;
   }
   
   /**
    * The description of this dataset
    */
   public String getDescription() {
      return description;
   }
   
   /**
    * Whether this is the default dataset in this datacenter
    */
   public boolean isDefault() {
      return isDefault;
   }

   /**
    * If the value is a string, it will be quoted, as that's how json strings are represented.
    * 
    * @return key to a json literal of the value
    * @see #getRequirements
    * @see Json#fromJson
    */
   public Map<String, String> getRequirementsAsJsonLiterals() {
      return Maps.transformValues(requirements, Functions.toStringFunction());
   }

   /**
    * Contains a grouping of various minimum requirements for provisioning a machine with this
    * dataset. For example 'password' indicates that a password must be provided.
    * 
    * <h4>Note</h4>
    * 
    * requirements can contain arbitrarily complex values. If the value has structure, you should
    * use {@link #getRequirementsAsJsonLiterals}
    */
   public Map<String, String> getRequirements() {
      return Maps.transformValues(requirements, Functions.compose(Functions.toStringFunction(), unquoteString));
   }

   /**
    * The version for this dataset
    */
   public String getVersion() {
      return version;
   }

   /**
    * When the dataset was created
    */
   public Date getCreated() {
      return created;
   }

   @VisibleForTesting
   static final Function<JsonBall, String> unquoteString = new Function<JsonBall, String>() {

      @Override
      public String apply(JsonBall input) {
         String value = input.toString();
         if (value.length() >= 2 && value.charAt(0) == '"' && value.charAt(input.length() - 1) == '"')
            return value.substring(1, input.length() - 1);
         return value;
      }

   };

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Dataset) {
         Dataset that = Dataset.class.cast(object);
         return Objects.equal(id, that.id);
      } else {
         return false;
      }
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }
   
   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("id", id)
                    .add("urn", urn)
                    .add("name", name)
                    .add("os", os)
                    .add("type", type)
                    .add("description", description)
                    .add("default", isDefault)
                    .add("requirements", requirements)
                    .add("version", version)
                    .add("created", created).toString();
   }
   
   @Override
   public int compareTo(Dataset that) {
      return ComparisonChain.start()
                            .compare(this.urn, that.urn)
                            .compare(this.name, that.name)
                            .compare(this.os, that.os)
                            .compare(this.type, that.type)
                            .compare(this.description, that.description)
                            .compare(this.version, that.version)
                            .compare(this.created, that.created)
                            .compare(this.id, that.id).result();
   }

}
