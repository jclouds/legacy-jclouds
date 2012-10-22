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

package org.jclouds.abiquo.strategy.event.internal;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.event.Event;
import org.jclouds.abiquo.domain.event.options.EventOptions;
import org.jclouds.abiquo.strategy.event.ListEvents;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.event.EventsDto;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

public class ListEventsImpl implements ListEvents {
   // This strategy does not have still an Executor instance because the current
   // methods call
   // single api methods

   protected final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   @Inject
   ListEventsImpl(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
      this.context = context;
   }

   @Override
   public Iterable<Event> execute() {
      EventsDto result = context.getApi().getEventApi().listEvents();
      return wrap(context, Event.class, result.getCollection());
   }

   @Override
   public Iterable<Event> execute(final EventOptions options) {
      EventsDto result = context.getApi().getEventApi().listEvents(options);
      return wrap(context, Event.class, result.getCollection());
   }

   @Override
   public Iterable<Event> execute(final Predicate<Event> selector) {
      return filter(execute(), selector);
   }

}
