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
package org.jclouds.cloudwatch.binders;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.PutMetricData;
import org.jclouds.cloudwatch.domain.StatisticSet;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;

/**
 * Binds the metrics request to the http request
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_PutMetricData.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class PutMetricDataBinder implements org.jclouds.rest.Binder {

   private final DateService dateService;

   @Inject
   protected PutMetricDataBinder(DateService dateService) {
      this.dateService = dateService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      PutMetricData pmdRequest = PutMetricData.class.cast(input);
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();
      int metricDatumIndex = 1;

      formParameters.put("Namespace", pmdRequest.getNamespace());

      for (MetricDatum metricDatum : pmdRequest.getMetricData()) {
         int dimensionIndex = 1;
         StatisticSet statisticSet = metricDatum.getStatisticSet();

         for (Dimension dimension : metricDatum.getDimensions()) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Dimensions.member." + dimensionIndex +
                                     ".Name", dimension.getName());
            formParameters.put("MetricData.member." + metricDatumIndex + ".Dimensions.member." + dimensionIndex +
                                     ".Value", dimension.getValue());
            dimensionIndex++;
         }

         formParameters.put("MetricData.member." + metricDatumIndex + ".MetricName", metricDatum.getMetricName());

         if (statisticSet != null) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Maximum",
                               String.valueOf(statisticSet.getMaximum()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Minimum",
                               String.valueOf(statisticSet.getMinimum()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.SampleCount",
                               String.valueOf(statisticSet.getSampleCount()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Sum",
                               String.valueOf(statisticSet.getSum()));
         }
         if (metricDatum.getTimestamp() != null) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Timestamp",
                               dateService.iso8601SecondsDateFormat(metricDatum.getTimestamp()));
         }

         formParameters.put("MetricData.member." + metricDatumIndex + ".Unit", String.valueOf(metricDatum.getUnit()));

         if (metricDatum.getValue() != null) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Value", String.valueOf(metricDatum.getValue()));
         }

         metricDatumIndex++;
      }

      return ModifyRequest.putFormParams(request, formParameters.build());
   }

}
