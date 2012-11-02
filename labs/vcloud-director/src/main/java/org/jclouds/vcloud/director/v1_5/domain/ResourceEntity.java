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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import java.util.Arrays;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Base type that represents a resource entity such as a vApp template or virtual media.
 *
 * <pre>
 * &lt;complexType name="ResourceEntity" /&gt;
 * </pre>
 *
 * @author danikov
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlType(name = "ResourceEntityType")
public abstract class ResourceEntity extends Entity {

   @XmlType(name = "ResourceEntityTypeStatus")
   @XmlEnum(Integer.class)
   public static enum Status {
      
      @XmlEnumValue("-1") FAILED_CREATION(-1, "The object could not be created.", true, true, true),
      @XmlEnumValue("0") UNRESOLVED(0, "The object is unresolved or not ready.", true, true, true),
      @XmlEnumValue("1") RESOLVED(1, "The object is resolved.", true, true, true),
      @XmlEnumValue("2") DEPLOYED(2, "The object is deployed.", false, false, false),
      @XmlEnumValue("3") SUSPENDED(3, "The object is suspended.", false, true, true),
      @XmlEnumValue("4") POWERED_ON(4, "The object is powered on.", false, true, true),
      @XmlEnumValue("5") WAITING_FOR_INPUT(5, "The object is waiting for user input.", false, true, true),
      @XmlEnumValue("6") UNKNOWN(6, "The object is in an unknown state.", true, true, true),
      @XmlEnumValue("7") UNRECOGNIZED(7, "The object is in an unrecognized state.", true, true, true),
      @XmlEnumValue("8") POWERED_OFF(8, "The object is powered off.", true, true, true),
      @XmlEnumValue("9") INCONSISTENT_STATE(9, "The object is in an inconsistent state.", false, true, true),
      @XmlEnumValue("10") MIXED(10, "Children do not all have the same status.", true, true, false),
      @XmlEnumValue("11") UPLOAD_OVF_PENDING(11, "Upload initiated, OVF descriptor pending.", true, false, false),
      @XmlEnumValue("12") UPLOAD_COPYING(12, "Upload initiated, copying contents.", true, false, false),
      @XmlEnumValue("13") UPLOAD_DISK_PENDING(13, "Upload initiated , disk contents pending.", true, false, false),
      @XmlEnumValue("14") UPLOAD_QUARANTINED(14, "Upload has been quarantined.", true, false, false),
      @XmlEnumValue("15") UPLOAD_QUARANTINE_EXPIRED(15, "Upload quarantine period has expired.", true, false, false),
      
      // Convention is "UNRECOGNIZED", but that is already a valid state name! so using UNRECOGNIZED_VALUE
      @XmlEnumValue("65535") UNRECOGNIZED_VALUE(65535, "Unrecognized", false, false, false);
      
      private Integer value;
      private String description;
      private boolean vAppTemplate;
      private boolean vApp;
      private boolean vm;
      
      private Status(int value, String description, boolean vAppTemplate, boolean vApp, boolean vm) {
         this.value = value;
         this.description = description;
         this.vAppTemplate = vAppTemplate;
         this.vApp = vApp;
         this.vm = vm;
      }

      public Integer getValue() {
         return value;
      }

      public String getDescription() {
         return description;
      }

      public boolean isVAppTemplate() {
         return vAppTemplate;
      }

      public boolean isVApp() {
         return vApp;
      }

      public boolean isVm() {
         return vm;
      }

      public static Status fromValue(final int value) {
         Optional<Status> found = Iterables.tryFind(Arrays.asList(values()), new Predicate<Status>() {
            @Override
            public boolean apply(Status status) {
               return status.getValue() == value;
            }
         });
         if (found.isPresent()) {
            return found.get();
         } else {
            logger.warn("Illegal status value '%d'", value);
            return UNRECOGNIZED_VALUE;
         }
      }
   }
       
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromResourceEntityType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {
      private Set<File> files;
      private Status status;

      /**
       * @see ResourceEntityType#getFiles()
       */
      public B files(Set<File> files) {
         this.files = files;
         return self();
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      public B status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      public B status(Integer status) {
         this.status = Status.fromValue(status);
         return self();
      }

      public B fromResourceEntityType(ResourceEntity in) {
         return fromEntityType(in).files(in.getFiles()).status(in.getStatus());
      }
   }

   @XmlElementWrapper(name = "Files")
   @XmlElement(name = "File")
   private Set<File> files;
   @XmlAttribute
   private Status status;

   public ResourceEntity(Builder<?> builder) {
      super(builder);
      this.files = builder.files;
      this.status = builder.status;
   }

   protected ResourceEntity() {
      // for JAXB
   }

   /**
    * Gets the value of the files property.
    */
   public Set<File> getFiles() {
      return files;
   }

   /**
    * Gets the value of the status property.
    */
   public Status getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntity that = ResourceEntity.class.cast(o);
      return super.equals(that) && equal(this.files, that.files) && equal(this.status, that.status);
   }
   
   @Override
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntity that = ResourceEntity.class.cast(o);
      return super.clone(that) && equal(this.files, that.files);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), files, status);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("files", files).add("status", status);
   }

}
