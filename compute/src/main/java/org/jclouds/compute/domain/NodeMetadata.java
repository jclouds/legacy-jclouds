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

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Credentials;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
@ImplementedBy(NodeMetadataImpl.class)
public interface NodeMetadata extends ComputeMetadata {
   /**
    * <h4>note</h4> hostname is something that is set in the operating system
    * image, so this value, if present, cannot be guaranteed on images not
    * directly controlled by the cloud provider.
    * 
    * @return hostname of the node, or null if unknown
    * 
    */
   @Nullable
   String getHostname();
   
   /**
    * Tag used for all resources that belong to the same logical group. run, destroy commands are
    * scoped to group.
    * 
    * @return group for this node, or null, if not a part of a group
    * 
    */
   @Nullable
   String getGroup();

   /**
    * @see #getGroup
    */
   @Deprecated
   String getTag();

   /**
    * 
    * The hardware this node is running, if possible to determine.
    */
   @Nullable
   Hardware getHardware();

   /**
    * 
    * The id of the image this node was created from, if possible to correlate.
    */
   @Nullable
   String getImageId();

   /**
    * 
    * The operating system this node is running, if possible to determine.
    */
   @Nullable
   OperatingSystem getOperatingSystem();

   /**
    * Current State of the node
    */
   NodeState getState();

   /**
    * @return the TCP port used for terminal connections. Generally, this is port 22 for ssh.
    */
   int getLoginPort();

   /**
    * secures access to root with a password. This password is required to access either the console
    * or run sudo as root.
    * <p/>
    * ex. {@code echo 'password' |sudo -S command}
    * 
    * @return root or console password, if configured, or null.
    */
   @Nullable
   String getAdminPassword();

   /**
    * If possible, these are returned upon all detail requests. However, it is often the case that
    * credentials are only available at "run" time.
    */
   Credentials getCredentials();

   /**
    * All public IP addresses, potentially including shared ips.
    */
   Set<String> getPublicAddresses();

   /**
    * All private IP addresses.
    */
   Set<String> getPrivateAddresses();

}