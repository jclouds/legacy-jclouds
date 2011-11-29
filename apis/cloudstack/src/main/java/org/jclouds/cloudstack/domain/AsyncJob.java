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

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class AsyncJob<T> {

   /**
    * Valid job result codes
    */
   public static enum ResultCode {
      SUCCESS (0),
      FAIL (530),
      UNKNOWN (-1);

      private final int code;

      private ResultCode(int code) {
         this.code = code;
      }

      public int code() { return this.code; }
   }

   /**
    * Valid async job statuses
    */
   public static enum Status {
      IN_PROGRESS (0),
      SUCCEEDED (1),
      FAILED (2),
      UNKNOWN (-1);

      private final int code;

      private Status(int code) {
         this.code = code;
      }

      public int code() { return this.code; }
   }

   public static <T> Builder<T> builder() {
      return new Builder<T>();
   }

   public static class Builder<T> {
      private long accountId = -1;
      private String cmd;
      private Date created;
      private long id = -1;
      private long instanceId = -1;
      private String instanceType;
      private int progress = -1;
      private T result;
      private int resultCode = -1;
      private String resultType;
      private AsyncJobError error;
      private int status = -1;
      private int userId = -1;

      public Builder<T> accountId(long accountId) {
         this.accountId = accountId;
         return this;
      }

      public Builder<T> cmd(String cmd) {
         this.cmd = cmd;
         return this;
      }

      public Builder<T> created(Date created) {
         this.created = created;
         return this;
      }

      public Builder<T> id(long id) {
         this.id = id;
         return this;
      }

      public Builder<T> instanceId(long instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      public Builder<T> error(AsyncJobError error) {
         this.error = error;
         return this;
      }

      public Builder<T> instanceType(String instanceType) {
         this.instanceType = instanceType;
         return this;
      }

      public Builder<T> progress(int progress) {
         this.progress = progress;
         return this;
      }

      public Builder<T> result(T result) {
         this.result = result;
         return this;
      }

      public Builder<T> resultCode(int resultCode) {
         this.resultCode = resultCode;
         return this;
      }

      public Builder<T> resultType(String resultType) {
         this.resultType = resultType;
         return this;
      }

      public Builder<T> status(int status) {
         this.status = status;
         return this;
      }

      public Builder<T> userId(int userId) {
         this.userId = userId;
         return this;
      }

      public AsyncJob<T> build() {
         return new AsyncJob<T>(accountId, cmd, created, id, instanceId, instanceType, progress, result, resultCode,
               resultType, status, userId, error);
      }

      public static <T> Builder<T> fromAsyncJobUntyped(AsyncJob<T> in) {
         return new Builder<T>().accountId(in.accountId).cmd(in.cmd).created(in.created).id(in.id)
               .instanceId(in.instanceId).instanceType(in.instanceType).progress(in.progress).result(in.result)
               .resultCode(in.resultCode).resultType(in.resultType).status(in.status).userId(in.userId).error(in.error);
      }
   }

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
   @SerializedName("jobprocstatus")
   private int progress = -1;
   @SerializedName("jobresult")
   private T result;
   @SerializedName("jobresultcode")
   private int resultCode = -1;
   @SerializedName("jobresulttype")
   private String resultType;
   @SerializedName("jobstatus")
   private int status = -1;
   @SerializedName("userid")
   private int userId = -1;
   private AsyncJobError error;

   public AsyncJob(long accountId, String cmd, Date created, long id, long instanceId, String instanceType,
         int progress, T result, int resultCode, String resultType, int status, int userId, AsyncJobError error) {
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
      this.error = error;
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
   public int getProgress() {
      return progress;
   }

   /**
    * @return the result reason
    */
   public T getResult() {
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

   /**
    * 
    * 
    * @return the error related to this command, or null if no error or error
    *         not yet encountered.
    */
   public AsyncJobError getError() {
      return error;
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
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + progress;
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
      AsyncJob<?> other = (AsyncJob<?>) obj;
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
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (progress != other.progress)
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
            + instanceId + ", instanceType=" + instanceType + ", error=" + error + ", progress=" + progress
            + ", result=" + result + ", resultCode=" + resultCode + ", resultType=" + resultType + ", status=" + status
            + ", userId=" + userId + "]";
   }

}
