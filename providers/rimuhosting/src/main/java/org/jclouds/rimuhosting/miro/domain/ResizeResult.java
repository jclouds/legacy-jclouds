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
package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Information about a resource change.
 */
public class ResizeResult {
   /**
    * Original monthly pricing, in USD.
    */
   @SerializedName("original_pricing")
   private Double originalPricing;
   /**
    * New monthly pricing, in USD, after the resource changes.
    */
   @SerializedName("new_pricing")
   private Double newPricing;
   /**
    * Information about the change.&nbsp; e.g. may include information
    * about how we calculate pro-rated credits or fees.&nbsp; Or what
    * paypal subscriptions need to get changed to.&nbsp; Or what
    * prepayment date changes were made.
    * <p/>
    * You will need to check these messages in some cases.&nbsp; e.g.
    * if you are decreasing pricing and you are paying via a paypal
    * subscription.&nbsp; Since you would be overpaying your identity,
    * unless you followed the directions to modify your paypal
    * subscription.
    */
   @SerializedName("resource_change_messages")
   private List<String> messages;
   /**
    * true if we made the resource changes.&nbsp; False if we did not
    * make them, e.g. if we hit a billing issue, or if the host server
    * did not have the resources to accomodate the change.
    */
   @SerializedName("were_resources_changed")
   private Boolean success;

   public double getNewPricing() {
      return newPricing;
   }

   public void setNewPricing(double newPricing) {
      this.newPricing = newPricing;
   }

   public List<String> getMessages() {
      return messages;
   }

   public void setMessages(List<String> messages) {
      this.messages = messages;
   }

   public boolean isSuccess() {
      return success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public double getOriginalPricing() {

      return originalPricing;
   }

   public void setOriginalPricing(double originalPricing) {
      this.originalPricing = originalPricing;
   }
}
