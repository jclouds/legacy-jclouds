package org.jclouds.cloudwatch.domain;

import com.google.common.base.CaseFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_GetMetricStatistics.html?r=5424"/>
 *
 * @author Andrei Savu
 */
public enum Statistics {
   AVERAGE,
   SUM,
   SAMPLE_COUNT,
   MAXIMUM,
   MINIMUM,
   UNRECOGNIZED;

   public String value() {
      return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name()));
   }

   @Override
   public String toString() {
      return value();
   }

   public static Statistics fromValue(String value) {
      try {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(value, "value")));

      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
