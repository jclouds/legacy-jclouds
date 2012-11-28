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
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIOXMLNS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Represents an asynchronous or long-running task in the vCloud environment.
 * <p/>
 * <pre>
 * &lt;xs:complexType name="TaskType"&gt;
 * </pre>
 *
 *  TODO: this object and the hierarchy is wrong.  it is literally a Task with a Task container.  please review class diagram
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Task")
public class Task extends Entity {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.TASK;

   @XmlType(name = "TaskStatus")
   @XmlEnum(String.class)
   public static enum Status {
      /** The task has been queued for execution. */
      @XmlEnumValue("queued") QUEUED("queued"),
      /** The task is awaiting preprocessing or, if it is a blocking task, administrative action. */
      @XmlEnumValue("preRunning") PRE_RUNNING("preRunning"),
      /** The task is running. */
      @XmlEnumValue("running") RUNNING("running"),
      /** The task completed with a status of success. */
      @XmlEnumValue("success") SUCCESS("success"),
      /** The task encountered an error while running. */
      @XmlEnumValue("error") ERROR("error"),
      /** The task was canceled by the owner or an administrator. */
      @XmlEnumValue("canceled") CANCELED("canceled"),
      /** The task was aborted by an administrative action. */
      @XmlEnumValue("aborted") ABORTED("aborted"),
      @XmlEnumValue("") UNRECOGNIZED("unrecognized");

      public static final List<Status> ALL = ImmutableList.of(QUEUED, PRE_RUNNING, RUNNING, SUCCESS, ERROR, CANCELED, ABORTED);

      protected final String stringValue;

      Status(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, Status> STATUS_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(Status.values()), new Function<Status, String>() {
               @Override
               public String apply(Status input) {
                  return input.stringValue;
               }
            });

      public static Status fromValue(String value) {
         Status status = STATUS_BY_ID.get(checkNotNull(value, "stringValue"));
         return status == null ? UNRECOGNIZED : status;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromTask(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> extends Entity.Builder<B> {

      private Error error;
      private Reference org;
      private Reference owner;
      private Reference user;
      private Object params;
      private Integer progress;
      private Status status;
      private String operation;
      private String operationName;
      private Date startTime;
      private Date endTime;
      private Date expiryTime;

      /**
       * @see Task#getError()
       */
      public B error(Error error) {
         this.error = error;
         return self();
      }

      /**
       * @see Task#get()
       */
      public B org(Reference org) {
         this.org = org;
         return self();
      }

      /**
       * @see Task#getOwner()
       */
      public B owner(Reference owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see Task#getUser()
       */
      public B user(Reference user) {
         this.user = user;
         return self();
      }

      /**
       * @see Task#getParams()
       */
      public B params(Object params) {
         this.params = params;
         return self();
      }

      /**
       * @see Task#getProgress()
       */
      public B progress(Integer progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see Task#getStatus()
       */
      public B status(String status) {
         this.status = Status.fromValue(status);
         return self();
      }

      /**
       * @see Task#getStatus()
       */
      public B status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Task#getOperation()
       */
      public B operation(String operation) {
         this.operation = operation;
         return self();
      }

      /**
       * @see Task#getOperationName()
       */
      public B operationName(String operationName) {
         this.operationName = operationName;
         return self();
      }

      /**
       * @see Task#getStartTime()
       */
      public B startTime(Date startTime) {
         this.startTime = startTime;
         return self();
      }

      /**
       * @see Task#getEndTime()
       */
      public B endTime(Date endTime) {
         this.endTime = endTime;
         return self();
      }

      /**
       * @see Task#getExpiryTime()
       */
      public B expiryTime(Date expiryTime) {
         this.expiryTime = expiryTime;
         return self();
      }

      @Override
      public Task build() {
         return new Task(this);
      }

      public B fromTask(Task in) {
         return fromEntityType(in)
               .error(in.getError())
               .org(in.get())
               .progress(in.getProgress())
               .owner(in.getOwner())
               .user(in.getUser())
               .params(in.getParams())
               .status(in.getStatus())
               .operation(in.getOperation())
               .operationName(in.getOperationName())
               .startTime(in.getStartTime())
               .endTime(in.getEndTime())
               .expiryTime(in.getExpiryTime());
      }
   }

   protected Task(Builder<?> builder) {
      super(builder);
      this.error = builder.error;
      this.org = builder.org;
      this.progress = builder.progress;
      this.owner = builder.owner;
      this.user = builder.user;
      this.params = builder.params;
      this.status = builder.status;
      this.operation = builder.operation;
      this.operationName = builder.operationName;
      this.startTime = builder.startTime;
      this.endTime = builder.endTime;
      this.expiryTime = builder.expiryTime;
   }

   protected Task() {
      // for JAXB
   }

   @XmlElement(name = "Error")
   private Error error;
   @XmlElement(name = "Organization")
   private Reference org;
   @XmlElement(name = "Progress")
   private Integer progress;
   @XmlElement(name = "Owner")
   private Reference owner;
   @XmlElement(name = "User")
   private Reference user;
   @XmlElement(name = "Params")
   private Object params;
   @XmlAttribute
   private Status status;
   @XmlAttribute
   private String operation;
   @XmlAttribute
   private String operationName;
   @XmlAttribute
   private Date startTime;
   @XmlAttribute
   private Date endTime;
   @XmlAttribute
   private Date expiryTime;

   /**
    * Represents an error information if the task failed.
    */
   public Error getError() {
      return error;
   }

   /**
    * The organization that started the task.
    */
   public Reference get() {
      return org;
   }

   /**
    * Reference to the owner of the task.
    */
   public Reference getOwner() {
      return owner;
   }

   /**
    * The user who started the task.
    */
   public Reference getUser() {
      return user;
   }

   /**
    * The parameters with which this task has been run.
    */
   public Object getParams() {
      return params;
   }

   /**
    * The progress of a long running asynchronous task.
    * <p/>
    * The value is between 0 - 100. Not all tasks have progress, the value is not
    * present for task which progress is not available.
    */
   public Integer getProgress() {
      return progress;
   }

   /**
    * The execution status of the task.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * The display name of the operation that is tracked by this task.
    */
   public String getOperation() {
      return operation;
   }

   /**
    * The name of the operation that is tracked by this task.
    */
   public String getOperationName() {
      return operationName;
   }

   /**
    * The date and time the system started executing the task.
    * <p/>
    * May not be present if the task hasn't been executed yet.
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * The date and time that processing of the task was completed.
    * <p/>
    * May not be present if the task is still being executed.
    */
   public Date getEndTime() {
      return endTime;
   }

   /**
    * The date and time at which the task resource will be destroyed and no longer available for retrieval.
    * <p/>
    * May not be present if the task has not been executed or is still being executed.
    */
   public Date getExpiryTime() {
      return expiryTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Task that = Task.class.cast(o);
      return super.equals(that) && equal(this.getHref(), that.getHref());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), getHref());
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("error", error).add("org", org).add("progress", progress).add("status", status)
            .add("operation", operation).add("operationName", operationName).add("startTime", startTime)
            .add("endTime", endTime).add("expiryTime", expiryTime);
   }
}
