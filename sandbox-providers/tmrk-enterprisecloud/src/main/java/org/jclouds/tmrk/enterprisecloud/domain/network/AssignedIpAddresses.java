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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.jclouds.tmrk.enterprisecloud.domain.Actions;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="AssignedIpAddresses">
 * @author Jason King
 */
@XmlRootElement(name="AssignedIpAddresses")
public class AssignedIpAddresses extends BaseResource<AssignedIpAddresses> {

   //TODO links

   @XmlElement(name = "Actions", required = true)
   private Actions actions = new Actions();

   @XmlElement(name = "Networks", required = true)
   private DeviceNetworks networks = new DeviceNetworks();

   public AssignedIpAddresses(URI href, String type, Actions actions, DeviceNetworks networks) {
      super(href, type);
      checkNotNull(actions,"actions");
      checkNotNull(networks,"networks");
   }

   public AssignedIpAddresses() {
       //For JAXB
   }

   public Actions getActions() {
       return actions;
   }

   public DeviceNetworks getNetworks() {
       return networks;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AssignedIpAddresses that = (AssignedIpAddresses) o;

        if (!actions.equals(that.actions)) return false;
        if (!networks.equals(that.networks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + actions.hashCode();
        result = 31 * result + networks.hashCode();
        return result;
    }

    @Override
    public String string() {
       return super.string()+", actions="+actions+", networks="+networks;
    }

}