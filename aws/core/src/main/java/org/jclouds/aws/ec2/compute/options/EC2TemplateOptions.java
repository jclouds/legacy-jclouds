/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.util.Utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation on the "ec2" provider.
 * <h2>
 * Usage</h2> The recommended way to instantiate a EC2TemplateOptions object is to statically import
 * EC2TemplateOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.compute.options.EC2TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set<? extends NodeMetadata> set = client.runNodesWithTag(tag, 2, templateBuilder.build());
 * <code>
 * 
 * @author Adrian Cole
 */
public class EC2TemplateOptions extends TemplateOptions {

   private Set<String> groupIds = ImmutableSet.of();
   private String keyPair = null;
   private boolean noKeyPair;
   private String placementGroup = null;
   private boolean noPlacementGroup;
   private String subnetId;

   public static final EC2TemplateOptions NONE = new EC2TemplateOptions();

   /**
    * 
    * @see EC2TemplateOptions#securityGroups(Iterable<String>)
    */
   public EC2TemplateOptions securityGroups(String... groupIds) {
      return securityGroups(ImmutableSet.copyOf(groupIds));
   }

   /**
    * Specifies the security groups to be used for nodes with this template
    */
   public EC2TemplateOptions securityGroups(Iterable<String> groupIds) {
      checkArgument(Iterables.size(groupIds) > 0, "you must specify at least one security group");
      for (String groupId : groupIds)
         Utils.checkNotEmpty(groupId, "all security groups must be non-empty");
      this.groupIds = ImmutableSet.copyOf(groupIds);
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public EC2TemplateOptions keyPair(String keyPair) {
      checkNotNull(keyPair, "use noKeyPair option to request boot without a keypair");
      checkState(!noKeyPair, "you cannot specify both options keyPair and noKeyPair");
      Utils.checkNotEmpty(keyPair, "keypair must be non-empty");
      this.keyPair = keyPair;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public EC2TemplateOptions noKeyPair() {
      checkState(keyPair == null, "you cannot specify both options keyPair and noKeyPair");
      this.noKeyPair = true;
      return this;
   }

   /**
    * Specifies the keypair used to run instances with
    */
   public EC2TemplateOptions placementGroup(String placementGroup) {
      checkNotNull(placementGroup, "use noPlacementGroup option to request boot without a keypair");
      checkState(!noPlacementGroup, "you cannot specify both options placementGroup and noPlacementGroup");
      Utils.checkNotEmpty(placementGroup, "placementGroup must be non-empty");
      this.placementGroup = placementGroup;
      return this;
   }

   /**
    * Do not use a keypair on instances
    */
   public EC2TemplateOptions noPlacementGroup() {
      checkState(placementGroup == null, "you cannot specify both options placementGroup and noPlacementGroup");
      this.noPlacementGroup = true;
      return this;
   }

   /**
    * Specifies the subnetId used to run instances in
    */
   public EC2TemplateOptions subnetId(String subnetId) {
      checkNotNull(subnetId, "subnetId cannot be null");
      Utils.checkNotEmpty(subnetId, "subnetId must be non-empty");
      this.subnetId = subnetId;
      return this;
   }

   public static class Builder {

      /**
       * @see EC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static EC2TemplateOptions securityGroups(String... groupIds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupIds));
      }

      /**
       * @see EC2TemplateOptions#securityGroups(Iterable<String>)
       */
      public static EC2TemplateOptions securityGroups(Iterable<String> groupIds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.securityGroups(groupIds));
      }

      /**
       * @see EC2TemplateOptions#keyPair
       */
      public static EC2TemplateOptions keyPair(String keyPair) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.keyPair(keyPair));
      }

