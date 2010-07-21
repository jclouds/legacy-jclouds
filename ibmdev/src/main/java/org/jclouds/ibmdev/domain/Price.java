/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Date;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class Price {

   private double rate;
   private String unitOfMeasure;
   private String countryCode;
   private Date effectiveDate;
   private String currencyCode;
   private double pricePerQuantity;

   Price() {

   }

   public Price(double rate, String unitOfMeasure, String countryCode, Date effectiveDate, String currencyCode,
         double pricePerQuantity) {
      this.rate = rate;
      this.unitOfMeasure = unitOfMeasure;
      this.countryCode = countryCode;
      this.effectiveDate = effectiveDate;
      this.currencyCode = currencyCode;
      this.pricePerQuantity = pricePerQuantity;
   }

   public double getRate() {
      return rate;
   }

   public String getUnitOfMeasure() {
      return unitOfMeasure;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public Date getEffectiveDate() {
      return effectiveDate;
   }

   public String getCurrencyCode() {
      return currencyCode;
   }

   public double getPricePerQuantity() {
      return pricePerQuantity;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
      result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
      result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
      long temp;
      temp = Double.doubleToLongBits(pricePerQuantity);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(rate);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
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
      Price other = (Price) obj;
      if (countryCode == null) {
         if (other.countryCode != null)
            return false;
      } else if (!countryCode.equals(other.countryCode))
         return false;
      if (currencyCode == null) {
         if (other.currencyCode != null)
            return false;
      } else if (!currencyCode.equals(other.currencyCode))
         return false;
      if (effectiveDate == null) {
         if (other.effectiveDate != null)
            return false;
      } else if (!effectiveDate.equals(other.effectiveDate))
         return false;
      if (Double.doubleToLongBits(pricePerQuantity) != Double.doubleToLongBits(other.pricePerQuantity))
         return false;
      if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
         return false;
      if (unitOfMeasure == null) {
         if (other.unitOfMeasure != null)
            return false;
      } else if (!unitOfMeasure.equals(other.unitOfMeasure))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[countryCode=" + countryCode + ", currencyCode=" + currencyCode + ", effectiveDate=" + effectiveDate
            + ", pricePerQuantity=" + pricePerQuantity + ", rate=" + rate + ", unitOfMeasure=" + unitOfMeasure + "]";
   }
}