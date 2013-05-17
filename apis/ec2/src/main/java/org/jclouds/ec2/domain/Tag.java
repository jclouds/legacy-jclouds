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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * To help you manage your Amazon EC2 instances, images, and other Amazon EC2
 * resources, you can assign your own metadata to each resource in the form of
 * tags.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/UserGuide/Using_Tags.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Tag {

   /**
    * Describes the well-known resource types that can be tagged.
    */
   public static interface ResourceType {
      public static final String CUSTOMER_GATEWAY = "customer-gateway";
      public static final String DHCP_OPTIONS = "dhcp-options";
      public static final String IMAGE = "image";
      public static final String INSTANCE = "instance";
      public static final String INTERNET_GATEWAY = "internet-gateway";
      public static final String NETWORK_ACL = "network-acl";
      public static final String RESERVED_INSTANCES = "reserved-instances";
      public static final String ROUTE_TABLE = "route-table";
      public static final String SECURITY_GROUP = "security-group";
      public static final String SNAPSHOT = "snapshot";
      public static final String SPOT_INSTANCES_REQUEST = "spot-instances-request";
      public static final String SUBNET = "subnet";
      public static final String VOLUME = "volume";
      public static final String VPC = "vpc";
      public static final String VPN_CONNECTION = "vpn-connection";
      public static final String VPN_GATEWAY = "vpn-gateway";
   }
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromTag(this);
   }

   public static class Builder {

      protected String resourceId;
      protected String resourceType;
      protected String key;
      protected Optional<String> value = Optional.absent();

      /**
       * @see Tag#getResourceId()
       */
      public Builder resourceId(String resourceId) {
         this.resourceId = resourceId;
         return this;
      }

      /**
       * @see Tag#getResourceType()
       */
      public Builder resourceType(String resourceType) {
         this.resourceType = resourceType;
         return this;
      }

      /**
       * @see Tag#getKey()
       */
      public Builder key(String key) {
         this.key = key;
         return this;
      }

      /**
       * @see TagGroup#getValue()
       */
      public Builder value(String value) {
         this.value = Optional.fromNullable(value);
         return this;
      }

      public Tag build() {
         return new Tag(resourceId, resourceType, key, value);
      }

      public Builder fromTag(Tag in) {
         return this.resourceId(in.getResourceId()).resourceType(in.getResourceType()).key(in.getKey())
               .value(in.getValue().orNull());
      }
   }

   protected final String resourceId;
   protected final String resourceType;
   protected final String key;
   protected final Optional<String> value;

   protected Tag(String resourceId, String resourceType, String key, Optional<String> value) {
      this.resourceId = checkNotNull(resourceId, "resourceId");
      this.resourceType = checkNotNull(resourceType, "resourceType");
      this.key = checkNotNull(key, "key");
      this.value = checkNotNull(value, "value");
   }

   /**
    * The resource ID ex. i-erf235
    */
   public String getResourceId() {
      return resourceId;
   }

   /**
    * The resource type. ex. customer-gateway, dhcp-options, image, instance,
    * internet-gateway, network-acl, reserved-instances, route-table,
    * security-group, snapshot, spot-instances-request, subnet, volume, vpc,
    * vpn-connection, vpn-gateway
    */
   public String getResourceType() {
      return resourceType;
   }

   /**
    * The tag key.
    */
   public String getKey() {
      return key;
   }

   /**
    * The tag value.
    */
   public Optional<String> getValue() {
      return value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(resourceId, key);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Tag other = (Tag) obj;
      return Objects.equal(this.resourceId, other.resourceId) && Objects.equal(this.key, other.key);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("resourceId", resourceId)
            .add("resourceType", resourceType).add("key", key).add("value", value.orNull());
   }

}
