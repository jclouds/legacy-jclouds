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
package org.jclouds.gogrid.options;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.END_DATE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.JOB_OBJECT_TYPE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.JOB_STATE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.MAX_NUMBER_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.OBJECT_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.OWNER_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.START_DATE_KEY;

import java.util.Date;

import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Oleksiy Yarmula
 */
public class GetJobListOptions extends BaseHttpRequestOptions {

   public static final GetJobListOptions NONE = new GetJobListOptions();

   public GetJobListOptions maxItemsNumber(Integer maxNumber) {
      checkState(!queryParameters.containsKey(MAX_NUMBER_KEY), "Can't have duplicate parameter of max returned items");
      queryParameters.put(MAX_NUMBER_KEY, maxNumber.toString());
      return this;
   }

   public GetJobListOptions withStartDate(Date startDate) {
      checkState(!queryParameters.containsKey(START_DATE_KEY), "Can't have duplicate start date for filtering");
      queryParameters.put(START_DATE_KEY, String.valueOf(startDate.getTime()));
      return this;
   }

   public GetJobListOptions withEndDate(Date endDate) {
      checkState(!queryParameters.containsKey(END_DATE_KEY), "Can't have duplicate end date for filtering");
      queryParameters.put(END_DATE_KEY, String.valueOf(endDate.getTime()));
      return this;
   }

   public GetJobListOptions withOwner(String owner) {
      checkState(!queryParameters.containsKey(OWNER_KEY), "Can't have duplicate owner name for filtering");
      queryParameters.put(OWNER_KEY, owner);
      return this;
   }

   public GetJobListOptions onlyForState(JobState jobState) {
      checkState(!queryParameters.containsKey(JOB_STATE_KEY), "Can't have duplicate job state for filtering");
      queryParameters.put(JOB_STATE_KEY, jobState.toString());
      return this;
   }

   public GetJobListOptions onlyForObjectType(ObjectType objectType) {
      checkState(!queryParameters.containsKey(JOB_OBJECT_TYPE_KEY), "Can't have duplicate object type for filtering");
      queryParameters.put(JOB_OBJECT_TYPE_KEY, objectType.toString());
      return this;
   }

   public GetJobListOptions onlyForObjectName(String objectName) {
      checkState(!queryParameters.containsKey(OBJECT_KEY), "Can't have duplicate object name for filtering");
      queryParameters.put(OBJECT_KEY, objectName);
      return this;
   }

   public GetJobListOptions latestJobForObjectByName(String serverName) {
      return maxItemsNumber(1).onlyForObjectName(serverName);
   }

   /*
    * This method is intended for testing
    */
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      GetJobListOptions options = (GetJobListOptions) o;

      return buildQueryParameters().equals(options.buildQueryParameters());
   }

   public static class Builder {

      public static GetJobListOptions maxItems(int maxNumber) {
         return new GetJobListOptions().maxItemsNumber(maxNumber);
      }
      
      public static GetJobListOptions startDate(Date startDate){
         return new GetJobListOptions().withStartDate(startDate);
      }

      public static GetJobListOptions latestJobForObjectByName(String serverName) {
         return new GetJobListOptions().latestJobForObjectByName(serverName);
      }
   }

}
