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

import javax.inject.Inject;

import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;

/**
 * Binds the metrics request to the http request
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_GetMetricStatistics.html"
 *      />
 * 
 * @author Jeremy Whitlock, Adrian Cole
 */
@Beta
public class GetMetricStatisticsBinder implements org.jclouds.rest.Binder {

   private final DateService dateService;
   
   @Inject
   protected GetMetricStatisticsBinder(DateService dateService){
      this.dateService = dateService;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      GetMetricStatistics getRequest = GetMetricStatistics.class.cast(checkNotNull(payload,
               "GetMetricStatistics must be set!"));
      int dimensionIndex = 1;
      int statisticIndex = 1;
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();

      for (Dimension dimension : getRequest.getDimensions()) {
         formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
         formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
         dimensionIndex++;
      }

      if (getRequest.getEndTime().isPresent()) {
         formParameters.put("EndTime", dateService.iso8601SecondsDateFormat(getRequest.getEndTime().get()));
      }
      formParameters.put("MetricName", getRequest.getMetricName());
      formParameters.put("Namespace", getRequest.getNamespace());
      formParameters.put("Period", Integer.toString(getRequest.getPeriod()));
      if (getRequest.getStartTime().isPresent()) {
         formParameters.put("StartTime", dateService.iso8601SecondsDateFormat(getRequest
                  .getStartTime().get()));
      }
      
      for (Statistics statistic : getRequest.getStatistics()) {
         formParameters.put("Statistics.member." + statisticIndex, statistic.toString());
         statisticIndex++;
      }

      if (getRequest.getUnit().isPresent()) {
         formParameters.put("Unit", getRequest.getUnit().get().toString());
      }

      return (R) request.toBuilder().replaceFormParams(formParameters.build()).build();
   }

}
