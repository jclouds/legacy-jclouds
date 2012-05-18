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

import com.google.common.base.Objects;
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

      public static ResultCode fromValue(String value) {
         try {
            int resultCode = Integer.parseInt(value);
            switch (resultCode) {
               case 0:
                  return SUCCESS;
               case 530:
                  return FAIL;
               default:
                  return UNKNOWN;
            }
         } catch(NumberFormatException e) {
            return UNKNOWN;
         }
      }
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

      public static Status fromValue(String value) {
         try {
            int statusCode = Integer.parseInt(value);
            switch (statusCode) {
               case 0:
                  return IN_PROGRESS;
               case 1:
                  return SUCCEEDED;
               case 2:
                  return FAILED;
               default:
                  return UNKNOWN;
            }
         } catch (NumberFormatException e) {
            return UNKNOWN;
         }
      }
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
      private ResultCode resultCode = ResultCode.UNKNOWN;
      private String resultType;
      private AsyncJobError error;
      private Status status = Status.UNKNOWN;
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

      public Builder<T> resultCode(ResultCode resultCode) {
         this.resultCode = resultCode;
         return this;
      }

      public Builder<T> resultType(String resultType) {
         this.resultType = resultType;
         return this;
      }

      public Builder<T> status(Status status) {
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
   private ResultCode resultCode = ResultCode.UNKNOWN;
   @SerializedName("jobresulttype")
   private String resultType;
   @SerializedName("jobstatus")
   private Status status = Status.UNKNOWN;
   @SerializedName("userid")
   private int userId = -1;
   private AsyncJobError error;

   public AsyncJob(long accountId, String cmd, Date created, long id, long instanceId, String instanceType,
         int progress, T result, ResultCode resultCode, String resultType, Status status, int userId, AsyncJobError error) {
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
   public ResultCode getResultCode() {
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
   public Status getStatus() {
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

   public boolean hasFailed() {
      return getError() != null || getResultCode() == ResultCode.FAIL || getStatus() == Status.FAILED;
   }

   public boolean hasSucceed() {
      return getError() == null && getResultCode() == ResultCode.SUCCESS && getStatus() == Status.SUCCEEDED;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(accountId, cmd, created, id, instanceId, instanceType, error, progress,
                               result, resultCode, resultType, status, userId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;

      AsyncJob<?> that = (AsyncJob<?>) obj;

      if (!Objects.equal(accountId, that.accountId)) return false;
      if (!Objects.equal(cmd, that.cmd)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(instanceId, that.instanceId)) return false;
      if (!Objects.equal(instanceType, that.instanceType)) return false;
      if (!Objects.equal(error, that.error)) return false;
      if (!Objects.equal(progress, that.progress)) return false;
      if (!Objects.equal(result, that.result)) return false;
      if (!Objects.equal(resultCode, that.resultCode)) return false;
      if (!Objects.equal(resultType, that.resultType)) return false;
      if (!Objects.equal(status, that.status)) return false;
      if (!Objects.equal(userId, that.userId)) return false;

      return true;
   }

   @Override
   public String toString() {
      return "AsyncJob{" +
            "accountId=" + accountId +
            ", cmd='" + cmd + '\'' +
            ", created=" + created +
            ", id=" + id +
            ", instanceId=" + instanceId +
            ", instanceType='" + instanceType + '\'' +
            ", progress=" + progress +
            ", result=" + result +
            ", resultCode=" + resultCode +
            ", resultType='" + resultType + '\'' +
            ", status=" + status +
            ", userId=" + userId +
            ", error=" + error +
            '}';
   }

}
