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
import java.util.Set;

import javax.inject.Named;

import org.jclouds.domain.JsonBall;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * a SmartMachine or traditional Virtual Machine
 * 
 * @author Gerald Pereira
 * @see <a href= "http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#machines" />
 */
public class Machine implements Comparable<Machine> {
   
   public static enum Type {
      VIRTUALMACHINE, SMARTMACHINE, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }
   }
   
   public static enum State {
      PROVISIONING, RUNNING, STOPPING, STOPPED, OFFLINE, DELETED, UNRECOGNIZED;

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }
   }

   public static Builder builder() {
      return new Builder();
   }
   
   public Builder toBuilder() {
      return new Builder().fromMachine(this);
   }

   public static class Builder {
      private String id;
      private String name;
      private Type type;
      private State state;
      private String dataset;
      private int memorySizeMb;
      private int diskSizeGb;
      private ImmutableSet.Builder<String> ips = ImmutableSet.<String> builder();
      private Date created;
      private Date updated;
      private ImmutableMap.Builder<String, JsonBall> metadata = ImmutableMap.<String, JsonBall>builder();
      
      /**
       * @see Machine#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }
      
      /**
       * @see Machine#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }
      
      /**
       * @see Machine#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }
      
      /**
       * @see Machine#getState()
       */
      public Builder state(State state) {
         this.state = state;
         return this;
      }
      
      /**
       * @see Machine#getDatasetURN()
       */
      public Builder dataset(String dataset) {
         this.dataset = dataset;
         return this;
      }
      
      /**
       * @see Machine#getMemorySizeMb()
       */
      public Builder memorySizeMb(int memorySizeMb) {
         this.memorySizeMb = memorySizeMb;
         return this;
      }
      
      /**
       * @see Machine#getDiskSizeGb()
       */
      public Builder diskSizeGb(int diskSizeGb) {
         this.diskSizeGb = diskSizeGb;
         return this;
      }

      /**
       * @see Machine#getIps()
       */
      public Builder ips(Set<String> ips) {
         this.ips = ImmutableSet.<String> builder();
         this.ips.addAll(checkNotNull(ips, "ips"));
         return this;
      }

      /**
       * @see Machine#getIps()
       */
      public Builder addIp(String ip) {
         this.ips.add(checkNotNull(ip, "ip"));
         return this;
      }
      
      /**
       * @see Machine#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }
      
      /**
       * @see Machine#getUpdated()
       */
      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      /**
       * @see Machine#getMetadata()
       */
      public Builder metadata(Map<String, JsonBall> metadata) {
         this.metadata = ImmutableMap.<String, JsonBall> builder();
         this.metadata.putAll(checkNotNull(metadata, "metadata"));
         return this;
      }
      
      /**
       * @see Machine#getMetadata()
       */
      public Builder addMetadata(String name, JsonBall values) {
         this.metadata.put(checkNotNull(name, "name"), checkNotNull(values, "value of %s", name));
         return this;
      }

      public Machine build() {
         return new Machine(id, name, type, state, dataset, memorySizeMb, diskSizeGb, ips.build(), created, updated, metadata.build());
      }

      public Builder fromMachine(Machine in) {
         return id(in.getId()).name(in.getName()).type(in.getType()).state(in.getState()).dataset(in.getDatasetURN())
               .memorySizeMb(in.getMemorySizeMb()).diskSizeGb(in.getDiskSizeGb()).ips(in.getIps())
               .metadata(in.metadata).created(in.getCreated()).updated(in.getUpdated());
      }
   }

   protected final String id;
   protected final String name;
   protected final Type type;
   protected final State state;
   protected final String dataset;
   @Named("memory")
   protected final int memorySizeMb;
   @Named("disk")
   protected final int diskSizeGb;
   protected final Set<String> ips;
   protected final Date created;
   protected final Date updated;

   // metadata Object[String => String] Any "extra" metadata this machine has
   private final Map<String, JsonBall> metadata;

   @ConstructorProperties({ "id", "name", "type", "state", "dataset", "memory", "disk", "ips", "created", "updated", "metadata" })
   public Machine(String id, String name, Type type, State state, String dataset, int memorySizeMb, int diskSizeGb,
         Set<String> ips, Date created, Date updated, final Map<String, JsonBall> metadata) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name of machine(%s)", id);
      this.type = checkNotNull(type, "type of machine(%s)", id);
      this.state = checkNotNull(state, "state of machine(%s)", id);
      this.dataset = checkNotNull(dataset, "dataset of machine(%s)", id);
      this.memorySizeMb = memorySizeMb;
      this.diskSizeGb = diskSizeGb;
      this.ips = ImmutableSet.<String> copyOf(checkNotNull(ips, "ips of machine(%s)", id));
      this.created = checkNotNull(created, "created date of machine(%s)", id);
      this.updated = checkNotNull(created, "updated date of machine(%s)", id);
      this.metadata = ImmutableMap.<String, JsonBall> copyOf(checkNotNull(metadata, "metadata of machine(%s)", id));
   }

   /**
    * The globally unique id for this machine
    */
   public String getId() {
      return id;
   }

   /**
    * The "friendly" name for this machine
    */
   public String getName() {
      return name;
   }

   /**
    * Whether this is a smartmachine or virtualmachine
    */
   public Type getType() {
      return type;
   }

   /**
    * The current state of this machine
    */
   public State getState() {
      return state;
   }

   /**
    * The dataset urn this machine was provisioned with
    */
   public String getDatasetURN() {
      return dataset;
   }

   /**
    * The amount of memory this machine has (Mb)
    */
   public int getMemorySizeMb() {
      return memorySizeMb;
   }

   /**
    * The amount of disk this machine has (Gb)
    */
   public int getDiskSizeGb() {
      return diskSizeGb;
   }

   /**
    * The IP addresses this machine has
    */
   public Set<String> getIps() {
      return ips;
   }

   /**
    * When this machine was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * When this machine was updated
    */
   public Date getUpdated() {
      return updated;
   }

   /**
    * 
    * <h4>note</h4>
    * 
    * If the value is a string, it will be quoted, as that's how json strings are represented.
    * 
    * @return key to a json literal of the value
    * @see Metadata#valueType
    * @see Json#fromJson
    */
   public Map<String, String> getMetadataAsJsonLiterals() {
      return Maps.transformValues(metadata, Functions.toStringFunction());
   }

   /**
    * Any "extra" metadata this machine has
    * 
    * <h4>note</h4>
    * 
    * metadata can contain arbitrarily complex values. If the value has structure, you should use
    * {@link #getMetadataAsJsonLiterals}
    * 
    */
   public Map<String, String> getMetadata() {
      return Maps.transformValues(metadata, Functions.compose(Functions.toStringFunction(), unquoteString));
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
      if (object instanceof Machine) {
         Machine that = Machine.class.cast(object);
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
                    .add("name", name)
                    .add("type", type)
                    .add("state", state)
                    .add("memorySizeMb", memorySizeMb)
                    .add("diskSizeGb", diskSizeGb)
                    .add("ips", ips)
                    .add("created", created)
                    .add("updated", updated).toString();
   }
   
   @Override
   public int compareTo(Machine that) {
      return ComparisonChain.start()
                            .compare(this.name, that.name)
                            .compare(this.created, that.created)
                            .compare(this.id, that.id).result();
   }
}
