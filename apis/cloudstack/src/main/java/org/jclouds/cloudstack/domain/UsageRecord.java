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
package org.jclouds.cloudstack.domain;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a usage record from CloudStack
 *
 * @author Richard Downer
 */
public class UsageRecord implements Comparable<UsageRecord> {

   public enum UsageType {
      RUNNING_VM(1),
      ALLOCATED_VM(2),
      IP_ADDRESS(3),
      NETWORK_BYTES_SENT(4),
      NETWORK_BYTES_RECEIVED(5),
      VOLUME(6),
      TEMPLATE(7),
      ISO(8),
      SNAPSHOT(9),
      SECURITY_GROUP(10),
      LOAD_BALANCER_POLICY(11),
      PORT_FORWARDING_RULE(12),
      NETWORK_OFFERING(13),
      VPN_USERS(14),
      UNRECOGNIZED(0);

      private int code;

      private static final Map<Integer, UsageType> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(UsageType.values()),
         new Function<UsageType, Integer>() {

            @Override
            public Integer apply(UsageType input) {
               return input.code;
            }

         });

      UsageType(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return "" + code;
      }

      public static UsageType fromValue(String usageType) {
         Integer code = new Integer(checkNotNull(usageType, "usageType"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private Builder() {
      }

      private long id;
      private String description;
      private long accountId;
      private String accountName;
      private long domainId;
      private Date startDate;
      private Date endDate;
      private Date assignDate;
      private long releaseDate;
      private long zoneId;
      private long virtualMachineId;
      private String virtualMachineName;
      private long serviceOfferingId;
      private long templateId;
      private String ipAddress;
      private boolean isSourceNAT;
      private double rawUsageHours;
      private String usage;
      private String type;
      private UsageType usageType;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder accountId(long accountId) {
         this.accountId = accountId;
         return this;
      }

      public Builder accountName(String accountName) {
         this.accountName = accountName;
         return this;
      }

      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder startDate(Date startDate) {
         this.startDate = startDate;
         return this;
      }

      public Builder endDate(Date endDate) {
         this.endDate = endDate;
         return this;
      }

      public Builder assignDate(Date assignDate) {
         this.assignDate = assignDate;
         return this;
      }

      public Builder releaseDate(long releaseDate) {
         this.releaseDate = releaseDate;
         return this;
      }

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder virtualMachineId(long virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return this;
      }

      public Builder serviceOfferingId(long serviceOfferingId) {
         this.serviceOfferingId = serviceOfferingId;
         return this;
      }

      public Builder templateId(long templateId) {
         this.templateId = templateId;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder surceNAT(boolean sourceNAT) {
         isSourceNAT = sourceNAT;
         return this;
      }

      public Builder rawUsageHours(double rawUsageHours) {
         this.rawUsageHours = rawUsageHours;
         return this;
      }

      public Builder usage(String usage) {
         this.usage = usage;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder usageType(UsageType usageType) {
         this.usageType = usageType;
         return this;
      }

      public UsageRecord build() {
         return new UsageRecord(id, description, accountId, accountName, domainId, startDate, endDate, assignDate, releaseDate, zoneId, virtualMachineId, virtualMachineName, serviceOfferingId, templateId, ipAddress, isSourceNAT, rawUsageHours, usage, type, usageType);
      }
   }

   @SerializedName("usageid") private long id;
   private String description;
   @SerializedName("accountid") private long accountId;
   @SerializedName("account") private String accountName;
   @SerializedName("domainid") private long domainId;
   @SerializedName("startdate") private Date startDate;
   @SerializedName("enddate") private Date endDate;
   @SerializedName("assigndate") private Date assignDate;
   @SerializedName("releasedate") private long releaseDate;
   @SerializedName("zoneid") private long zoneId;
   @SerializedName("virtualmachineid") private long virtualMachineId;
   @SerializedName("name") private String virtualMachineName;
   @SerializedName("offeringid") private long serviceOfferingId;
   @SerializedName("templateid") private long templateId;
   @SerializedName("ipaddress") private String ipAddress;
   @SerializedName("issourcenat") private boolean isSourceNAT;
   @SerializedName("rawusage") private double rawUsageHours;
   @SerializedName("usage") private String usage;
   private String type;
   @SerializedName("usagetype") private UsageType usageType;

   /* Exists only for the deserializer */
   UsageRecord(){
   }

   public UsageRecord(long id, String description, long accountId, String accountName, long domainId, Date startDate, Date endDate, Date assignDate, long releaseDate, long zoneId, long virtualMachineId, String virtualMachineName, long serviceOfferingId, long templateId, String ipAddress, boolean sourceNAT, double rawUsageHours, String usage, String type, UsageType usageType) {
      this.id = id;
      this.description = description;
      this.accountId = accountId;
      this.accountName = accountName;
      this.domainId = domainId;
      this.startDate = startDate;
      this.endDate = endDate;
      this.assignDate = assignDate;
      this.releaseDate = releaseDate;
      this.zoneId = zoneId;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.serviceOfferingId = serviceOfferingId;
      this.templateId = templateId;
      this.ipAddress = ipAddress;
      isSourceNAT = sourceNAT;
      this.rawUsageHours = rawUsageHours;
      this.usage = usage;
      this.type = type;
      this.usageType = usageType;
   }

   public long getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public long getAccountId() {
      return accountId;
   }

   public String getAccountName() {
      return accountName;
   }

   public long getDomainId() {
      return domainId;
   }

   public Date getStartDate() {
      return startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   public Date getAssignDate() {
      return assignDate;
   }

   public long getReleaseDate() {
      return releaseDate;
   }

   public long getZoneId() {
      return zoneId;
   }

   public long getVirtualMachineId() {
      return virtualMachineId;
   }

   public String getVirtualMachineName() {
      return virtualMachineName;
   }

   public long getServiceOfferingId() {
      return serviceOfferingId;
   }

   public long getTemplateId() {
      return templateId;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public boolean isSourceNAT() {
      return isSourceNAT;
   }

   public double getRawUsageHours() {
      return rawUsageHours;
   }

   public String getUsage() {
      return usage;
   }

   public String getType() {
      return type;
   }

   public UsageType getUsageType() {
      return usageType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UsageRecord that = (UsageRecord) o;

      if (accountId != that.accountId) return false;
      if (domainId != that.domainId) return false;
      if (id != that.id) return false;
      if (isSourceNAT != that.isSourceNAT) return false;
      if (Double.compare(that.rawUsageHours, rawUsageHours) != 0) return false;
      if (releaseDate != that.releaseDate) return false;
      if (serviceOfferingId != that.serviceOfferingId) return false;
      if (templateId != that.templateId) return false;
      if (virtualMachineId != that.virtualMachineId) return false;
      if (zoneId != that.zoneId) return false;
      if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null) return false;
      if (assignDate != null ? !assignDate.equals(that.assignDate) : that.assignDate != null) return false;
      if (description != null ? !description.equals(that.description) : that.description != null) return false;
      if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
      if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;
      if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
      if (type != null ? !type.equals(that.type) : that.type != null) return false;
      if (usage != null ? !usage.equals(that.usage) : that.usage != null) return false;
      if (usageType != that.usageType) return false;
      if (virtualMachineName != null ? !virtualMachineName.equals(that.virtualMachineName) : that.virtualMachineName != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = (int) (id ^ (id >>> 32));
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (int) (accountId ^ (accountId >>> 32));
      result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
      result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
      result = 31 * result + (assignDate != null ? assignDate.hashCode() : 0);
      result = 31 * result + (int) (releaseDate ^ (releaseDate >>> 32));
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (int) (virtualMachineId ^ (virtualMachineId >>> 32));
      result = 31 * result + (virtualMachineName != null ? virtualMachineName.hashCode() : 0);
      result = 31 * result + (int) (serviceOfferingId ^ (serviceOfferingId >>> 32));
      result = 31 * result + (int) (templateId ^ (templateId >>> 32));
      result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
      result = 31 * result + (isSourceNAT ? 1 : 0);
      temp = rawUsageHours != +0.0d ? Double.doubleToLongBits(rawUsageHours) : 0L;
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (usage != null ? usage.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (usageType != null ? usageType.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "UsageRecord{" +
         "id=" + id +
         ", description='" + description + '\'' +
         ", accountId=" + accountId +
         ", accountName='" + accountName + '\'' +
         ", domainId=" + domainId +
         ", startDate=" + startDate +
         ", endDate=" + endDate +
         ", assignDate=" + assignDate +
         ", releaseDate=" + releaseDate +
         ", zoneId=" + zoneId +
         ", virtualMachineId=" + virtualMachineId +
         ", virtualMachineName='" + virtualMachineName + '\'' +
         ", serviceOfferingId=" + serviceOfferingId +
         ", templateId=" + templateId +
         ", ipAddress='" + ipAddress + '\'' +
         ", isSourceNAT=" + isSourceNAT +
         ", rawUsageHours=" + rawUsageHours +
         ", usage='" + usage + '\'' +
         ", type='" + type + '\'' +
         ", usageType=" + usageType +
         '}';
   }

   @Override
   public int compareTo(UsageRecord other) {
      return Long.valueOf(this.id).compareTo(other.id);
   }
}
