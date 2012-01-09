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
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * The result of an operation.
 *
 * A handful of Cloudstack API calls return this structure when there is no domain model data to return - for example,
 * when deleting an object.
 *
 * @author Richard Downer
 */
public class JobResult implements Comparable<JobResult> {

   private boolean success;
   @SerializedName("displaytext")
   private String displayText;

   /**
    * present only for the serializer
    */
   JobResult() {
   }

   public JobResult(boolean success, String displayText) {
      this.success = success;
      this.displayText = displayText;
   }

   public boolean getSuccess() {
      return success;
   }

   public String getDisplayText() {
      return displayText;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      JobResult that = (JobResult) o;

      if (success != that.success) return false;
      if (displayText != null ? !displayText.equals(that.displayText) : that.displayText != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (success ? 1 : 0);
      result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "JobResult{" +
            "success=" + success +
            ", displayText='" + displayText + '\'' +
            '}';
   }

   @Override
   public int compareTo(JobResult other) {
      int comparison = Boolean.valueOf(success).compareTo(other.success);
      if (comparison == 0)
         comparison = displayText.compareTo(other.displayText);
      return comparison;
   }
}
