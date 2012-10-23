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

package org.jclouds.abiquo.strategy.config.internal;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.config.SystemProperty;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.strategy.config.ListProperties;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.config.SystemPropertiesDto;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * List properties.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Singleton
public class ListPropertiesImpl implements ListProperties {
   // This strategy does not have still an Executor instance because the current
   // methods call
   // single api methods

   protected final RestContext<AbiquoApi, AbiquoAsyncApi> context;

   @Inject
   ListPropertiesImpl(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
      this.context = context;
   }

   @Override
   public Iterable<SystemProperty> execute() {
      SystemPropertiesDto result = context.getApi().getConfigApi().listSystemProperties();
      return wrap(context, SystemProperty.class, result.getCollection());
   }

   @Override
   public Iterable<SystemProperty> execute(final Predicate<SystemProperty> selector) {
      return filter(execute(), selector);
   }

   @Override
   public Iterable<SystemProperty> execute(final PropertyOptions options) {
      SystemPropertiesDto result = context.getApi().getConfigApi().listSystemProperties(options);
      return wrap(context, SystemProperty.class, result.getCollection());
   }
}
