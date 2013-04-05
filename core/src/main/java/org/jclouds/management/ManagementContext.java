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

package org.jclouds.management;

import org.jclouds.Context;

import javax.management.MBeanServer;

/**
 * The management context, keeps track of the {@link JcloudsManagedBean} objects that have been created.
 * It is responsible for exporting beans to the {@link MBeanServer}, whenever it becomes available.
 * It also keeps track of {@link Context}s created, so that they can be accessed via JMX.
 */
public interface ManagementContext {

   /**
    * Register a {@link JcloudsManagedBean} to the MBeanServer.
    * @param mBean
    */
   void manage(JcloudsManagedBean mBean);

   /**
    * Un-registers a {@link JcloudsManagedBean} to the MBeanServer.
    * @param mBean
    */
   void unmanage(JcloudsManagedBean mBean);


   /**
    * Bind an {@link MBeanServer} to the context.
    * This is mostly useful for dynamic environments where an {@link MBeanServer} may come and go.
    * The context should re-register the {@link JcloudsManagedBean} objects that have been added to the context.
    * @param mBeanServer
    */
   void bind(MBeanServer mBeanServer);

   /**
    * Unbind an {@link MBeanServer} to the context.
    * This is mostly useful for dynamic environments where an {@link MBeanServer} may come and go.
    * The context should unregister the {@link JcloudsManagedBean} objects that have been added to the context.
    * @param mBeanServer
    */
   void unbind(MBeanServer mBeanServer);

   /**
    * Register {@link org.jclouds.Context}.
    * @param context
    * @param <C>
    */
   <C extends Context> void register(C context);

   /**
    * Un-register {@link Context}.
    * @param context
    * @param <C>
    */
   <C extends Context> void unregister(C context);

   /**
    * List all registered {@link Context} objects.
    * @return
    */
   Iterable<? extends Context> listContexts();


   /**
    * Returns {@link Context} by name.
    * @param name
    * @param <C>
    */
   <C extends Context> C getContext(String name);

}
