/*
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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecompute.options.ListOptions;

import java.util.Iterator;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static org.jclouds.googlecompute.domain.Resource.Kind;

/**
 * The collection returned from any <code>list()</code> method.
 *
 * @author David Alves
 */
public class PagedList<T> extends IterableWithMarker<T> {

   public static <E> Builder<E, ?> builder() {
      return new ConcreteBuilder<E>();
   }

   public Builder<?, ?> toBuilder() {
      return new ConcreteBuilder<T>().fromPagedList(this);
   }

   public abstract static class Builder<E, T extends Builder<E, T>> {

      protected abstract T self();

      private Kind kind;
      private String id;
      private String selfLink;
      private String nextPageToken;
      private ListOptions listOptions;
      private ImmutableSet.Builder<E> items = ImmutableSet.builder();

      public T kind(Kind kind) {
         this.kind = kind;
         return self();
      }

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T selfLink(String selfLink) {
         this.selfLink = selfLink;
         return self();
      }

      public T addItem(E item) {
         this.items.add(item);
         return self();
      }

      public T items(Iterable<E> items) {
         this.items.addAll(items);
         return self();
      }

      public T nextPageToken(String nextPageToken) {
         this.nextPageToken = nextPageToken;
         return self();
      }

      public T pagingOptions(ListOptions listOptions) {
         this.listOptions = listOptions;
         return self();
      }

      public PagedList<E> build() {
         return new PagedList<E>(kind, id, selfLink, nextPageToken, listOptions, items.build());
      }

      public T fromPagedList(PagedList<E> in) {
         return this
                 .kind(in.getKind())
                 .id(in.getId())
                 .selfLink(in.getSelfLink())
                 .nextPageToken(in.getNextPageToken())
                 .pagingOptions(in.getListOptions())
                 .items(in);

      }
   }

   private static class ConcreteBuilder<E> extends Builder<E, ConcreteBuilder<E>> {

      @Override
      protected ConcreteBuilder<E> self() {
         return this;
      }
   }

   private final Kind kind;
   private final String id;
   private final String selfLink;
   private final String nextPageToken;
   private final ListOptions listOptions;
   private final Iterable<T> items;

   protected PagedList(Kind kind, String id, String selfLink, String nextPageToken,
                       ListOptions listOptions, Iterable<T> items) {
      this.kind = kind;
      this.id = id;
      this.selfLink = selfLink;
      this.nextPageToken = nextPageToken;
      this.listOptions = listOptions;
      this.items = items;
   }

   public Kind getKind() {
      return kind;
   }

   public String getId() {
      return id;
   }

   public String getSelfLink() {
      return selfLink;
   }

   public String getNextPageToken() {
      return nextPageToken;
   }

   public ListOptions getListOptions() {
      return listOptions;
   }

   @Override
   public Optional<Object> nextMarker() {
      return nextPageToken != null ? Optional.<Object>of(nextPageToken) : Optional.absent();
   }

   @Override
   public Iterator<T> iterator() {
      return items.iterator();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, selfLink, nextPageToken);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PagedList that = PagedList.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.id, that.id)
              && equal(this.selfLink, that.selfLink)
              && equal(this.nextPageToken, that.nextPageToken);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("kind", kind).add("id", id).add("selfLink", selfLink).add("items", items);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
