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

package org.jclouds.vcloud.director.v1_5.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Gives more details of local peer end point.
 *             
 * 
 * <p>Java class for IpsecVpnLocalPeer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IpsecVpnLocalPeer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}IpsecVpnManagedPeerType">
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "IpsecVpnLocalPeer")
public class IpsecVpnLocalPeer
    extends IpsecVpnManagedPeerType<IpsecVpnLocalPeer>

{
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnLocalPeer(this);
   }

   public static class Builder extends IpsecVpnManagedPeerType.Builder<IpsecVpnLocalPeer> {
      public IpsecVpnLocalPeer build() {
         return new IpsecVpnLocalPeer(id, name);
      }

      @Override
      public Builder fromIpsecVpnManagedPeerType(IpsecVpnManagedPeerType<IpsecVpnLocalPeer> in) {
          return Builder.class.cast(super.fromIpsecVpnManagedPeerType(in));
      }
      
      public Builder fromIpsecVpnLocalPeer(IpsecVpnLocalPeer in) {
         return fromIpsecVpnManagedPeerType(in);
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

   }

   private IpsecVpnLocalPeer(String id, String name) {
      super(id, name);
   }

   private IpsecVpnLocalPeer() {
      // For JAXB
   }
}