      /**
       * @see EC2TemplateOptions#noKeyPair
       */
      public static EC2TemplateOptions noKeyPair() {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.noKeyPair());
      }

      /**
       * @see EC2TemplateOptions#placementGroup
       */
      public static EC2TemplateOptions placementGroup(String placementGroup) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.placementGroup(placementGroup));
      }

      /**
       * @see EC2TemplateOptions#noPlacementGroup
       */
      public static EC2TemplateOptions noPlacementGroup() {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.noPlacementGroup());
      }

      // methods that only facilitate returning the correct object type
      /**
       * @see TemplateOptions#inboundPorts
       */
      public static EC2TemplateOptions inboundPorts(int... ports) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#port
       */
      public static EC2TemplateOptions blockOnPort(int port, int seconds) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static EC2TemplateOptions runScript(byte[] script) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.runScript(script));
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static EC2TemplateOptions installPrivateKey(String rsaKey) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.installPrivateKey(rsaKey));
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static EC2TemplateOptions authorizePublicKey(String rsaKey) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.authorizePublicKey(rsaKey));
      }

      /**
       * @see TemplateOptions#withDetails
       */
      public static EC2TemplateOptions withDetails() {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.withMetadata());
      }

      /**
       * @see TemplateOptions#withSubnetId
       */
      public static EC2TemplateOptions subnetId(String subnetId) {
         EC2TemplateOptions options = new EC2TemplateOptions();
         return EC2TemplateOptions.class.cast(options.subnetId(subnetId));
      }

   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#authorizePublicKey
    */
   @Override
   public EC2TemplateOptions authorizePublicKey(String publicKey) {
      return EC2TemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#blockOnPort
    */
   @Override
   public EC2TemplateOptions blockOnPort(int port, int seconds) {
      return EC2TemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * 
    * special thing is that we do assume if you are passing groups that you have everything you need
    * already defined. for example, our option inboundPorts normally creates ingress rules
    * accordingly but if we notice you've specified securityGroups, we do not mess with rules at all
    * 
    * @see TemplateOptions#inboundPorts
    */
   @Override
   public EC2TemplateOptions inboundPorts(int... ports) {
      return EC2TemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#installPrivateKey
    */
   @Override
   public EC2TemplateOptions installPrivateKey(String privateKey) {
      return EC2TemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * @see TemplateOptions#runScript
    */
   @Override
   public EC2TemplateOptions runScript(byte[] script) {
      return EC2TemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * @see TemplateOptions#withMetadata
    */
   @Override
   public EC2TemplateOptions withMetadata() {
      return EC2TemplateOptions.class.cast(super.withMetadata());
   }

   /**
    * @return groupIds the user specified to run instances with, or zero length set to create an
    *         implicit group
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * @return keyPair to use when running the instance or null, to generate a keypair.
    */
   public String getKeyPair() {
      return keyPair;
   }

   /**
    * @return true (default) if we are supposed to use a keypair
    */
   public boolean shouldAutomaticallyCreateKeyPair() {
      return !noKeyPair;
   }

   /**
    * @return placementGroup to use when running the instance or null, to generate a placementGroup.
    */
   public String getPlacementGroup() {
      return placementGroup;
   }

   /**
    * @return true (default) if we are supposed to use a placementGroup
    */
   public boolean shouldAutomaticallyCreatePlacementGroup() {
      return !noPlacementGroup;
   }

   /**
    * @return subnetId to use when running the instance or null.
    */
   public String getSubnetId() {
      return subnetId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((keyPair == null) ? 0 : keyPair.hashCode());
      result = prime * result + (noKeyPair ? 1231 : 1237);
      result = prime * result + (noPlacementGroup ? 1231 : 1237);
      result = prime * result + ((placementGroup == null) ? 0 : placementGroup.hashCode());
      result = prime * result + ((subnetId == null) ? 0 : subnetId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      EC2TemplateOptions other = (EC2TemplateOptions) obj;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (keyPair == null) {
         if (other.keyPair != null)
            return false;
      } else if (!keyPair.equals(other.keyPair))
         return false;
      if (noKeyPair != other.noKeyPair)
         return false;
      if (noPlacementGroup != other.noPlacementGroup)
         return false;
      if (placementGroup == null) {
         if (other.placementGroup != null)
            return false;
      } else if (!placementGroup.equals(other.placementGroup))
         return false;
      if (subnetId == null) {
         if (other.subnetId != null)
            return false;
      } else if (!subnetId.equals(other.subnetId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[groupIds=" + groupIds + ", keyPair=" + keyPair + ", noKeyPair=" + noKeyPair + ", placementGroup="
               + placementGroup + ", noPlacementGroup=" + noPlacementGroup + ", inboundPorts="
               + Arrays.toString(inboundPorts) + ", privateKey=" + (privateKey != null) + ", publicKey="
               + (publicKey != null) + ", runScript=" + (script != null) + ", port:seconds=" + port + ":" + seconds
               + ", subnetId=" + subnetId + ", metadata/details: " + includeMetadata + "]";
   }

}
