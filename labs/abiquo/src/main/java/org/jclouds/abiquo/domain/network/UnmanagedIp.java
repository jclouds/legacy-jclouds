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

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.google.inject.TypeLiteral;

/**
 * Adds generic high level functionality to {@link UnmanagedIpDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class UnmanagedIp extends AbstractPublicIp<UnmanagedIpDto, UnmanagedNetwork> {
   /**
    * Constructor to be used only by the builder.
    */
   protected UnmanagedIp(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final UnmanagedIpDto target) {
      super(context, target);
   }

   // Domain operations

   @Override
   public UnmanagedNetwork getNetwork() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.UNMANAGED_NETWORK),
            ValidationErrors.MISSING_REQUIRED_LINK + " " + ParentLinkName.UNMANAGED_NETWORK);

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<VLANNetworkDto> parser = new ParseXMLWithJAXB<VLANNetworkDto>(utils.getXml(),
            TypeLiteral.get(VLANNetworkDto.class));

      return wrap(context, UnmanagedNetwork.class, parser.apply(response));
   }

   @Override
   public NetworkType getNetworkType() {
      return NetworkType.UNMANAGED;
   }

   @Override
   public String toString() {
      return "UnmanagedIp [networkType=" + getNetworkType() + ", available=" + isAvailable() + ", quarantine="
            + isQuarantine() + ", id=" + getId() + ", ip=" + getIp() + ", mac=" + getMac() + ", name=" + getName()
            + ", networkName=" + getNetworkName() + "]";
   }

}
