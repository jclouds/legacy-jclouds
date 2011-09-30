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

      public Builder orderId(int orderId) {
         this.orderId = orderId;
         return this;
      }

      public ProductOrderReceipt build() {
         return new ProductOrderReceipt(orderId);
      }

      public static Builder fromAddress(ProductOrderReceipt in) {
         return ProductOrderReceipt.builder().orderId(in.getOrderId());
      }
   }

   private int orderId = -1;

   // for deserializer
   ProductOrderReceipt() {

   }

   public ProductOrderReceipt(int orderId) {
      this.orderId = orderId;
   }

   @Override
   public int compareTo(ProductOrderReceipt arg0) {
      return new Integer(orderId).compareTo(arg0.getOrderId());
   }

   /**
    * @return unique identifier for the order.
    */
   public int getOrderId() {
      return orderId;
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
      return "[orderId=" + orderId + "]";
   }
   
   
}
