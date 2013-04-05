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
package org.jclouds.compute.domain;

import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

import com.google.inject.ImplementedBy;
import org.jclouds.management.annotations.ManagedAttribute;
import org.jclouds.management.annotations.ManagedType;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
@ImplementedBy(NodeMetadataImpl.class)
@ManagedType
public interface NodeMetadata extends ComputeMetadataIncludingStatus<NodeMetadata.Status> {
   
   public static enum Status {
      /**
       * The node is in transition
       */
      PENDING,
      /**
       * The node is visible, and in the process of being deleted.
       */
      TERMINATED,
      /**
       * The node is deployed, but suspended or stopped.
       */
      SUSPENDED,
      /**
       * The node is available for requests
       */
      RUNNING,
      /**
       * There is an error on the node
       */
      ERROR,
      /**
       * The state of the node is unrecognized.
       */
      UNRECOGNIZED;

   }
   
   /**
    * <h4>note</h4> hostname is something that is set in the operating system image, so this value,
    * if present, cannot be guaranteed on images not directly controlled by the cloud provider.
    * 
    * @return hostname of the node, or null if unknown
    * 
    */
   @Nullable
   @ManagedAttribute( description = "The hostname of the node")
   String getHostname();

   /**
    * Tag used for all resources that belong to the same logical group. run, destroy commands are
    * scoped to group.
    * 
    * @return group for this node, or null, if not a part of a group
    * 
    */
   @Nullable
   @ManagedAttribute( description = "The group of the node")
   String getGroup();

   /**
    * 
    * The hardware this node is running, if possible to determine.
    */
   @Nullable
   @ManagedAttribute( description = "The hardware of the node")
   Hardware getHardware();

   /**
    * 
    * The id of the image this node was created from, if possible to correlate.
    */
   @Nullable
   @ManagedAttribute( description = "The id of the image the node is running")
   String getImageId();

   /**
    * 
    * The operating system this node is running, if possible to determine.
    */
   @Nullable
   @ManagedAttribute( description = "The operating system the node is running")
   OperatingSystem getOperatingSystem();

   /**
    * @return the TCP port used for terminal connections. Generally, this is port 22 for ssh.
    */
   @ManagedAttribute( description = "The login port of the node")
   int getLoginPort();

   /**
    * If possible, these are returned upon all detail requests. However, it is often the case that
    * credentials are only available when a node is initially created.
    * 
    * @see ComputeServiceContext#credentialStore
    */
   @Nullable
   LoginCredentials getCredentials();

   /**
    * All public IP addresses, potentially including shared ips.
    */
   @ManagedAttribute( description = "The set of public addresses assigned to the node")
   Set<String> getPublicAddresses();

   /**
    * All private IP addresses.
    */
   @ManagedAttribute( description = "The set of private addresses assigned to the node")
   Set<String> getPrivateAddresses();

}
