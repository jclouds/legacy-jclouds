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

package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.domain.internal.ServerType;

/**
 * @author Ivan Meredith
 */
public class PricingPlan implements Comparable<PricingPlan>{
   @SerializedName("minimum_data_transfer_allowance_gb")
   private Long dataTransfer;
   @SerializedName("minimum_disk_gb")
   private Integer diskSize;
   @SerializedName("minimum_memory_mb")
   private Integer ram;
   @SerializedName("monthly_recurring_fee_usd")
   private Double monthlyCharge;
   @SerializedName("offered_at_data_center")
   private DataCenter dataCenter;
   @SerializedName("pricing_plan_code")
   private String id;
   @SerializedName("pricing_plan_description")
   private String description;
   @SerializedName("see_also_url")
   private String see_also_url;
   @SerializedName("server_type")
   private ServerType serverType;

   public Long getDataTransfer() {
      return dataTransfer;
   }

   public void setDataTransfer(Long dataTransfer) {
      this.dataTransfer = dataTransfer;
   }

   public Integer getDiskSize() {
      return diskSize;
   }

   public void setDiskSize(Integer diskSize) {
      this.diskSize = diskSize;
   }

   public Integer getRam() {
      return ram;
   }

   public void setRam(Integer ram) {
      this.ram = ram;
   }

   public Double getMonthlyCharge() {
      return monthlyCharge;
   }

   public void setMonthlyCharge(Double monthlyCharge) {
      this.monthlyCharge = monthlyCharge;
   }

   public DataCenter getDataCenter() {
      return dataCenter;
   }

   public void setDataCenter(DataCenter dataCenter) {
      this.dataCenter = dataCenter;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSee_also_url() {
      return see_also_url;
   }

   public void setSee_also_url(String see_also_url) {
      this.see_also_url = see_also_url;
   }

   public ServerType getServerType() {
      return serverType;
   }

   public void setServerType(ServerType serverType) {
      this.serverType = serverType;
   }

   @Override
   public int compareTo(PricingPlan pricingPlan) {
      return id.compareTo(pricingPlan.getId());
   }
}
