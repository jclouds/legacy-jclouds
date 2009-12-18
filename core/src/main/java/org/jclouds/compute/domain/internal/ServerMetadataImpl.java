/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.compute.domain.internal;

import java.net.InetAddress;
import java.util.Comparator;
import java.util.SortedSet;

import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.ServerMetadata;
import org.jclouds.compute.domain.ServerState;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ServerMetadataImpl extends ServerIdentityImpl implements ServerMetadata {
   public static final Comparator<InetAddress> ADDRESS_COMPARATOR = new Comparator<InetAddress>() {

      @Override
      public int compare(InetAddress o1, InetAddress o2) {
         return (o1 == o2) ? 0 : o1.getHostAddress().compareTo(o2.getHostAddress());
      }

   };
   private final ServerState state;
   private final SortedSet<InetAddress> publicAddresses = Sets.newTreeSet(ADDRESS_COMPARATOR);
   private final SortedSet<InetAddress> privateAddresses = Sets.newTreeSet(ADDRESS_COMPARATOR);
   private final int loginPort;
   private final LoginType loginType;

   public ServerMetadataImpl(String id, String name, ServerState state,
            Iterable<InetAddress> publicAddresses, Iterable<InetAddress> privateAddresses,
            int loginPort, LoginType loginType) {
      super(id, name);
      this.state = state;
      Iterables.addAll(this.publicAddresses, publicAddresses);
      Iterables.addAll(this.privateAddresses, privateAddresses);
      this.loginPort = loginPort;
      this.loginType = loginType;
   }

   /**
    * {@inheritDoc}
    */
   public SortedSet<InetAddress> getPublicAddresses() {
      return publicAddresses;
   }

   /**
    * {@inheritDoc}
    */
   public SortedSet<InetAddress> getPrivateAddresses() {
      return privateAddresses;
   }

   /**
    * {@inheritDoc}
    */
   public int getLoginPort() {
      return loginPort;
   }

   /**
    * {@inheritDoc}
    */
   public LoginType getLoginType() {
      return loginType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + loginPort;
      result = prime * result + ((loginType == null) ? 0 : loginType.hashCode());
      result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
      result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
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
      ServerMetadataImpl other = (ServerMetadataImpl) obj;
      if (loginPort != other.loginPort)
         return false;
      if (loginType == null) {
         if (other.loginType != null)
            return false;
      } else if (!loginType.equals(other.loginType))
         return false;
      if (privateAddresses == null) {
         if (other.privateAddresses != null)
            return false;
      } else if (!privateAddresses.equals(other.privateAddresses))
         return false;
      if (publicAddresses == null) {
         if (other.publicAddresses != null)
            return false;
      } else if (!publicAddresses.equals(other.publicAddresses))
         return false;
      return true;
   }

   public ServerState getState() {
      return state;
   }

}
