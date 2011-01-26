/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.elb.domain;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;

/**
 * 
 * 
 * @author Lili Nader
 */
@Beta
// Missing fields, this class is too big, please cut out inner classes into top-level
public class LoadBalancer implements Comparable<LoadBalancer> {

   // Missing: createdTime, healthcheck
   private String region;
   private String name;
   private Set<String> instanceIds;
   private Set<String> availabilityZones;
   private String dnsName;
   // TODO: this could be cleaned up to be a policy collection of subclasses of Policy. note that
   // docs suggest there could be many
   private AppCookieStickinessPolicy appCookieStickinessPolicy;
   private LBCookieStickinessPolicy lBCookieStickinessPolicy;
   private Set<LoadBalancerListener> loadBalancerListeners;

   public LoadBalancer() {
      super();
      this.instanceIds = new HashSet<String>();
      this.availabilityZones = new HashSet<String>();
      this.loadBalancerListeners = new HashSet<LoadBalancerListener>();
   }

   public LoadBalancer(String region, String name, Set<String> instanceIds, Set<String> availabilityZones,
            String dnsName) {
      this.region = region;
      this.name = name;
      this.instanceIds = instanceIds;
      this.availabilityZones = availabilityZones;
      this.dnsName = dnsName;
      this.loadBalancerListeners = new HashSet<LoadBalancerListener>();
   }

