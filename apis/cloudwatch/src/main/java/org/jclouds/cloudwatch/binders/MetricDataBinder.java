/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudwatch.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.StatisticValues;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;

/**
 * Binds the metrics request to the http request
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_PutMetricData.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class MetricDataBinder implements org.jclouds.rest.Binder {

   private final DateService dateService;

   @Inject
   protected MetricDataBinder(DateService dateService) {
      this.dateService = dateService;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Iterable<MetricDatum> metrics = (Iterable<MetricDatum>) checkNotNull(input, "metrics must be set!");
      
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();
      int metricDatumIndex = 1;

      for (MetricDatum metricDatum : metrics) {
         int dimensionIndex = 1;

         for (Dimension dimension : metricDatum.getDimensions()) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Dimensions.member." + dimensionIndex +
                                     ".Name", dimension.getName());
            formParameters.put("MetricData.member." + metricDatumIndex + ".Dimensions.member." + dimensionIndex +
                                     ".Value", dimension.getValue());
            dimensionIndex++;
         }

         formParameters.put("MetricData.member." + metricDatumIndex + ".MetricName", metricDatum.getMetricName());


         if (metricDatum.getStatisticValues().isPresent()) {
            StatisticValues statisticValues = metricDatum.getStatisticValues().get();

            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Maximum",
                               String.valueOf(statisticValues.getMaximum()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Minimum",
                               String.valueOf(statisticValues.getMinimum()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.SampleCount",
                               String.valueOf(statisticValues.getSampleCount()));
            formParameters.put("MetricData.member." + metricDatumIndex + ".StatisticValues.Sum",
                               String.valueOf(statisticValues.getSum()));
         }
         
         if (metricDatum.getTimestamp().isPresent()) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Timestamp",
                               dateService.iso8601SecondsDateFormat(metricDatum.getTimestamp().get()));
         }

         formParameters.put("MetricData.member." + metricDatumIndex + ".Unit",
                            String.valueOf(metricDatum.getUnit()));

         if (metricDatum.getValue().isPresent()) {
            formParameters.put("MetricData.member." + metricDatumIndex + ".Value",
                     String.valueOf(metricDatum.getValue().get()));
         }

         metricDatumIndex++;
      }

      return (R) request.toBuilder().replaceFormParams(formParameters.build()).build();
   }

}
