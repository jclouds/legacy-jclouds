/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.data.NewServerData;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;

/**
 * Instance Object.
 * TODO: javadoc
 *
 * @author Ivan Meredith
 */
public class Server implements Comparable<Server> {

   @SerializedName("allocated_ips")
   private IpAddresses ipAddresses;
   @SerializedName("billing_oid")
   private Long billingId;
   @SerializedName("data_transfer_allowance")
   private DataTransferAllowance allowance;
   @SerializedName("distro")
   private String imageId;
   @SerializedName("domain_name")
   private String name;

   @SerializedName("host_server_oid")
   private String hostServerId;
   @SerializedName("is_on_customers_own_physical_server")
   private Boolean onDedicatedHardware;
   @SerializedName("order_oid")
   private Long id;
   @SerializedName("running_state")
   private RunningState state;
   @SerializedName("server_type")
   private String type;
   private String slug;
   @SerializedName("vps_parameters")
   private ServerParameters serverParameters;
   @SerializedName("billing_info")
   private BillingData billingData;
   
   private DataCenter location;

   @SerializedName("meta_data")
   private List<MetaData> metaData;
   //Object returned back with
   private transient NewServerData serverDataRequest;

 
   public IpAddresses getIpAddresses() {
      return ipAddresses;
   }

   public void setIpAddresses(IpAddresses ipAddresses) {
      this.ipAddresses = ipAddresses;
   }

   public Long getBillingId() {
      return billingId;
   }

   public void setBillingId(Long billingId) {
      this.billingId = billingId;
   }

   public DataTransferAllowance getAllowance() {
      return allowance;
   }

   public void setAllowance(DataTransferAllowance allowance) {
      this.allowance = allowance;
   }

   public String getImageId() {
      return imageId;
   }

   public void setImageId(String imageId) {
      this.imageId = imageId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getHostServerId() {
      return hostServerId;
   }

   public void setHostServerId(String hostServerId) {
      this.hostServerId = hostServerId;
   }

   public Boolean isOnDedicatedHardware() {
      return onDedicatedHardware;
   }

   public void setOnDedicatedHardware(Boolean onDedicatedHardware) {
      this.onDedicatedHardware = onDedicatedHardware;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public RunningState getState() {
      return state;
   }

   public void setState(RunningState state) {
      this.state = state;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getSlug() {
      return slug;
   }

   public void setSlug(String slug) {
      this.slug = slug;
   }

   public ServerParameters getInstanceParameters() {
      return serverParameters;
   }

   public void setInstanceParameters(ServerParameters serverParameters) {
      this.serverParameters = serverParameters;
   }

   public NewServerData getInstanceRequest() {
      return serverDataRequest;
   }

   public void setInstanceRequest(NewServerData serverDataRequest) {
      this.serverDataRequest = serverDataRequest;
   }
   
   @Override
   public int compareTo(Server server) {
      return name.compareTo(server.getName());
   }

   public void setLocation(DataCenter location) {
      this.location = location;
   }

   public DataCenter getLocation() {
      return location;
   }

   public void setMetaData(List<MetaData> metaData) {
      this.metaData = metaData;
   }

   public List<MetaData> getMetaData() {
      return metaData;
   }

   public Boolean getOnDedicatedHardware() {
      return onDedicatedHardware;
   }

   public BillingData getBillingData() {
      return billingData;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      Server other = (Server) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }
}
