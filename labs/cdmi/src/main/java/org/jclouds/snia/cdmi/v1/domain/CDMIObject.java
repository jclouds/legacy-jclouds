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
import java.util.List;
import java.util.Map;

import org.jclouds.domain.JsonBall;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The base type for all objects in the CDMI model.
 * 
 * @author Kenneth Nagin
 */
public class CDMIObject {

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

      private Map<String, JsonBall> metadata = Maps.newHashMap();

      /**
       * @see DataObject#getMetadata()
       */
      public B metadata(Map<String, JsonBall> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
         return self();
      }

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see CDMIObject#getObjectID()
       */
      public B objectID(String objectID) {
         this.objectID = objectID;
         return self();
      }

      /**
       * @see CDMIObject#getObjectType()
       */
      public B objectType(String objectType) {
         this.objectType = objectType;
         return self();
      }

      /**
       * @see CDMIObject#getObjectName()
       */
      public B objectName(String objectName) {
         this.objectName = objectName;
         return self();
      }

      /**
       * @see CDMIObject#getParentURI()
       */
      public B parentURI(String parentURI) {
         this.parentURI = parentURI;
         return self();
      }

      public CDMIObject build() {
         return new CDMIObject(this);
      }

      protected B fromCDMIObject(CDMIObject in) {
         return objectID(in.getObjectID()).objectType(in.getObjectType()).objectName(in.getObjectName())
                  .parentURI(in.getParentURI()).metadata(in.getMetadata());
      }
   }

   private final String objectID;
   private final String objectType;
   private final String objectName;
   private String parentURI;
   private final Map<String, JsonBall> metadata;
   private Map<String, String> userMetaDataIn;
   private Map<String, String> systemMetaDataIn;
   private List<Map<String, String>> aclMetaDataIn;

   protected CDMIObject(Builder<?> builder) {
      this.objectID = checkNotNull(builder.objectID, "objectID");
      this.objectType = checkNotNull(builder.objectType, "objectType");
      this.objectName = builder.objectName;
      this.parentURI = checkNotNull(builder.parentURI, "parentURI");
      this.metadata = ImmutableMap.copyOf(checkNotNull(builder.metadata, "metadata"));
   }

   /**
    * Object ID of the object <br/>
    * Every object stored within a CDMI-compliant system shall have a globally unique object
    * identifier (ID) assigned at creation time. The CDMI object ID is a string with requirements
    * for how it is generated and how it obtains its uniqueness. Each offering that implements CDMI
    * is able to produce these identifiers without conflicting with other offerings.
    * 
    * note: CDMI Servers do not always support ObjectID tags, however downstream jclouds code does
    * not handle null so we return a empty String instead.
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
    * For objects in a container, the objectName field shall be returned. For objects not in a
    * container (objects that are only accessible by ID), the objectName field shall not be
    * returned.
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
    * Metadata for the CDMI object. This field includes any user and system metadata specified in
    * the request body metadata field, along with storage system metadata generated by the cloud
    * storage system.
    */
   public Map<String, JsonBall> getMetadata() {
      return metadata;
   }

   /**
    * Parse Metadata for the container object from the original JsonBall. System metadata data is
    * prefixed with cdmi. System ACL metadata data is prefixed with cdmi_acl
    * 
    */
   private void parseMetadata() {
      userMetaDataIn = Maps.newHashMap();
      systemMetaDataIn = Maps.newHashMap();
      aclMetaDataIn = Lists.newArrayList();
      for (Map.Entry<String, JsonBall> entry : metadata.entrySet()) {
         String key = entry.getKey();
         JsonBall value = entry.getValue();
         if (key.startsWith("cdmi")) {
            if (key.matches("cdmi_acl")) {
               String[] cdmi_acl_array = value.toString().split("[{}]");
               for (int i = 0; i < cdmi_acl_array.length; i++) {
                  if (!(cdmi_acl_array[i].startsWith("[") || cdmi_acl_array[i].startsWith("]") || cdmi_acl_array[i]
                           .startsWith(","))) {
                     Map<String, String> aclMap = Maps.newHashMap();
                     String[] cdmi_acl_member = cdmi_acl_array[i].split(",");
                     for (String s : cdmi_acl_member) {
                        String cdmi_acl_key = s.substring(0, s.indexOf(":"));
                        String cdmi_acl_value = s.substring(s.indexOf(":") + 1);
                        cdmi_acl_value.replace('"', ' ').trim();
                        aclMap.put(cdmi_acl_key, cdmi_acl_value);
                     }
                     aclMetaDataIn.add(aclMap);
                  }
               }
            } else {
               systemMetaDataIn.put(key, value.toString().replace('"', ' ').trim());
            }
         } else {
            userMetaDataIn.put(key, value.toString().replace('"', ' ').trim());
         }
      }
   }

   /**
    * Get User Metadata for the container object. This field includes any user metadata
    */
   public Map<String, String> getUserMetadata() {
      if (userMetaDataIn == null) {
         parseMetadata();
      }
      return userMetaDataIn;
   }

   /**
    * Get System Metadata for the container object excluding ACL related metadata
    */
   public Map<String, String> getSystemMetadata() {
      if (systemMetaDataIn == null) {
         parseMetadata();
      }
      return systemMetaDataIn;
   }

   /**
    * Get System Metadata for the container object excluding ACL related metadata
    */
   public List<Map<String, String>> getACLMetadata() {
      if (aclMetaDataIn == null) {
         parseMetadata();
      }
      return aclMetaDataIn;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CDMIObject that = CDMIObject.class.cast(o);
      return equal(this.objectID, that.objectID) && equal(this.objectName, that.objectName)
               && equal(this.objectType, that.objectType) && equal(this.parentURI, that.parentURI)
               && equal(this.metadata, that.metadata);
   }

   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      CDMIObject that = CDMIObject.class.cast(o);
      return equal(this.objectType, that.objectType);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(objectID, objectName, objectType, parentURI, metadata);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("objectID", objectID).add("objectName", objectName)
               .add("objectType", objectType).add("parentURI", parentURI).add("metadata", metadata);
   }
}
