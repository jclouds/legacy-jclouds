/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.network;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;

import java.util.List;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Adds generic high level functionality to {@link VLANNetworkDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public abstract class Network<T extends Ip<?, ?>> extends DomainWrapper<VLANNetworkDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Network(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final VLANNetworkDto target) {
      super(context, target);
   }

   // Domain operations

   public abstract void save();

   public abstract void update();

   public abstract void delete();

   public abstract List<T> listIps(IpOptions options);

   public abstract T getIp(Integer id);

   public List<T> listIps() {
      // Disable pagination by default
      return listIps(IpOptions.builder().disablePagination().build());
   }

   public List<T> listIps(final Predicate<T> filter) {
      return Lists.newLinkedList(filter(listIps(), filter));
   }

   public T findIp(final Predicate<T> filter) {
      return Iterables.getFirst(filter(listIps(), filter), null);
   }

   public List<T> listUnusedIps() {
      return listIps(IpPredicates.<T> notUsed());
   }

   public T findUnusedIp(final Predicate<T> filter) {
      return Iterables.getFirst(filter(listUnusedIps(), filter), null);
   }

   // Builder

   public static class NetworkBuilder<T extends NetworkBuilder<T>> {
      protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

      protected String name;

      protected Integer tag;

      protected String gateway;

      protected String address;

      protected Integer mask;

      protected String primaryDNS;

      protected String secondaryDNS;

      protected String sufixDNS;

      protected Boolean defaultNetwork;

      public NetworkBuilder(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
         super();
         this.context = context;
      }

      @SuppressWarnings("unchecked")
      public T name(final String name) {
         this.name = name;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T tag(final Integer tag) {
         this.tag = tag;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T gateway(final String gateway) {
         this.gateway = gateway;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T address(final String address) {
         this.address = address;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T mask(final int mask) {
         this.mask = mask;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T primaryDNS(final String primaryDNS) {
         this.primaryDNS = primaryDNS;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T secondaryDNS(final String secondaryDNS) {
         this.secondaryDNS = secondaryDNS;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T sufixDNS(final String sufixDNS) {
         this.sufixDNS = sufixDNS;
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T defaultNetwork(final Boolean defaultNetwork) {
         this.defaultNetwork = defaultNetwork;
         return (T) this;
      }
   }

   public PrivateNetwork toPrivateNetwork() {
      checkArgument(target.getType().equals(NetworkType.INTERNAL),
            ValidationErrors.INVALID_NETWORK_TYPE + target.getType());

      return wrap(context, PrivateNetwork.class, target);

   }

   public ExternalNetwork toExternalNetwork() {
      checkArgument(target.getType().equals(NetworkType.EXTERNAL),
            ValidationErrors.INVALID_NETWORK_TYPE + target.getType());

      return wrap(context, ExternalNetwork.class, target);

   }

   public PublicNetwork toPublicNetwork() {
      checkArgument(target.getType().equals(NetworkType.PUBLIC),
            ValidationErrors.INVALID_NETWORK_TYPE + target.getType());

      return wrap(context, PublicNetwork.class, target);

   }

   public UnmanagedNetwork toUnmanagedNetwork() {
      checkArgument(target.getType().equals(NetworkType.UNMANAGED),
            ValidationErrors.INVALID_NETWORK_TYPE + target.getType());

      return wrap(context, UnmanagedNetwork.class, target);

   }

   // Delegate methods

   public String getAddress() {
      return target.getAddress();
   }

   public Boolean getDefaultNetwork() {
      return target.getDefaultNetwork();
   }

   public String getGateway() {
      return target.getGateway();
   }

   public Integer getId() {
      return target.getId();
   }

   public Integer getMask() {
      return target.getMask();
   }

   public String getName() {
      return target.getName();
   }

   public String getPrimaryDNS() {
      return target.getPrimaryDNS();
   }

   public String getSecondaryDNS() {
      return target.getSecondaryDNS();
   }

   public String getSufixDNS() {
      return target.getSufixDNS();
   }

   public Integer getTag() {
      return target.getTag();
   }

   public NetworkType getType() {
      return target.getType();
   }

   public void setAddress(final String address) {
      target.setAddress(address);
   }

   public void setDefaultNetwork(final Boolean defaultNetwork) {
      target.setDefaultNetwork(defaultNetwork);
   }

   public void setGateway(final String gateway) {
      target.setGateway(gateway);
   }

   public void setMask(final Integer mask) {
      target.setMask(mask);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setPrimaryDNS(final String primaryDNS) {
      target.setPrimaryDNS(primaryDNS);
   }

   public void setSecondaryDNS(final String secondaryDNS) {
      target.setSecondaryDNS(secondaryDNS);
   }

   public void setSufixDNS(final String sufixDNS) {
      target.setSufixDNS(sufixDNS);
   }

   public void setTag(final Integer tag) {
      target.setTag(tag);
   }

   @Override
   public String toString() {
      return "Network [id=" + getId() + ", address=" + getAddress() + ", defaultNetwork=" + getDefaultNetwork()
            + ", gateway=" + getGateway() + ", mask=" + getMask() + ", name=" + getName() + ", primaryDNS="
            + getPrimaryDNS() + ", secondaryDNS=" + getSecondaryDNS() + ", suffixDNS=" + getSufixDNS() + ", tag="
            + getTag() + ", type=" + getType() + "]";
   }

   public static Network<?> wrapNetwork(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final VLANNetworkDto dto) {
      if (dto == null) {
         return null;
      }

      Network<?> network = null;

      switch (dto.getType()) {
         case EXTERNAL:
            network = wrap(context, ExternalNetwork.class, dto);
            break;
         case EXTERNAL_UNMANAGED:
            // TODO: How do we manage External && unmanaged networks ?
            throw new UnsupportedOperationException("EXTERNAL_UNMANAGED networks not supported yet");
         case INTERNAL:
            network = wrap(context, PrivateNetwork.class, dto);
            break;
         case PUBLIC:
            network = wrap(context, PublicNetwork.class, dto);
            break;
         case UNMANAGED:
            network = wrap(context, UnmanagedNetwork.class, dto);
            break;
      }

      return network;
   }

   public static List<Network<?>> wrapNetworks(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final List<VLANNetworkDto> dtos) {
      List<Network<?>> networks = Lists.newLinkedList();
      for (VLANNetworkDto dto : dtos) {
         networks.add(wrapNetwork(context, dto));
      }
      return networks;
   }
}
