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
package org.jclouds.softlayer.domain;

/**
 * 
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Container_Product_Order_Receipt"
 *      />
 */
public class ProductOrderReceipt implements Comparable<ProductOrderReceipt> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int orderId = -1;
      private ProductOrder orderDetails;

      public Builder orderId(int orderId) {
         this.orderId = orderId;
         return this;
      }

      public Builder orderDetails(ProductOrder orderDetails) {
         this.orderDetails = orderDetails;
         return this;
      }

      public ProductOrderReceipt build() {
         return new ProductOrderReceipt(orderId,orderDetails);
      }

      public static Builder fromAddress(ProductOrderReceipt in) {
         return ProductOrderReceipt.builder().orderId(in.getOrderId()).orderDetails(in.getOrderDetails());
      }
   }

   private int orderId = -1;
   private ProductOrder orderDetails;

   // for deserializer
   ProductOrderReceipt() {

   }

   public ProductOrderReceipt(int orderId,ProductOrder orderDetails) {
      this.orderId = orderId;
      this.orderDetails = orderDetails;
   }

   @Override
   public int compareTo(ProductOrderReceipt arg0) {
      return Integer.valueOf(orderId).compareTo(arg0.getOrderId());
   }

   /**
    * @return unique identifier for the order.
    */
   public int getOrderId() {
      return orderId;
   }

   /**
    * This is a copy of the SoftLayer_Container_Product_Order
    * which holds all the data related to an order.
    * This will only return when an order is processed successfully.
    * It will contain all the items in an order as well as the order totals.
    */
   public ProductOrder getOrderDetails() {
      return orderDetails;
   }

   public Builder toBuilder() {
      return Builder.fromAddress(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (orderId ^ (orderId >>> 32));
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
      ProductOrderReceipt other = (ProductOrderReceipt) obj;
      if (orderId != other.orderId)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[orderId=" + orderId + ", orderDetails="+orderDetails+"]";
   }
   
   
}
