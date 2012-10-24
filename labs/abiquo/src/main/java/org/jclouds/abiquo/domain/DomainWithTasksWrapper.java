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

import static com.google.common.collect.Iterables.filter;

import java.util.Collections;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.rest.RestContext;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * This class is used to decorate transport objects that are owners of some
 * {@link TaskDto}
 * 
 * @author Ignasi Barrera
 */
public abstract class DomainWithTasksWrapper<T extends SingleResourceTransportDto> extends DomainWrapper<T> {

   protected DomainWithTasksWrapper(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final T target) {
      super(context, target);
   }

   public List<AsyncTask> listTasks() {
      TasksDto result = context.getApi().getTaskApi().listTasks(target);
      List<AsyncTask> tasks = wrap(context, AsyncTask.class, result.getCollection());

      // Return the most recent task first
      Collections.sort(tasks, new Ordering<AsyncTask>() {
         @Override
         public int compare(final AsyncTask left, final AsyncTask right) {
            return Longs.compare(left.getTimestamp(), right.getTimestamp());
         }
      }.reverse());

      return tasks;
   }

   public List<AsyncTask> listTasks(final Predicate<AsyncTask> filter) {
      return Lists.newLinkedList(filter(listTasks(), filter));
   }

   public AsyncTask findTask(final Predicate<AsyncTask> filter) {
      return Iterables.getFirst(filter(listTasks(), filter), null);
   }
}
