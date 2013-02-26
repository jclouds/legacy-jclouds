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
package org.jclouds.snia.cdmi.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * The base type for all objects in the CDMI model.
 * 
 * @author Kenneth Nagin
 */
public class CDMIObjectCapability {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromCDMIObject(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {
      private String objectID;
      private String objectType;
      private String objectName;
      private String parentURI;
      private String parentID;
      private String completionStatus;

      private Map<String, JsonBall> capabilities = Maps.newHashMap();

      /**
       * @see DataObject#getMetadata()
       */
      public B capabilities(Map<String, JsonBall> capabilities) {
         this.capabilities = ImmutableMap.copyOf(checkNotNull(capabilities, "capabilities"));
         return self();
      }

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see CDMIObjectCapability#getObjectID()
       */
      public B objectID(String objectID) {
         this.objectID = objectID;
         return self();
      }

      /**
       * @see CDMIObjectCapability#getObjectType()
       */
      public B objectType(String objectType) {
         this.objectType = objectType;
         return self();
      }

      /**
       * @see CDMIObjectCapability#getObjectName()
       */
      public B objectName(String objectName) {
         this.objectName = objectName;
         return self();
      }

      /**
       * @see CDMIObjectCapability#getParentURI()
       */
      public B parentURI(String parentURI) {
         this.parentURI = parentURI;
         return self();
      }

      /**
       * @see CDMIObjectCapability#getParentID()
       */
      public B parentID(String parentID) {
         this.parentID = parentID;
         return self();
      }

      /**
       * @see CDMIObjectCapability#getCompletionStatus()
       */
      public B completionStatus(String completionStatus) {
         this.completionStatus = completionStatus;
         return self();
      }

      public CDMIObjectCapability build() {
         return new CDMIObjectCapability(this);
      }

      protected B fromCDMIObject(CDMIObjectCapability in) {
         return objectID(in.getObjectID()).objectType(in.getObjectType()).objectName(in.getObjectName())
                  .parentURI(in.getParentURI()).capabilities(in.getCapabilities()).parentID(in.getParentID())
                  .completionStatus(in.getCompletionStatus());
      }
   }

   private final String objectID;
   private final String objectType;
   private final String objectName;
   private String parentURI;

   private String parentID;
   private String completionStatus;

   private final Map<String, JsonBall> capabilities;
   private Map<String, String> storageSystemCapabilities;
   private Map<String, String> cdmiGenericCapabilities;

   protected CDMIObjectCapability(Builder<?> builder) {
      this.objectID = checkNotNull(builder.objectID, "objectID");
      this.objectType = checkNotNull(builder.objectType, "objectType");
      this.objectName = builder.objectName;
      this.parentURI = checkNotNull(builder.parentURI, "parentURI");
      this.parentID = checkNotNull(builder.parentID, "parentID");
      this.completionStatus = checkNotNull(builder.completionStatus, "completionStatus");
      this.capabilities = ImmutableMap.copyOf(checkNotNull(builder.capabilities, "metadata"));
   }

   /**
    * Object ID of the object <br/>
    * Every object stored within a CDMI-compliant system shall have a globally
    * unique object identifier (ID) assigned at creation time. The CDMI object
    * ID is a string with requirements for how it is generated and how it
    * obtains its uniqueness. Each offering that implements CDMI is able to
    * produce these identifiers without conflicting with other offerings.
    * 
    * note: CDMI Servers do not always support ObjectID tags, however downstream
    * jclouds code does not handle null so we return a empty String instead.
    */
   public String getObjectID() {
      return (objectID == null) ? "" : objectID;
   }

   /**
    * 
    * type of the object
    */
   public String getObjectType() {
      return objectType;
   }

   /**
    * For objects in a container, the objectName field shall be returned. For
    * objects not in a container (objects that are only accessible by ID), the
    * objectName field shall not be returned.
    * 
    * Name of the object
    */
   @Nullable
   public String getObjectName() {
      return (objectName == null) ? "" : objectName;
   }

   /**
    * 
    * parent URI
    */
   public String getParentURI() {
      return parentURI;
   }

   /**
    * @return the parentID
    */
   public String getParentID() {
      return parentID;
   }

   /**
    * @return the completionStatus
    */
   public String getCompletionStatus() {
      return completionStatus;
   }

   /**
    * Get capabilities
    * 
    * @return capabilities as a map to JsonBall.
    */
   public Map<String, JsonBall> getCapabilities() {
      return capabilities;
   }

   /**
    * Parse Capabilities for the container object from the original JsonBall.
    * System capabilies are prefixed with cdmi. Storage system capabilities are
    * without cdmi prefix.
    * 
    */
   private void parseCapability() {
      storageSystemCapabilities = Maps.newHashMap();
      cdmiGenericCapabilities = Maps.newHashMap();
      Iterator<String> keys = capabilities.keySet().iterator();
      while (keys.hasNext()) {
         String key = keys.next();
         JsonBall value = capabilities.get(key);
         if (key.startsWith("cdmi")) {
            cdmiGenericCapabilities.put(key, value.toString().replace('"', ' ').trim());
         } else {
            storageSystemCapabilities.put(key, value.toString().replace('"', ' ').trim());
         }
      }
   }

   /**
    * Get storageSystemCapabilities for the container object.
    */
   public Map<String, String> getStorageSystemCapabilities() {
      if (storageSystemCapabilities == null) {
         parseCapability();
      }
      return storageSystemCapabilities;
   }

   /**
    * Get cdmiGenericCapabilities for the container object
    */
   public Map<String, String> getCdmiGenericCapabilities() {
      if (cdmiGenericCapabilities == null) {
         parseCapability();
      }
      return cdmiGenericCapabilities;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CDMIObjectCapability that = CDMIObjectCapability.class.cast(o);
      return equal(this.objectID, that.objectID) && equal(this.objectName, that.objectName)
               && equal(this.objectType, that.objectType) && equal(this.parentURI, that.parentURI)
               && equal(this.parentID, that.parentID) && equal(this.completionStatus, that.completionStatus)
               && equal(this.capabilities, that.capabilities);
   }

   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      CDMIObjectCapability that = CDMIObjectCapability.class.cast(o);
      return equal(this.objectType, that.objectType);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(objectID, objectName, objectType, parentURI, parentID, completionStatus, capabilities);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("objectID", objectID).add("objectName", objectName)
               .add("objectType", objectType).add("parentURI", parentURI).add("parentID", parentID)
               .add("completionStatus", completionStatus).add("capabilities", capabilities);
   }
}
