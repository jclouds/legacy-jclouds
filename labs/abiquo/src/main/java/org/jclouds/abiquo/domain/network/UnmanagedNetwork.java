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

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
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
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.inject.TypeLiteral;

/**
 * Adds high level functionality to external {@link VLANNetworkDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Public+Network+Resource"
 *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource</a>
 */
@EnterpriseEdition
public class UnmanagedNetwork extends Network<UnmanagedIp> {
   /** The datacenter where the network belongs. */
   private Datacenter datacenter;

   /** The enterprise where the network belongs. */
   private Enterprise enterprise;

   /**
    * Constructor to be used only by the builder.
    */
   protected UnmanagedNetwork(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final VLANNetworkDto target) {
      super(context, target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-DeleteanUnmanagedNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -DeleteanUnmanagedNetwork</a>
    */
   @Override
   public void delete() {
      context.getApi().getInfrastructureApi().deleteNetwork(target);
      target = null;
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-CreateanewUnmanagedNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -CreateanewUnmanagedNetwork</a>
    */
   @Override
   public void save() {
      this.addEnterpriseLink();
      target = context.getApi().getInfrastructureApi().createNetwork(datacenter.unwrap(), target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Public+Network+Resource#PublicNetworkResource-UpdateanUnmanagedNetwork"
    *      > http://community.abiquo.com/display/ABI20/Public+Network+Resource#
    *      PublicNetworkResource -UpdateanUnmanagedNetwork</a>
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
   public List<UnmanagedIp> listIps(final IpOptions options) {
      UnmanagedIpsDto ips = context.getApi().getInfrastructureApi().listUnmanagedIps(target, options);
      return wrap(context, UnmanagedIp.class, ips.getCollection());
   }

   @Override
   public UnmanagedIp getIp(final Integer id) {
      UnmanagedIpDto ip = context.getApi().getInfrastructureApi().getUnmanagedIp(target, id);
      return wrap(context, UnmanagedIp.class, ip);
   }

   // Parent access

   public Enterprise getEnterprise() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.ENTERPRISE), ValidationErrors.MISSING_REQUIRED_LINK
            + " " + ParentLinkName.ENTERPRISE);

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<EnterpriseDto> parser = new ParseXMLWithJAXB<EnterpriseDto>(utils.getXml(),
            TypeLiteral.get(EnterpriseDto.class));

      enterprise = wrap(context, Enterprise.class, parser.apply(response));
      return enterprise;
   }

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

   private void addEnterpriseLink() {
      checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
      checkNotNull(enterprise.getId(), ValidationErrors.MISSING_REQUIRED_FIELD + " id in " + Enterprise.class);

      RESTLink link = enterprise.unwrap().searchLink("edit");

      checkNotNull(link, ValidationErrors.MISSING_REQUIRED_LINK);

      target.addLink(new RESTLink("enterprise", link.getHref()));
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter,
         final Enterprise enterprise) {
      return new Builder(context, datacenter, enterprise);
   }

   public static class Builder extends NetworkBuilder<Builder> {
      private Datacenter datacenter;

      private Enterprise enterprise;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter,
            final Enterprise enterprise) {
         super(context);
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Enterprise.class);
         this.datacenter = datacenter;
         this.enterprise = enterprise;
         this.context = context;
      }

      public Builder datacenter(final Datacenter datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder enterprise(final Enterprise enterprise) {
         this.enterprise = enterprise;
         return this;
      }

      public UnmanagedNetwork build() {
         VLANNetworkDto dto = new VLANNetworkDto();
         dto.setName(name);
         dto.setTag(tag);
         dto.setGateway(gateway);
         dto.setAddress(address);
         dto.setMask(mask);
         dto.setPrimaryDNS(primaryDNS);
         dto.setSecondaryDNS(secondaryDNS);
         dto.setSufixDNS(sufixDNS);
         dto.setDefaultNetwork(defaultNetwork);
         dto.setUnmanaged(true);
         dto.setType(NetworkType.UNMANAGED);

         UnmanagedNetwork network = new UnmanagedNetwork(context, dto);
         network.datacenter = datacenter;
         network.enterprise = enterprise;

         return network;
      }

      public static Builder fromUnmanagedNetwork(final UnmanagedNetwork in) {
         return UnmanagedNetwork.builder(in.context, in.datacenter, in.enterprise).name(in.getName()).tag(in.getTag())
               .gateway(in.getGateway()).address(in.getAddress()).mask(in.getMask()).primaryDNS(in.getPrimaryDNS())
               .secondaryDNS(in.getSecondaryDNS()).sufixDNS(in.getSufixDNS()).defaultNetwork(in.getDefaultNetwork());
      }
   }

   @Override
   public String toString() {
      return "Unmanaged " + super.toString();
   }
}
