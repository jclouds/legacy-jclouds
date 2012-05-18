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

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class AsyncCreateResponse {
   public static final AsyncCreateResponse UNINITIALIZED = new AsyncCreateResponse();
   
   private long id = -1;
   @SerializedName("jobid")
   private long jobId = -1;

   /**
    * present only for serializer
    * 
    */
   AsyncCreateResponse() {

   }

   public AsyncCreateResponse(long id, long jobId) {
      this.id = id;
      this.jobId = jobId;
   }

   /**
    * @return id of the resource being created
    */
   public long getId() {
      return id;
   }

   /**
    * @return id of the job in progress
    */
   public long getJobId() {
      return jobId;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, jobId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AsyncCreateResponse that = (AsyncCreateResponse) obj;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;

      return true;
   }

   @Override
   public String toString() {
      return "AsyncCreateResponse{" +
            "id=" + id +
            ", jobId=" + jobId +
            '}';
   }

}
