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
package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.network.NetworkServiceType;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.network.NetworkInterfaceDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.google.inject.TypeLiteral;

/**
 * Represents a physical attached NIC.
 * 
 * Allows to specify the {@link NetworkServiceType} for the network interface.
 * This way all network interfaces have the information of the kind of network
 * they are attached to.
 * 
 * @author Jaume Devesa
 */
public class NetworkInterface extends DomainWrapper<NetworkInterfaceDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   protected NetworkInterface(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final NetworkInterfaceDto target) {
      super(context, target);
   }

   public String getName() {
      return target.getName();
   }

   public String getMac() {
      return target.getMac();
   }

   public void setNetworkServiceType(final NetworkServiceType type) {
      checkNotNull(type, "network service type cannot be null");
      target.setNetworkServiceTypeLink(type.unwrap().getEditLink().getHref());
   }

   public NetworkServiceType getNetworkServiceType() {
      RESTLink link = target.getNetworkServiceTypeLink();

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<NetworkServiceTypeDto> parser = new ParseXMLWithJAXB<NetworkServiceTypeDto>(utils.getXml(),
            TypeLiteral.get(NetworkServiceTypeDto.class));

      return wrap(context, NetworkServiceType.class, parser.apply(response));
   }
}
