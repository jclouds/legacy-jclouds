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

/**
 * 
 *                 Gives more details of third party peer end point.
 *             
 * 
 * <p>Java class for IpsecVpnThirdPartyPeer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IpsecVpnThirdPartyPeer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}IpsecVpnUnmanagedPeerType">
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "IpsecVpnThirdPartyPeer")
public class IpsecVpnThirdPartyPeer extends IpsecVpnUnmanagedPeerType<IpsecVpnThirdPartyPeer> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnThirdPartyPeer(this);
   }

   public static class Builder {      
      public IpsecVpnThirdPartyPeer build() {
         return new IpsecVpnThirdPartyPeer();
      }

      public Builder fromIpsecVpnThirdPartyPeer(IpsecVpnThirdPartyPeer in) {
         return new Builder();
      }
   }

   private IpsecVpnThirdPartyPeer() {
      // For JAXB and builder use
   }
}
