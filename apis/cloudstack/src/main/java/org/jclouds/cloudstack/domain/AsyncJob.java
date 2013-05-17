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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class AsyncJob
 *
 * @author Adrian Cole
 */
public class AsyncJob<S> {

   /**
    * Valid job result codes
    */
   public static enum ResultCode {
      SUCCESS(0),
      FAIL(530),
      UNKNOWN(-1);

      private final int code;

      private ResultCode(int code) {
         this.code = code;
      }

      public int code() {
         return this.code;
      }

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
         } catch (NumberFormatException e) {
            return UNKNOWN;
         }
      }
   }

   /**
    * Valid async job statuses
    */
   public static enum Status {
      IN_PROGRESS(0),
      SUCCEEDED(1),
      FAILED(2),
      UNKNOWN(-1);

      private final int code;

      private Status(int code) {
         this.code = code;
      }

      public int code() {
         return this.code;
      }

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

   public static <T> Builder<?, T> builder() {
      return new ConcreteBuilder<T>();
   }

   public Builder toBuilder() {
      return new ConcreteBuilder<S>().fromAsyncJob(this);
   }

   public abstract static class Builder<T extends Builder<T, S>, S> {
      protected abstract T self();

      protected String accountId;
      protected String cmd;
      protected Date created;
      protected String id;
      protected String instanceId;
      protected String instanceType;
      protected int progress;
      protected S result;
      protected AsyncJob.ResultCode resultCode;
      protected String resultType;
      protected AsyncJob.Status status;
      protected String userId;
      protected AsyncJobError error;

      /**
       * @see AsyncJob#getAccountId()
       */
      public T accountId(String accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see AsyncJob#getCmd()
       */
      public T cmd(String cmd) {
         this.cmd = cmd;
         return self();
      }

      /**
       * @see AsyncJob#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see AsyncJob#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see AsyncJob#getInstanceId()
       */
      public T instanceId(String instanceId) {
         this.instanceId = instanceId;
         return self();
      }

      /**
       * @see AsyncJob#getInstanceType()
       */
      public T instanceType(String instanceType) {
         this.instanceType = instanceType;
         return self();
      }

      /**
       * @see AsyncJob#getProgress()
       */
      public T progress(int progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see AsyncJob#getResult()
       */
      public T result(S result) {
         this.result = result;
         return self();
      }

      /**
       * @see AsyncJob#getResultCode()
       */
      public T resultCode(AsyncJob.ResultCode resultCode) {
         this.resultCode = resultCode;
         return self();
      }

      /**
       * @see AsyncJob#getResultType()
       */
      public T resultType(String resultType) {
         this.resultType = resultType;
         return self();
      }

      /**
       * @see AsyncJob#getStatus()
       */
      public T status(AsyncJob.Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see AsyncJob#getUserId()
       */
      public T userId(String userId) {
         this.userId = userId;
         return self();
      }

      /**
       * @see AsyncJob#getError()
       */
      public T error(AsyncJobError error) {
         this.error = error;
         return self();
      }

      public AsyncJob build() {
         return new AsyncJob<S>(accountId, cmd, created, id, instanceId, instanceType, progress, result, resultCode,
               resultType, status, userId, error);
      }

      public T fromAsyncJob(AsyncJob<S> in) {
         return this
               .accountId(in.getAccountId())
               .cmd(in.getCmd())
               .created(in.getCreated())
               .id(in.getId())
               .instanceId(in.getInstanceId())
               .instanceType(in.getInstanceType())
               .progress(in.getProgress())
               .result(in.getResult())
               .resultCode(in.getResultCode())
               .resultType(in.getResultType())
               .status(in.getStatus())
               .userId(in.getUserId())
               .error(in.getError());
      }

      public static Builder<?, Object> fromAsyncJobUntyped(AsyncJob in) {
         return new ConcreteBuilder().fromAsyncJob(in);
      }
   }

   private static class ConcreteBuilder<T> extends Builder<ConcreteBuilder<T>, T> {
      @Override
      protected ConcreteBuilder<T> self() {
         return this;
      }
   }

   private final String accountId;
   private final String cmd;
   private final Date created;
   private final String id;
   private final String instanceId;
   private final String instanceType;
   private final int progress;
   private final S result;
   private final AsyncJob.ResultCode resultCode;
   private final String resultType;
   private final AsyncJob.Status status;
   private final String userId;
   private final AsyncJobError error;

   @ConstructorProperties({
         "accountid", "cmd", "created", "jobid", "jobinstanceid", "jobinstancetype", "jobprocstatus", "jobresult",
         "jobresultcode", "jobresulttype", "jobstatus", "userid", "error"
   })
   protected AsyncJob(@Nullable String accountId, @Nullable String cmd, @Nullable Date created, String id,
                      @Nullable String instanceId, @Nullable String instanceType, int progress, @Nullable S result,
                      @Nullable AsyncJob.ResultCode resultCode, @Nullable String resultType, @Nullable AsyncJob.Status status,
                      @Nullable String userId, @Nullable AsyncJobError error) {
      this.accountId = accountId;
      this.cmd = cmd;
      this.created = created;
      this.id = checkNotNull(id, "id");
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
    * @return the account that executed the async command
    */
   @Nullable
   public String getAccountId() {
      return this.accountId;
   }

   /**
    * @return the async command executed
    */
   @Nullable
   public String getCmd() {
      return this.cmd;
   }

   /**
    * @return the created date of the job
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return async job ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the unique ID of the instance/entity object related to the job
    */
   @Nullable
   public String getInstanceId() {
      return this.instanceId;
   }

   /**
    * @return the instance/entity object related to the job
    */
   @Nullable
   public String getInstanceType() {
      return this.instanceType;
   }

   /**
    * @return the progress information of the PENDING job
    */
   public int getProgress() {
      return this.progress;
   }

   /**
    * @return the result reason
    */
   @Nullable
   public S getResult() {
      return this.result;
   }

   /**
    * @return the result code for the job
    */
   @Nullable
   public AsyncJob.ResultCode getResultCode() {
      return this.resultCode;
   }

   /**
    * @return the result type
    */
   @Nullable
   public String getResultType() {
      return this.resultType;
   }

   /**
    * @return the current job status-should be 0 for PENDING
    */
   @Nullable
   public AsyncJob.Status getStatus() {
      return this.status;
   }

   /**
    * @return the user that executed the async command
    */
   @Nullable
   public String getUserId() {
      return this.userId;
   }

   /**
    * @return the error related to this command, or null if no error or error
    *         not yet encountered.
    */
   @Nullable
   public AsyncJobError getError() {
      return this.error;
   }

   public boolean hasFailed() {
      return getError() != null || getResultCode() == ResultCode.FAIL || getStatus() == Status.FAILED;
   }

   public boolean hasSucceed() {
      return getError() == null && getResultCode() == ResultCode.SUCCESS && getStatus() == Status.SUCCEEDED;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accountId, cmd, created, id, instanceId, instanceType, progress, result, resultCode, resultType, status, userId, error);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AsyncJob that = AsyncJob.class.cast(obj);
      return Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.cmd, that.cmd)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.id, that.id)
            && Objects.equal(this.instanceId, that.instanceId)
            && Objects.equal(this.instanceType, that.instanceType)
            && Objects.equal(this.progress, that.progress)
            && Objects.equal(this.result, that.result)
            && Objects.equal(this.resultCode, that.resultCode)
            && Objects.equal(this.resultType, that.resultType)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.userId, that.userId)
            && Objects.equal(this.error, that.error);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("accountId", accountId).add("cmd", cmd).add("created", created).add("id", id).add("instanceId", instanceId)
            .add("instanceType", instanceType).add("progress", progress).add("result", result).add("resultCode", resultCode)
            .add("resultType", resultType).add("status", status).add("userId", userId).add("error", error);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
