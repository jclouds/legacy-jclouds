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
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.GetMetricStatistics;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

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
   
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      GetMetricStatistics getRequest = GetMetricStatistics.class.cast(payload);
      Set<Dimension> dimensions = getRequest.getDimensions() != null ?
            getRequest.getDimensions() :
            new HashSet<Dimension>();
      Set<Statistics> statistics = getRequest.getStatistics() != null ?
            getRequest.getStatistics() :
            new HashSet<Statistics>();
      int dimensionIndex = 1;
      int statisticIndex = 1;
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();

      for (Dimension dimension : dimensions) {
         formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
         formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
         dimensionIndex++;
      }

      if (getRequest.getEndTime() != null) {
         formParameters.put("EndTime", dateService.iso8601SecondsDateFormat(getRequest.getEndTime()));
      }
      formParameters.put("MetricName", getRequest.getMetricName());
      formParameters.put("Namespace", getRequest.getNamespace());
      formParameters.put("Period", Integer.toString(getRequest.getPeriod()));
      if (getRequest.getStartTime() != null) {
         formParameters.put("StartTime", dateService.iso8601SecondsDateFormat(getRequest
                  .getStartTime()));
      }

      for (Statistics statistic : statistics) {
         formParameters.put("Statistics.member." + statisticIndex, statistic.toString());
         statisticIndex++;
      }

      if (getRequest.getUnit() != null) {
         formParameters.put("Unit", getRequest.getUnit().toString());
      }

      return ModifyRequest.putFormParams(request, formParameters.build());
   }

}