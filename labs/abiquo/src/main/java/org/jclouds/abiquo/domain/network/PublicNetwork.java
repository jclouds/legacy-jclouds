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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

/**
 * Adds high level functionality to public {@link VLANNetworkDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Public+Network+Resource"
 *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource</a>
 */
@EnterpriseEdition
public class PublicNetwork extends Network<PublicIp> {
   /** The datacenter where the network belongs. */
   private Datacenter datacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected PublicNetwork(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final VLANNetworkDto target) {
      super(context, target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-DeleteaPublicNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -DeleteaPublicNetwork</a>
    */
   @Override
   public void delete() {
      context.getApi().getInfrastructureApi().deleteNetwork(target);
      target = null;
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-CreateanewPublicNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -CreateanewPublicNetwork</a>
    */
   @Override
   public void save() {
      target = context.getApi().getInfrastructureApi().createNetwork(datacenter.unwrap(), target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-UpdateaPublicNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -UpdateaPublicNetwork</a>
    */
   @Override
   public void update() {
      target = context.getApi().getInfrastructureApi().updateNetwork(target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+IPs+Resource#PublicIPsResource-ReturnthelistofIPsforaPublicNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+IPs+Resource#
    *      PublicIPsResource- ReturnthelistofIPsforaPublicNetwork</a>
    */
   @Override
   public List<PublicIp> listIps(final IpOptions options) {
      PublicIpsDto ips = context.getApi().getInfrastructureApi().listPublicIps(target, options);
      return wrap(context, PublicIp.class, ips.getCollection());
   }

   @Override
   public PublicIp getIp(final Integer id) {
      PublicIpDto ip = context.getApi().getInfrastructureApi().getPublicIp(target, id);
      return wrap(context, PublicIp.class, ip);
   }

   // Parent access

   public Datacenter getDatacenter() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.DATACENTER), ValidationErrors.MISSING_REQUIRED_LINK
            + " " + ParentLinkName.DATACENTER);

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<DatacenterDto> parser = new ParseXMLWithJAXB<DatacenterDto>(utils.getXml(),
            TypeLiteral.get(DatacenterDto.class));

      datacenter = wrap(context, Datacenter.class, parser.apply(response));
      return datacenter;
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   public static class Builder extends NetworkBuilder<Builder> {
      private Datacenter datacenter;

      private Optional<NetworkServiceType> networkServiceType = Optional.absent();

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super(context);
         this.datacenter = checkNotNull(datacenter,
               ValidationErrors.NULL_RESOURCE + Datacenter.class.getCanonicalName());
         this.context = context;
      }

      public Builder datacenter(final Datacenter datacenter) {
         this.datacenter = checkNotNull(datacenter,
               ValidationErrors.NULL_RESOURCE + Datacenter.class.getCanonicalName());
         return this;
      }

      public Builder networkServiceType(final NetworkServiceType networkServiceType) {
         this.networkServiceType = Optional.of(networkServiceType);
         return this;
      }

      public PublicNetwork build() {
         VLANNetworkDto dto = new VLANNetworkDto();
         dto.setName(name);
         dto.setTag(tag);
         dto.setGateway(gateway);
         dto.setAddress(address);
         dto.setMask(mask);
         dto.setPrimaryDNS(primaryDNS);
         dto.setSecondaryDNS(secondaryDNS);
         dto.setSufixDNS(suffixDNS);
         dto.setDefaultNetwork(defaultNetwork);
         dto.setUnmanaged(false);
         dto.setType(NetworkType.PUBLIC);

         NetworkServiceType nst = networkServiceType.or(datacenter.defaultNetworkServiceType());
         dto.addLink(new RESTLink("networkservicetype", nst.unwrap().getEditLink().getHref()));

         PublicNetwork network = new PublicNetwork(context, dto);
         network.datacenter = datacenter;

         return network;
      }

      public static Builder fromPublicNetwork(final PublicNetwork in) {
         return PublicNetwork.builder(in.context, in.datacenter).name(in.getName()).tag(in.getTag())
               .gateway(in.getGateway()).address(in.getAddress()).mask(in.getMask()).primaryDNS(in.getPrimaryDNS())
               .secondaryDNS(in.getSecondaryDNS()).suffixDNS(in.getSuffixDNS()).defaultNetwork(in.getDefaultNetwork());
      }
   }

   @Override
   public String toString() {
      return "Public " + super.toString();
   }
}