   public void setRegion(String region) {
      this.region = region;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setInstanceIds(Set<String> instanceIds) {
      this.instanceIds = instanceIds;
   }

   public void setAvailabilityZones(Set<String> availabilityZones) {
      this.availabilityZones = availabilityZones;
   }

   public void setDnsName(String dnsName) {
      this.dnsName = dnsName;
   }

   public void setAppCookieStickinessPolicy(AppCookieStickinessPolicy appCookieStickinessPolicy) {
      this.appCookieStickinessPolicy = appCookieStickinessPolicy;
   }

   public void setlBCookieStickinessPolicy(LBCookieStickinessPolicy lBCookieStickinessPolicy) {
      this.lBCookieStickinessPolicy = lBCookieStickinessPolicy;
   }

   public void setLoadBalancerListeners(Set<LoadBalancerListener> loadBalancerListeners) {
      this.loadBalancerListeners = loadBalancerListeners;
   }

   public String getName() {
      return name;
   }

   public Set<String> getInstanceIds() {
      return instanceIds;
   }

   public Set<String> getAvailabilityZones() {
      return availabilityZones;
   }

   public String getDnsName() {
      return dnsName;
   }

   public AppCookieStickinessPolicy getAppCookieStickinessPolicy() {
      return appCookieStickinessPolicy;
   }

   public LBCookieStickinessPolicy getlBCookieStickinessPolicy() {
      return lBCookieStickinessPolicy;
   }

   public Set<LoadBalancerListener> getLoadBalancerListeners() {
      return loadBalancerListeners;
   }

   public String getRegion() {
      return region;
   }

   @Override
   public int compareTo(LoadBalancer that) {
      return name.compareTo(that.name);
   }

   @Override
   public String toString() {
      return "[region=" + region + ", name=" + name + ", instanceIds=" + instanceIds + ", availabilityZones="
               + availabilityZones + ", dnsName=" + dnsName + ", appCookieStickinessPolicy="
               + appCookieStickinessPolicy + ", lBCookieStickinessPolicy=" + lBCookieStickinessPolicy
               + ", loadBalancerListeners=" + loadBalancerListeners + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((appCookieStickinessPolicy == null) ? 0 : appCookieStickinessPolicy.hashCode());
      result = prime * result + ((availabilityZones == null) ? 0 : availabilityZones.hashCode());
      result = prime * result + ((dnsName == null) ? 0 : dnsName.hashCode());
      result = prime * result + ((instanceIds == null) ? 0 : instanceIds.hashCode());
      result = prime * result + ((lBCookieStickinessPolicy == null) ? 0 : lBCookieStickinessPolicy.hashCode());
      result = prime * result + ((loadBalancerListeners == null) ? 0 : loadBalancerListeners.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadBalancer other = (LoadBalancer) obj;
      if (appCookieStickinessPolicy == null) {
         if (other.appCookieStickinessPolicy != null)
            return false;
      } else if (!appCookieStickinessPolicy.equals(other.appCookieStickinessPolicy))
         return false;
      if (availabilityZones == null) {
         if (other.availabilityZones != null)
            return false;
      } else if (!availabilityZones.equals(other.availabilityZones))
         return false;
      if (dnsName == null) {
         if (other.dnsName != null)
            return false;
      } else if (!dnsName.equals(other.dnsName))
         return false;
      if (instanceIds == null) {
         if (other.instanceIds != null)
            return false;
      } else if (!instanceIds.equals(other.instanceIds))
         return false;
      if (lBCookieStickinessPolicy == null) {
         if (other.lBCookieStickinessPolicy != null)
            return false;
      } else if (!lBCookieStickinessPolicy.equals(other.lBCookieStickinessPolicy))
         return false;
      if (loadBalancerListeners == null) {
         if (other.loadBalancerListeners != null)
            return false;
      } else if (!loadBalancerListeners.equals(other.loadBalancerListeners))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      return true;
   }

   public static class AppCookieStickinessPolicy {
      private String policyName;
      private String cookieName;

      public AppCookieStickinessPolicy() {
         super();
      }

      public AppCookieStickinessPolicy(String policyName, String cookieName) {
         super();
         this.policyName = policyName;
         this.cookieName = cookieName;
      }

      public String getPolicyName() {
         return policyName;
      }

      public String getCookieName() {
         return cookieName;
      }

      public void setPolicyName(String policyName) {
         this.policyName = policyName;
      }

      public void setCookieName(String cookieName) {
         this.cookieName = cookieName;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((cookieName == null) ? 0 : cookieName.hashCode());
         result = prime * result + ((policyName == null) ? 0 : policyName.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return "[policyName=" + policyName + ", cookieName=" + cookieName + "]";
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         AppCookieStickinessPolicy other = (AppCookieStickinessPolicy) obj;
         if (cookieName == null) {
            if (other.cookieName != null)
               return false;
         } else if (!cookieName.equals(other.cookieName))
            return false;
         if (policyName == null) {
            if (other.policyName != null)
               return false;
         } else if (!policyName.equals(other.policyName))
            return false;
         return true;
      }

   }

   public static class LBCookieStickinessPolicy {
      private String policyName;
      private Integer cookieExpirationPeriod;

      public LBCookieStickinessPolicy() {
         super();
      }

      public LBCookieStickinessPolicy(String policyName, Integer cookieExpirationPeriod) {
         super();
         this.policyName = policyName;
         this.cookieExpirationPeriod = cookieExpirationPeriod;
      }

      public String getPolicyName() {
         return policyName;
      }

      public Integer getCookieExpirationPeriod() {
         return cookieExpirationPeriod;
      }

      public void setPolicyName(String policyName) {
         this.policyName = policyName;
      }

      public void setCookieExpirationPeriod(Integer cookieExpirationPeriod) {
         this.cookieExpirationPeriod = cookieExpirationPeriod;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((cookieExpirationPeriod == null) ? 0 : cookieExpirationPeriod.hashCode());
         result = prime * result + ((policyName == null) ? 0 : policyName.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         LBCookieStickinessPolicy other = (LBCookieStickinessPolicy) obj;
         if (cookieExpirationPeriod == null) {
            if (other.cookieExpirationPeriod != null)
               return false;
         } else if (!cookieExpirationPeriod.equals(other.cookieExpirationPeriod))
            return false;
         if (policyName == null) {
            if (other.policyName != null)
               return false;
         } else if (!policyName.equals(other.policyName))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[policyName=" + policyName + ", cookieExpirationPeriod=" + cookieExpirationPeriod + "]";
      }

   }

   public static class LoadBalancerListener {
      // TODO: missing SSLCertificateId
      private Set<String> policyNames;
      private Integer instancePort;
      private Integer loadBalancerPort;
      private String protocol;

      public LoadBalancerListener(Set<String> policyNames, Integer instancePort, Integer loadBalancerPort,
               String protocol) {
         super();
         this.policyNames = policyNames;
         this.instancePort = instancePort;
         this.loadBalancerPort = loadBalancerPort;
         this.protocol = protocol;
      }

      public LoadBalancerListener() {
         super();
      }

      public Set<String> getPolicyNames() {
         return policyNames;
      }

      public Integer getInstancePort() {
         return instancePort;
      }

      public Integer getLoadBalancerPort() {
         return loadBalancerPort;
      }

      public String getProtocol() {
         return protocol;
      }

      public void setPolicyNames(Set<String> policyNames) {
         this.policyNames = policyNames;
      }

      public void setInstancePort(Integer instancePort) {
         this.instancePort = instancePort;
      }

      public void setLoadBalancerPort(Integer loadBalancerPort) {
         this.loadBalancerPort = loadBalancerPort;
      }

      public void setProtocol(String protocol) {
         this.protocol = protocol;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((instancePort == null) ? 0 : instancePort.hashCode());
         result = prime * result + ((loadBalancerPort == null) ? 0 : loadBalancerPort.hashCode());
         result = prime * result + ((policyNames == null) ? 0 : policyNames.hashCode());
         result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         LoadBalancerListener other = (LoadBalancerListener) obj;
         if (instancePort == null) {
            if (other.instancePort != null)
               return false;
         } else if (!instancePort.equals(other.instancePort))
            return false;
         if (loadBalancerPort == null) {
            if (other.loadBalancerPort != null)
               return false;
         } else if (!loadBalancerPort.equals(other.loadBalancerPort))
            return false;
         if (policyNames == null) {
            if (other.policyNames != null)
               return false;
         } else if (!policyNames.equals(other.policyNames))
            return false;
         if (protocol == null) {
            if (other.protocol != null)
               return false;
         } else if (!protocol.equals(other.protocol))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[policyNames=" + policyNames + ", instancePort=" + instancePort + ", loadBalancerPort="
                  + loadBalancerPort + ", protocol=" + protocol + "]";
      }

   }
}
