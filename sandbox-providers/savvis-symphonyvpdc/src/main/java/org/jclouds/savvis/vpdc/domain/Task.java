package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * The result of a client request cannot be returned immediately, the server creates a task entity
 * and returns its URL to the client. The client can use this URL in a subsequent GET request to
 * obtain the current status of the task.
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/getTask.html" />
 */
public class Task extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Resource.Builder {
      private Status status;
      private Date startTime;
      private Date endTime;
      private Resource owner;
      private Resource result;
      private TaskError error;

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      public Builder endTime(Date endTime) {
         this.endTime = endTime;
         return this;
      }

      public Builder owner(Resource owner) {
         this.owner = owner;
         return this;
      }

      public Builder result(Resource result) {
         this.result = result;
         return this;
      }

      public Builder error(TaskError error) {
         this.error = error;
         return this;
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      @Override
      public Task build() {
         return new Task(id, name, type, href, status, startTime, endTime, owner, result, error);
      }

      public static Builder fromTask(Task in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
               .status(in.getStatus()).startTime(in.getStartTime()).endTime(in.getEndTime()).owner(in.getOwner())
               .error(in.getError()).result(in.getResult());
      }
   }

   public enum Status {
      /**
       * Savvis VPDC successfully provisioned and available for use.
       */
      SUCCESS,
      /**
       * Savvis VPDC is processing the user request, please wait for the provisioning process to
       * complete.
       */
      RUNNING,

      /**
       * Savvis VPDC is processing the user request, please wait for the provisioning process to
       * complete.
       */
      QUEUED,
      /**
       * Savvis VPDC is failed, please kindly contact Savvis administrator for further
       * clarification/assistance with the respective request data.
       */
      ERROR,
      /**
       * Unexpected Savvus VPDC Status, Savvis VPDC is failed, please kindly contact Savvis
       * administrator for further clarification/assistance with the respective request data.
       */
      NONE, UNRECOGNIZED;
      public String value() {
         return name().toLowerCase();
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   private final Status status;
   private final Date startTime;
   private final Date endTime;
   private final Resource owner;
   private final Resource result;
   private final TaskError error;

   public Task(String id, String name, String type, URI href, Status status, Date startTime, Date endTime,
         Resource result, Resource owner, TaskError error) {
      super(id, name, type, href);
      this.status = status;
      this.startTime = startTime;
      this.endTime = endTime;
      this.owner = owner;
      this.result = result;
      this.error = error;
   }

   /**
    * The current status of the task.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * date and time when the task was started.
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * date and time when the task completed. Does not appear for running tasks.
    */
   @Nullable
   public Date getEndTime() {
      return endTime;
   }

   /**
    * A link to the object that owns the task. For copy operations, the owner is the copy that is
    * being created. For delete operations, the owner is the deleted object, so this element is not
    * included. For all other operations, the owner is the object to which the request was made.
    */
   @Nullable
   public Resource getOwner() {
      return owner;
   }

   /**
    * Result is represent outcome of request url. if any VM related operation, the result will
    * present as get VApp. if any VMDK related operation, the result will present as get VMKD
    */
   @Nullable
   public Resource getResult() {
      return result;
   }

   /**
    * error message or related information returned by the task
    */
   @Nullable
   public TaskError getError() {
      return error;
   }

   public Builder toBuilder() {
      return Builder.fromTask(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", type=" + type + ", href=" + href + ", status=" + status
            + ", startTime=" + startTime + ", endTime=" + endTime + ", owner=" + owner + ", result=" + result
            + ", error=" + error + "]";
   }

}