/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.exception.WrapperException;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.domain.util.LinkUtils;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.WrapperDto;
import com.abiquo.server.core.task.TaskDto;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * This class is used to decorate transport objects with high level
 * functionality.
 * 
 * @author Francesc Montserrat
 * @author Ignasi Barrera
 */
public abstract class DomainWrapper<T extends SingleResourceTransportDto> {
   /** The rest context. */
   protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

   /** The wrapped object. */
   protected T target;

   protected DomainWrapper(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final T target) {
      super();
      this.context = checkNotNull(context, "context");
      this.target = checkNotNull(target, "target");
   }

   /**
    * Returns the URI that identifies the transport object
    * 
    * @return The URI identifying the transport object
    */
   public URI getURI() {
      RESTLink link = LinkUtils.getSelfLink(target);
      return link == null ? null : URI.create(link.getHref());
   }

   /**
    * Returns the wrapped object.
    */
   public T unwrap() {
      return target;
   }

   /**
    * Read the ID of the parent resource from the given link.
    * 
    * @param parentLinkRel
    *           The link to the parent resource.
    * @return The ID of the parent resource.
    */
   protected Integer getParentId(final String parentLinkRel) {
      return target.getIdFromLink(parentLinkRel);
   }

   /**
    * Wraps an object in the given wrapper class.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> W wrap(
         final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Class<W> wrapperClass, final T target) {
      if (target == null) {
         return null;
      }

      try {
         Constructor<W> cons = wrapperClass.getDeclaredConstructor(RestContext.class, target.getClass());
         if (!cons.isAccessible()) {
            cons.setAccessible(true);
         }
         return cons.newInstance(context, target);
      } catch (Exception ex) {
         throw new WrapperException(wrapperClass, target, ex);
      }
   }

   /**
    * Wrap a collection of objects to the given wrapper class.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> List<W> wrap(
         final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Class<W> wrapperClass, final Iterable<T> targets) {
      if (targets == null) {
         return null;
      }

      return Lists.newLinkedList(transform(targets, new Function<T, W>() {
         @Override
         public W apply(final T input) {
            return wrap(context, wrapperClass, input);
         }
      }));
   }

   /**
    * Unwrap a collection of objects.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> List<T> unwrap(
         final Iterable<W> targets) {
      return Lists.newLinkedList(transform(targets, new Function<W, T>() {
         @Override
         public T apply(final W input) {
            return input.unwrap();
         }
      }));
   }

   /**
    * Update or creates a link of "target" with the uri of a link from "source".
    */
   protected <T1 extends SingleResourceTransportDto, T2 extends SingleResourceTransportDto> void updateLink(
         final T1 target, final String targetLinkRel, final T2 source, final String sourceLinkRel) {
      RESTLink parent = null;

      checkNotNull(source.searchLink(sourceLinkRel), ValidationErrors.MISSING_REQUIRED_LINK);

      // Insert
      if ((parent = target.searchLink(targetLinkRel)) == null) {
         target.addLink(new RESTLink(targetLinkRel, source.searchLink(sourceLinkRel).getHref()));
      }
      // Replace
      else {
         parent.setHref(source.searchLink(sourceLinkRel).getHref());
      }
   }

   /**
    * Join a collection of {@link WrapperDto} objects in a single collection
    * with all the elements of each wrapper object.
    */
   public static <T extends SingleResourceTransportDto> Iterable<T> join(
         final Iterable<? extends WrapperDto<T>> collection) {
      List<T> dtos = Lists.newLinkedList();
      for (WrapperDto<T> wrapper : collection) {
         dtos.addAll(wrapper.getCollection());
      }
      return dtos;
   }

   /**
    * Utility method to get an {@link AsyncTask} given an
    * {@link AcceptedRequestDto}.
    * 
    * @param acceptedRequest
    *           The accepted request dto.
    * @return The async task.
    */
   protected AsyncTask getTask(final AcceptedRequestDto<String> acceptedRequest) {
      RESTLink taskLink = acceptedRequest.getStatusLink();
      checkNotNull(taskLink, ValidationErrors.MISSING_REQUIRED_LINK + AsyncTask.class);

      // This will return null on untrackable tasks
      TaskDto task = context.getApi().getTaskApi().getTask(taskLink);
      return wrap(context, AsyncTask.class, task);
   }

   /**
    * Utility method to get all {@link AsyncTask} related to an
    * {@link AcceptedRequestDto}.
    * 
    * @param acceptedRequest
    *           The accepted request dto.
    * @return The async task array.
    */
   protected AsyncTask[] getTasks(final AcceptedRequestDto<String> acceptedRequest) {
      List<AsyncTask> tasks = new ArrayList<AsyncTask>();

      for (RESTLink link : acceptedRequest.getLinks()) {
         // This will return null on untrackable tasks
         TaskDto task = context.getApi().getTaskApi().getTask(link);
         if (task != null) {
            tasks.add(wrap(context, AsyncTask.class, task));
         }
      }

      AsyncTask[] taskArr = new AsyncTask[tasks.size()];
      return tasks.toArray(taskArr);
   }

}
