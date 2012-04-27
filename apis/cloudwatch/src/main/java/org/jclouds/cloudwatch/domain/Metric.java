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
package org.jclouds.cloudwatch.domain;

import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;

import java.util.Iterator;
import java.util.Set;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_Metric.html" />
 *
 * @author Jeremy Whitlock
 */
public class Metric {

   private final Set<Dimension> dimensions;
   private final String metricName;
   private final String namespace;

   public Metric (String metricName, String namespace, @Nullable Set<Dimension> dimensions) {
      // Default to an empty set
      if (dimensions == null) {
         this.dimensions = Sets.newLinkedHashSet();
      } else {
         this.dimensions = dimensions;
      }

      this.metricName = metricName;
      this.namespace = namespace;
   }

   /**
    * return the metric name for the metric.
    */
   public String getMetricName() {
      return metricName;
   }

   /**
    * return the namespace for the metric
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * return the available dimensions for the metric
    */
   @Nullable
   public Set<Dimension> getDimensions() {
      return dimensions;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("[metricName=")
             .append(metricName)
             .append(", namespace=")
             .append(namespace);


      builder.append(", dimensions=[");

      Iterator<Dimension> iterator = dimensions.iterator();

      while (iterator.hasNext()) {
         builder.append(iterator.next());

         if (iterator.hasNext()) {
            builder.append(", ");
         }
      }

      builder.append("]]");

      return builder.toString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Metric other = (Metric)obj;
      if (metricName == null) {
         if (other.metricName != null)
            return false;
      } else if (!metricName.equals(other.metricName))
         return false;
      if (namespace == null) {
         if (other.namespace != null)
            return false;
      } else if (!namespace.equals(other.namespace))
         return false;
      if (dimensions == null) {
         if (other.dimensions != null)
            return false;
      } else if (!dimensions.equals(other.dimensions))
         return false;
     return true;
  }

}
