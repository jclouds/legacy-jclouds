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
package org.jclouds.route53.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jclouds.route53.domain.ChangeBatch.ActionOnResourceRecordSet;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
public class ChangeBatch extends ForwardingList<ActionOnResourceRecordSet> {

   public static ChangeBatch createAll(Iterable<ResourceRecordSet> toCreate) {
      return builder().createAll(toCreate).build();
   }

   public static ChangeBatch deleteAll(Iterable<ResourceRecordSet> toDelete) {
      return builder().deleteAll(toDelete).build();
   }

   private final Optional<String> comment;
   private final List<ActionOnResourceRecordSet> changes;

   public static enum Action {
      CREATE, DELETE;
   }

   public static class ActionOnResourceRecordSet {
      private final Action action;
      private final ResourceRecordSet rrs;

      private ActionOnResourceRecordSet(Action action, ResourceRecordSet rrs) {
         this.action = action;
         this.rrs = rrs;
      }

      public Action getAction() {
         return action;
      }

      public ResourceRecordSet getRRS() {
         return rrs;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(action, rrs);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         ActionOnResourceRecordSet that = ActionOnResourceRecordSet.class.cast(obj);
         return equal(this.action, that.action) && equal(this.rrs, that.rrs);
      }

      @Override
      public String toString() {
         return toStringHelper("").omitNullValues().add("action", action).add("rrs", rrs).toString();
      }
   }

   private ChangeBatch(Optional<String> comment, ImmutableList<ActionOnResourceRecordSet> changes) {
      this.comment = checkNotNull(comment, "comment");
      this.changes = checkNotNull(changes, "changes%s", comment.isPresent() ? " for %s " + comment.get() : "");
      checkArgument(!changes.isEmpty(), "no changes%s", comment.isPresent() ? " for %s " + comment.get() : "");
   }

   /**
    * Any comments you want to include about the changes in this change batch.
    */
   public Optional<String> getComment() {
      return comment;
   }

   @Override
   protected List<ActionOnResourceRecordSet> delegate() {
      return changes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(comment, changes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ChangeBatch that = ChangeBatch.class.cast(obj);
      return equal(this.comment, that.comment) && equal(this.changes, that.changes);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("comment", comment.orNull()).add("changes", changes).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private Optional<String> comment = Optional.absent();
      private ImmutableList.Builder<ActionOnResourceRecordSet> changes = ImmutableList.builder();

      /**
       * @see ChangeBatch#getComment()
       */
      public Builder comment(String comment) {
         this.comment = Optional.fromNullable(comment);
         return this;
      }

      public Builder create(ResourceRecordSet rrs) {
         this.changes.add(new ActionOnResourceRecordSet(Action.CREATE, rrs));
         return this;
      }

      public Builder createAll(Iterable<ResourceRecordSet> toCreate) {
         for (ResourceRecordSet rrs : toCreate)
            create(rrs);
         return this;
      }

      public Builder delete(ResourceRecordSet rrs) {
         this.changes.add(new ActionOnResourceRecordSet(Action.DELETE, rrs));
         return this;
      }

      public Builder deleteAll(Iterable<ResourceRecordSet> toDelete) {
         for (ResourceRecordSet rrs : toDelete)
            delete(rrs);
         return this;
      }

      public ChangeBatch build() {
         return new ChangeBatch(comment, changes.build());
      }
   }
}
