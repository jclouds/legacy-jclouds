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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

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

      if (!Objects.equal(accountId, that.accountId)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(isSourceNAT, that.isSourceNAT)) return false;
      if (!Objects.equal(rawUsageHours, that.rawUsageHours)) return false;
      if (!Objects.equal(releaseDate, that.releaseDate)) return false;
      if (!Objects.equal(serviceOfferingId, that.serviceOfferingId)) return false;
      if (!Objects.equal(templateId, that.templateId)) return false;
      if (!Objects.equal(virtualMachineId, that.virtualMachineId)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(accountName, that.accountName)) return false;
      if (!Objects.equal(assignDate, that.assignDate)) return false;
      if (!Objects.equal(description, that.description)) return false;
      if (!Objects.equal(endDate, that.endDate)) return false;
      if (!Objects.equal(ipAddress, that.ipAddress)) return false;
      if (!Objects.equal(startDate, that.startDate)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(usage, that.usage)) return false;
      if (!Objects.equal(usageType, that.usageType)) return false;
      if (!Objects.equal(virtualMachineName, that.virtualMachineName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(accountId, domainId, id, isSourceNAT, rawUsageHours, releaseDate,
                               serviceOfferingId, templateId, virtualMachineId, zoneId, accountName,
                               assignDate, description, endDate, ipAddress, startDate, type, usage,
                               usageType, virtualMachineName);
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
