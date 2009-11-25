/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 * Billing data. Need to make it inteface with jclouds.
 * TODO: test
 *
 * @author Ivan Meredith
 */
public class BillingData {
   @SerializedName("cancellation_date")
   private Timestamp dateCancelled;
   @SerializedName("monthly_recurring_fee")
   private Double monthlyCharge;
   @SerializedName("order_date")
   private Timestamp dateOrdered;
   @SerializedName("prepaid_until")
   private Timestamp dataPrepaidUntil;
   @SerializedName("suspended_date")
   private Timestamp dateSuspended;

   public Timestamp getDateCancelled() {
      return dateCancelled;
   }

   public void setDateCancelled(Timestamp dateCancelled) {
      this.dateCancelled = dateCancelled;
   }

   public Double getMonthlyCharge() {
      return monthlyCharge;
   }

   public void setMonthlyCharge(Double monthlyCharge) {
      this.monthlyCharge = monthlyCharge;
   }

   public Timestamp getDateOrdered() {
      return dateOrdered;
   }

   public void setDateOrdered(Timestamp dateOrdered) {
      this.dateOrdered = dateOrdered;
   }

   public Timestamp getDataPrepaidUntil() {
      return dataPrepaidUntil;
   }

   public void setDataPrepaidUntil(Timestamp dataPrepaidUntil) {
      this.dataPrepaidUntil = dataPrepaidUntil;
   }

   public Timestamp getDateSuspended() {
      return dateSuspended;
   }

   public void setDateSuspended(Timestamp dateSuspended) {
      this.dateSuspended = dateSuspended;
   }
}
