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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Returns a network configuration
 * 
 * @author danikov
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "NetworkConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkConfiguration{

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromConfiguration(this);
   }

   public static class Builder {
      private IpScope ipScope;
      private ReferenceType<?> parentNetwork;
      private String fenceMode;
      private boolean retainNetInfoAcrossDeployments;
      private NetworkFeatures features;
      private SyslogServerSettings syslogServerSettings;
      private RouterInfo routerInfo;

      /**
       * @see NetworkConfiguration#getIpScope()
       */
      public Builder ipScope(IpScope ipScope) {
         this.ipScope = ipScope;
         return this;
      }

      /**
       * @see NetworkConfiguration#getParentNetwork()
       */
      public Builder parentNetwork(ReferenceType<?> parentNetwork) {
         this.parentNetwork = parentNetwork;
         return this;
      }

      /**
       * @see NetworkConfiguration#getFenceMode()
       */
      public Builder fenceMode(String fenceMode) {
         this.fenceMode = fenceMode;
         return this;
      }

      /**
       * @see NetworkConfiguration#getRetainNetInfoAcrossDeployments()
       */
      public Builder retainNetInfoAcrossDeployments(boolean retainNetInfoAcrossDeployments) {
         this.retainNetInfoAcrossDeployments = retainNetInfoAcrossDeployments;
         return this;
      }

      /**
       * @see NetworkConfiguration#getNetworkFeatures()
       */
      public Builder features(NetworkFeatures features) {
         this.features = features;
         return this;
      }

      /**
       * @see NetworkConfiguration#getSyslogServerSettings()
       */
      public Builder syslogServerSettings(SyslogServerSettings syslogServerSettings) {
         this.syslogServerSettings = syslogServerSettings;
         return this;
      }

      /**
       * @see NetworkConfiguration#getRouterInfo()
       */
      public Builder routerInfo(RouterInfo routerInfo) {
         this.routerInfo = routerInfo;
         return this;
      }

      public NetworkConfiguration build() {
         NetworkConfiguration networkConfiguration = new NetworkConfiguration(fenceMode);
         networkConfiguration.setIpScope(ipScope);
         networkConfiguration.setParentNetwork(parentNetwork);
         networkConfiguration.setRetainNetInfoAcrossDeployments(retainNetInfoAcrossDeployments);
         networkConfiguration.setNetworkFeatures(features);
         networkConfiguration.setSyslogServerSettings(syslogServerSettings);
         networkConfiguration.setRouterInfo(routerInfo);
         return networkConfiguration;
      }

      public Builder fromConfiguration(NetworkConfiguration in) {
         return ipScope(in.getIpScope()).parentNetwork(in.getParentNetwork()).fenceMode(in.getFenceMode())
               .retainNetInfoAcrossDeployments(in.getRetainNetInfoAcrossDeployments())
               .features(in.getNetworkFeatures())
               .syslogServerSettings(in.getSyslogServerSettings())
               .routerInfo(in.getRouterInfo());
      }
   }

   private NetworkConfiguration() {
      // For JAXB and builder use
   }

   private NetworkConfiguration(String fenceMode) {
      this.fenceMode = fenceMode;
   }

   @XmlElement(namespace = VCLOUD_1_5_NS, name = "IpScope")
   private IpScope ipScope;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "ParentNetwork")
   private ReferenceType<?> parentNetwork;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "FenceMode")
   private String fenceMode;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "RetainNetInfoAcrossDeployments")
   private boolean retainNetInfoAcrossDeployments = false;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Features")
   private NetworkFeatures features;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "SyslogServerSettings")
   private SyslogServerSettings syslogServerSettings;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "RouterInfo")
   private RouterInfo routerInfo;

   /**
    * @return IP level configuration items such as gateway, dns, subnet, 
    * IP address pool to be used for allocation. Note that the pool of IP addresses 
    * needs to fall within the subnet/mask of the IpScope.
    */
   public IpScope getIpScope() {
      return ipScope;
   }
   
   public void setIpScope(IpScope ipScope) {
      this.ipScope = ipScope;
   }

   /**
    * @return reference to parent network.
    */
   public ReferenceType<?> getParentNetwork() {
      return parentNetwork;
   }
   
   public void setParentNetwork(ReferenceType<?> parentNetwork) {
      this.parentNetwork = parentNetwork;
   }

   /**
    * @return Isolation type of the network. If ParentNetwork is specified, this property 
    * controls connectivity to the parent. One of: bridged (connected directly to the ParentNetwork), 
    * isolated (not connected to any other network), natRouted (connected to the ParentNetwork via a 
    * NAT service)
    */
   public String getFenceMode() {
      return fenceMode;
   }

   /**
    * @return whether the network resources such as IP/MAC of router will be retained 
    * across deployments. Default is false.
    */
   public boolean getRetainNetInfoAcrossDeployments() {
      return retainNetInfoAcrossDeployments;
   }
   
   public void setRetainNetInfoAcrossDeployments(boolean retainNetInfoAcrossDeployments) {
      this.retainNetInfoAcrossDeployments = retainNetInfoAcrossDeployments;
   }

   /**
    * @return Network features like DHCP, firewall and NAT rules.
    */
   public NetworkFeatures getNetworkFeatures() {
      return features;
   }
   
   public void setNetworkFeatures(NetworkFeatures features) {
      this.features = features;
   }

   /**
    * @return Syslog server settings for the network.
    */
   public SyslogServerSettings getSyslogServerSettings() {
      return syslogServerSettings;
   }
   
   public void setSyslogServerSettings(SyslogServerSettings syslogServerSettings) {
      this.syslogServerSettings = syslogServerSettings;
   }

   /**
    * @return router information
    */
   public RouterInfo getRouterInfo() {
      return routerInfo;
   }
   
   public void setRouterInfo(RouterInfo routerInfo) {
      this.routerInfo = routerInfo;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkConfiguration that = NetworkConfiguration.class.cast(o);
      return equal(ipScope, that.ipScope) && equal(parentNetwork, that.parentNetwork) &&
            equal(fenceMode, that.fenceMode) &&
            equal(retainNetInfoAcrossDeployments, that.retainNetInfoAcrossDeployments) &&
            equal(features, that.features) &&
            equal(syslogServerSettings, that.syslogServerSettings) &&
            equal(routerInfo, that.routerInfo);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipScope, parentNetwork, fenceMode, retainNetInfoAcrossDeployments,
            features, syslogServerSettings, routerInfo);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("ipScope", ipScope).add("parentNetwork", parentNetwork)
            .add("fenceMode", fenceMode)
            .add("retainNetInfoAcrossDeployments", retainNetInfoAcrossDeployments)
            .add("features", features)
            .add("syslogServerSettings", syslogServerSettings)
            .add("routerInfo", routerInfo).toString();
   }
}
