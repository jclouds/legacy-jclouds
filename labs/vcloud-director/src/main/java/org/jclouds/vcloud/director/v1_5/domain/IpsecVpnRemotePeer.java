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

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Gives more details of remote peer end point.
 * <p/>
 * <p/>
 * <p>Java class for IpsecVpnRemotePeer complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="IpsecVpnRemotePeer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}IpsecVpnManagedPeerType">
 *       &lt;sequence>
 *         &lt;element name="VcdUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VcdOrganization" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VcdUsername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "IpsecVpnRemotePeer")
@XmlType(propOrder = {
      "vcdUrl",
      "vcdOrganization",
      "vcdUsername"
})
public class IpsecVpnRemotePeer extends IpsecVpnManagedPeerType<IpsecVpnRemotePeer> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnRemotePeer(this);
   }

   public static class Builder extends IpsecVpnManagedPeerType.Builder<IpsecVpnRemotePeer> {
      private String vcdUrl;
      private String vcdOrganization;
      private String vcdUsername;

      /**
       * @see IpsecVpnRemotePeer#getVcdUrl()
       */
      public Builder vcdUrl(String vcdUrl) {
         this.vcdUrl = vcdUrl;
         return this;
      }

      /**
       * @see IpsecVpnRemotePeer#getVcdOrganization()
       */
      public Builder vcdOrganization(String vcdOrganization) {
         this.vcdOrganization = vcdOrganization;
         return this;
      }

      /**
       * @see IpsecVpnRemotePeer#getVcdUsername()
       */
      public Builder vcdUsername(String vcdUsername) {
         this.vcdUsername = vcdUsername;
         return this;
      }

      public IpsecVpnRemotePeer build() {
         return new IpsecVpnRemotePeer(id, name, vcdUrl, vcdOrganization, vcdUsername);
      }

      @Override
      public Builder fromIpsecVpnManagedPeerType(IpsecVpnManagedPeerType<IpsecVpnRemotePeer> in) {
         return Builder.class.cast(super.fromIpsecVpnManagedPeerType(in));
      }

      public Builder fromIpsecVpnRemotePeer(IpsecVpnRemotePeer in) {
         return fromIpsecVpnManagedPeerType(in)
               .vcdUrl(in.getVcdUrl())
               .vcdOrganization(in.getVcdOrganization())
               .vcdUsername(in.getVcdUsername());
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

   private IpsecVpnRemotePeer(String id, String name, String vcdUrl, String vcdOrganization, String vcdUsername) {
      super(id, name);
      this.vcdUrl = vcdUrl;
      this.vcdOrganization = vcdOrganization;
      this.vcdUsername = vcdUsername;
   }

   private IpsecVpnRemotePeer() {
      // for JAXB
   }

   @XmlElement(name = "VcdUrl", required = true)
   protected String vcdUrl;
   @XmlElement(name = "VcdOrganization", required = true)
   protected String vcdOrganization;
   @XmlElement(name = "VcdUsername", required = true)
   protected String vcdUsername;

   /**
    * Gets the value of the vcdUrl property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVcdUrl() {
      return vcdUrl;
   }

   /**
    * Gets the value of the vcdOrganization property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVcdOrganization() {
      return vcdOrganization;
   }

   /**
    * Gets the value of the vcdUsername property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVcdUsername() {
      return vcdUsername;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpsecVpnRemotePeer that = IpsecVpnRemotePeer.class.cast(o);
      return super.equals(that)
            && equal(vcdUrl, that.vcdUrl)
            && equal(vcdOrganization, that.vcdOrganization)
            && equal(vcdUsername, that.vcdUsername);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
            vcdUrl,
            vcdOrganization,
            vcdUsername);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("vcdUrl", vcdUrl)
            .add("vcdOrganization", vcdOrganization)
            .add("vcdUsername", vcdUsername);
   }

}
