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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class TemplateMetadata
 *
 * @author Richard Downer
 */
public class TemplateMetadata {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTemplateMetadata(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String name;
      protected String osTypeId;
      protected String displayText;
      protected String snapshotId;
      protected String volumeId;
      protected String virtualMachineId;
      protected Boolean passwordEnabled;

      /**
       * @see TemplateMetadata#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see TemplateMetadata#getOsTypeId()
       */
      public T osTypeId(String osTypeId) {
         this.osTypeId = osTypeId;
         return self();
      }

      /**
       * @see TemplateMetadata#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see TemplateMetadata#getSnapshotId()
       */
      public T snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return self();
      }

      /**
       * @see TemplateMetadata#getVolumeId()
       */
      public T volumeId(String volumeId) {
         this.volumeId = volumeId;
         return self();
      }

      /**
       * @see TemplateMetadata#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see TemplateMetadata#isPasswordEnabled()
       */
      public T passwordEnabled(Boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return self();
      }

      public TemplateMetadata build() {
         return new TemplateMetadata(name, osTypeId, displayText, snapshotId, volumeId, virtualMachineId, passwordEnabled);
      }

      public T fromTemplateMetadata(TemplateMetadata in) {
         return this
               .name(in.getName())
               .osTypeId(in.getOsTypeId())
               .displayText(in.getDisplayText())
               .snapshotId(in.getSnapshotId())
               .volumeId(in.getVolumeId())
               .virtualMachineId(in.getVirtualMachineId())
               .passwordEnabled(in.isPasswordEnabled());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final String osTypeId;
   private final String displayText;
   private final String snapshotId;
   private final String volumeId;
   private final String virtualMachineId;
   private final Boolean passwordEnabled;

   @ConstructorProperties({
         "name", "osTypeId", "displayText", "snapshotId", "volumeId", "virtualMachineId", "passwordEnabled"
   })
   protected TemplateMetadata(String name, @Nullable String osTypeId, @Nullable String displayText, @Nullable String snapshotId,
                              @Nullable String volumeId, String virtualMachineId, Boolean passwordEnabled) {
      this.name = checkNotNull(name, "name");
      this.osTypeId = osTypeId;
      this.displayText = displayText;
      this.snapshotId = snapshotId;
      this.volumeId = volumeId;
      this.virtualMachineId = virtualMachineId;
      this.passwordEnabled = passwordEnabled;
   }

   /**
    * @return the name of the template
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the ID of the OS Type that best represents the OS of this template.
    */
   @Nullable
   public String getOsTypeId() {
      return this.osTypeId;
   }

   /**
    * @return the display text of the template. This is usually used for display purposes.
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   /**
    * @return the ID of the snapshot the template is being created from
    */
   @Nullable
   public String getSnapshotId() {
      return this.snapshotId;
   }

   /**
    * @return the ID of the disk volume the template is being created from
    */
   @Nullable
   public String getVolumeId() {
      return this.volumeId;
   }

   /**
    * @return Optional, VM ID
    */
   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   @Nullable
   public Boolean isPasswordEnabled() {
      return this.passwordEnabled;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, osTypeId, displayText, snapshotId, volumeId, virtualMachineId, passwordEnabled);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TemplateMetadata that = TemplateMetadata.class.cast(obj);
      return Objects.equal(this.name, that.name)
            && Objects.equal(this.osTypeId, that.osTypeId)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.snapshotId, that.snapshotId)
            && Objects.equal(this.volumeId, that.volumeId)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.passwordEnabled, that.passwordEnabled);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name).add("osTypeId", osTypeId).add("displayText", displayText).add("snapshotId", snapshotId)
            .add("volumeId", volumeId).add("virtualMachineId", virtualMachineId).add("passwordEnabled", passwordEnabled);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
