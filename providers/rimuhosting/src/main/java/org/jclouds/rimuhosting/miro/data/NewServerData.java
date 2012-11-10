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
package org.jclouds.rimuhosting.miro.data;

import java.util.List;

import org.jclouds.rimuhosting.miro.domain.MetaData;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.ServerParameters;

import com.google.gson.annotations.SerializedName;

/**
 * This structure defines the VPS to be setup.
 * 
 * @author Ivan Meredith
 */
public class NewServerData implements PostData {
   public NewServerData() {
   }

   public NewServerData(CreateOptions createOptions, PricingPlan pricingPlan) {
      this.createOptions = createOptions;
      if (pricingPlan != null) {
         this.planId = pricingPlan.getId();
      }
   }

   public NewServerData(CreateOptions createOptions, String pricingPlanId) {
      this.createOptions = createOptions;
      this.planId = pricingPlanId;
   }

   public NewServerData(CloneOptions cloneOptions) {
      this.cloneOptions = cloneOptions;
   }

   /**
    * Set the billing id if you want to control how it is billed.&nbsp; Else we will, for example,
    * try to use, say, the credit card you used on your last order that had a credit card.&nbsp; Or
    * use a wire transfer method if you are using that on other orders.&nbsp; See the billing
    * methods resource for how to find what billing methods/ids you have setup on your identity.
    */
   @SerializedName("billing_oid")
   private Long billingId;
   /**
    * The host server on which to setup the server.&nbsp; Typically you will want to leave this
    * blank and let the API decide what is best/available.&nbsp; And exception may be if you are a
    * customer with a dedicated server that is a VPS host with us.&nbsp; And in that case you may
    * want to force a VPS to be setup on a particular server of yours.
    */
   @SerializedName("host_server_oid")
   private String hostServerId;
   /**
    * These are the instantiation options.&nbsp; e.g. domain name, password, etc.&nbsp; Only provide
    * these if you are not cloning a VPS (the vps_order_oid_to_clone setting). i.e. mutually
    * exclusive to instantiation_via_clone_options
    */
   @SerializedName("instantiation_options")
   private CreateOptions createOptions;
   /**
    * These are the instantiation options if you are creating a new VPS as a clone of an existing
    * VPS. Mutually exclusive to instantiation_options.
    */
   @SerializedName("instantiation_via_clone_options")
   private CloneOptions cloneOptions;
   /**
    * The number of IPs you need on the VPS and a justification for having more than one.&nbsp; Just
    * leave blank for a single IP (which is all most servers need).
    */
   @SerializedName("ip_request")
   private IpRequestData ipRequest;
   /**
    * The pricing plan code you want to use.&nbsp; Per the pricing plans resource.
    */
   @SerializedName("pricing_plan_code")
   private String planId;
   /**
    * To whom will the order belong? Leave this blank and we will assign it to you.&nbsp; If you set
    * it and you do not have permissions on that user's identity you will get an error.
    */
   @SerializedName("user_oid")
   private Long userId;
   /**
    * Any particular memory/disk size overrides you want to make.&nbsp; If they are compatible with
    * the pricing plan you selected we will use them.&nbsp; We will calculate the cost based on the
    * resources we setup you up with.&nbsp; We can provision VPSs in most sizes, provided that the
    * host has space for them.&nbsp; The low contention plans are an exception.&nbsp; You will
    * likely need to use the provided memory and disk sizes.&nbsp; Since those plans are designed so
    * there is a specific (small) number of VPSs per host.&nbsp; And having VPSs with 'odd' sizes
    * stops them all fitting in 'neatly' (that's not a problem on the bigger-, non-low
    * contention-plans.
    */
   @SerializedName("vps_parameters")
   private ServerParameters serverParameters;

   @SerializedName("meta_data")
   private List<MetaData> metaData;

   public Long getBillingId() {
      return billingId;
   }

   public void setBillingId(Long billingId) {
      this.billingId = billingId;
   }

   public String getHostServerId() {
      return hostServerId;
   }

   public void setHostServerId(String hostServerId) {
      this.hostServerId = hostServerId;
   }

   public CreateOptions getCreateOptions() {
      return createOptions;
   }

   public void setCreateOptions(CreateOptions createOptions) {
      this.createOptions = createOptions;
   }

   public CloneOptions getCloneOptions() {
      return cloneOptions;
   }

   public void setCloneOptions(CloneOptions cloneOptions) {
      this.cloneOptions = cloneOptions;
   }

   public IpRequestData getIpRequest() {
      return ipRequest;
   }

   public void setIpRequest(IpRequestData ipRequest) {
      this.ipRequest = ipRequest;
   }

   public String getPlanId() {
      return planId;
   }

   public void setPlanId(String planId) {
      this.planId = planId;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public ServerParameters getInstanceParameters() {
      return serverParameters;
   }

   public void setInstanceParameters(ServerParameters serverParameters) {
      this.serverParameters = serverParameters;
   }

   public void validate() {
      // bitwise XOR, works with boolean :)
      assert this.cloneOptions == null ^ this.createOptions == null;
      if (this.cloneOptions != null) {
         this.cloneOptions.validate();
      }
      if (this.createOptions != null) {
         this.createOptions.validate();
         assert this.planId != null && this.planId.length() == 0;
      }

      if (this.ipRequest != null) {
         this.ipRequest.validate();
      }

      if (this.serverParameters != null) {
         this.serverParameters.validate();
      }
   }

   public void setMetaData(List<MetaData> metaData) {
      this.metaData = metaData;
   }

   public List<MetaData> getMetaData() {
      return metaData;
   }
}
