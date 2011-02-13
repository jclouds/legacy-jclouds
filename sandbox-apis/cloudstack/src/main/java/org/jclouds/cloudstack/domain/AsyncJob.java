/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.domain;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class AsyncJob {
   @SerializedName("accountid")
   private long accountId = -1;
   private String cmd;
   private Date created;
   @SerializedName("jobid")
   private long id = -1;
   @SerializedName("jobinstanceid")
   private long instanceId = -1;
   @SerializedName("jobinstancetype")
   private String instanceType;
   @SerializedName("jobinstancetype")
   private String progress;
   @SerializedName("jobresult")
   private Map<String, Object> result = ImmutableMap.of();
   @SerializedName("jobresultcode")
   private int resultCode = -1;
   @SerializedName("jobresulttype")
   private String resultType;
   @SerializedName("jobstatus")
   private int status = -1;
   @SerializedName("userid")
   private int userId = -1;

   public AsyncJob(int accountId, String cmd, Date created, long id, long instanceId, String instanceType,
         String progress, Map<String, Object> result, int resultCode, String resultType, int status, int userId) {
      this.accountId = accountId;
      this.cmd = cmd;
      this.created = created;
      this.id = id;
      this.instanceId = instanceId;
      this.instanceType = instanceType;
      this.progress = progress;
      this.result = result;
      this.resultCode = resultCode;
      this.resultType = resultType;
      this.status = status;
      this.userId = userId;
   }

   /**
    * present only for serializer
    * 
    */
   AsyncJob() {

   }

   /**
    * @return the account that executed the async command
    */
   public long getAccountId() {
      return accountId;
   }

   /**
    * @return the async command executed
    */
   public String getCmd() {
      return cmd;
   }

   /**
    * @return the created date of the job
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return async job ID
    */
   public long getId() {
      return id;
   }

   /**
    * @return the unique ID of the instance/entity object related to the job
    */
   public long getInstanceId() {
      return instanceId;
   }

   /**
    * @return the instance/entity object related to the job
    */
   public String getInstanceType() {
      return instanceType;
   }

   /**
    * @return the progress information of the PENDING job
    */
   public String getProgress() {
      return progress;
   }

   /**
    * @return the result reason
    */
   public Map<String, Object> getResult() {
      return result;
   }

   /**
    * @return the result code for the job
    */
   public int getResultCode() {
      return resultCode;
   }

   /**
    * @return the result type
    */
   public String getResultType() {
      return resultType;
   }

   /**
    * @return the current job status-should be 0 for PENDING
    */
   public int getStatus() {
      return status;
   }

   /**
    * @return the user that executed the async command
    */
   public int getUserId() {
      return userId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (accountId ^ (accountId >>> 32));
      result = prime * result + ((cmd == null) ? 0 : cmd.hashCode());
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (int) (instanceId ^ (instanceId >>> 32));
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((progress == null) ? 0 : progress.hashCode());
      result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
      result = prime * result + resultCode;
      result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
      result = prime * result + status;
      result = prime * result + userId;
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
      AsyncJob other = (AsyncJob) obj;
      if (accountId != other.accountId)
         return false;
      if (cmd == null) {
         if (other.cmd != null)
            return false;
      } else if (!cmd.equals(other.cmd))
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (id != other.id)
         return false;
      if (instanceId != other.instanceId)
         return false;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      if (progress == null) {
         if (other.progress != null)
            return false;
      } else if (!progress.equals(other.progress))
         return false;
      if (result == null) {
         if (other.result != null)
            return false;
      } else if (!result.equals(other.result))
         return false;
      if (resultCode != other.resultCode)
         return false;
      if (resultType == null) {
         if (other.resultType != null)
            return false;
      } else if (!resultType.equals(other.resultType))
         return false;
      if (status != other.status)
         return false;
      if (userId != other.userId)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[accountId=" + accountId + ", cmd=" + cmd + ", created=" + created + ", id=" + id + ", instanceId="
            + instanceId + ", instanceType=" + instanceType + ", progress=" + progress + ", result=" + result
            + ", resultCode=" + resultCode + ", resultType=" + resultType + ", status=" + status + ", userId=" + userId
            + "]";
   }

}
