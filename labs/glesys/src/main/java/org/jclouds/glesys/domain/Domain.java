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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Domain data for a Glesys account.
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#domain_list" />
 */
public class Domain implements Comparable<Domain> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String domainName;
      private Date createTime;
      private int recordCount;
      private boolean useGlesysNameServer;

      public Builder domainName(String domainName) {
         this.domainName = domainName;
         return this;
      }

      public Builder createTime(Date createTime) {
         this.createTime = createTime;
         return this;
      }

      public Builder recordCount(int recordCount) {
         this.recordCount = recordCount;
         return this;
      }

      public Builder useGlesysNameServer(boolean useGlesysNameServer) {
         this.useGlesysNameServer = useGlesysNameServer;
         return this;
      }

      public Domain build() {
         return new Domain(domainName, createTime, recordCount, useGlesysNameServer);
      }

      public Builder fromDomain(Domain in) {
         return new Builder().domainName(in.getDomainName()).createTime(in.getCreateTime()).recordCount(in.getRecordCount()).useGlesysNameServer(in.isGlesysNameServer());
      }
   }

   @SerializedName("domainname")
   private final String domainName;
   @SerializedName("createtime")
   private final Date createTime;
   @SerializedName("recordcount")
   private final int recordCount;
   @SerializedName("usingglesysnameserver")
   private final boolean useGlesysNameServer;

   public Domain(String domainName, Date createTime, int recordCount, boolean useGlesysNameServer) {
      this.domainName = domainName;
      this.createTime = createTime;
      this.recordCount = recordCount;
      this.useGlesysNameServer = useGlesysNameServer;
   }

   /** @return the domain name, ex. "jclouds.org" */
   public String getDomainName() {
      return domainName;
   }

   /** @return the date the domain was registered with GleSYS */
   public Date getCreateTime() {
      return createTime;
   }

   /** @return the number of DNS records for this domain */
   public int getRecordCount() {
      return recordCount;
   }

   /** @return true if a GleSYS nameserver holds the records */
   public boolean isGlesysNameServer() {
      return useGlesysNameServer;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(domainName);
   }

   @Override
   public int compareTo(Domain other) {
      return domainName.compareTo(other.getDomainName());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Domain) {
         return Objects.equal(domainName, ((Domain) object).domainName);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return String.format("[domainname=%s, createtime=%s, count=%d, useglesysnameserver=%b]", domainName, createTime, recordCount, useGlesysNameServer);
   }

}