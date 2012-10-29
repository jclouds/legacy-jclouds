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
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Syslog server settings. If logging is configured for firewall rules, the logs
 * will be directed to these syslog servers.
 *
 * @author danikov
 */
@XmlRootElement(name = "SyslogServerSettings")
public class SyslogServerSettings {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSyslogServerSettings(this);
   }

   public static class Builder {

      private String syslogServerIp1;
      private String syslogServerIp2;

      /**
       * @see SyslogServerSettings#getSyslogServerIp1()
       */
      public Builder syslogServerIp1(String syslogServerIp1) {
         this.syslogServerIp1 = syslogServerIp1;
         return this;
      }

      /**
       * @see SyslogServerSettings#getSyslogServerIp2()
       */
      public Builder syslogServerIp2(String syslogServerIp2) {
         this.syslogServerIp2 = syslogServerIp2;
         return this;
      }

      public SyslogServerSettings build() {
         SyslogServerSettings syslogServerSettings = new SyslogServerSettings();
         syslogServerSettings.syslogServerIp1 = syslogServerIp1;
         syslogServerSettings.syslogServerIp2 = syslogServerIp2;
         return syslogServerSettings;
      }

      public Builder fromSyslogServerSettings(SyslogServerSettings in) {
         return syslogServerIp1(in.getSyslogServerIp1()).syslogServerIp2(in.getSyslogServerIp2());
      }
   }

   private SyslogServerSettings() {
      // for JAXB
   }

   @XmlElement(name = "SyslogServerIp1")
   private String syslogServerIp1;
   @XmlElement(name = "SyslogServerIp2")
   private String syslogServerIp2;

   /**
    * @return Primary syslog server.
    */
   public String getSyslogServerIp1() {
      return syslogServerIp1;
   }

   /**
    * @return Secondary syslog server.
    */
   public String getSyslogServerIp2() {
      return syslogServerIp2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SyslogServerSettings that = SyslogServerSettings.class.cast(o);
      return equal(syslogServerIp1, that.syslogServerIp1) && equal(syslogServerIp2, that.syslogServerIp1);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(syslogServerIp1, syslogServerIp2);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("syslogServerIp1", syslogServerIp1)
            .add("syslogServerIp1", syslogServerIp2).toString();
   }
}
