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

package org.jclouds.management.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jclouds.Context;
import org.jclouds.management.JcloudsManagedBean;
import org.jclouds.management.ManagementContext;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;

public enum BaseManagementContext implements ManagementContext {

   INSTANCE;

   private final Map<String, Context> contexts = Maps.newHashMap();
   private final Set<JcloudsManagedBean> mbeans = Sets.newHashSet();

   private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void manage(JcloudsManagedBean mBean) {
      if (mBeanServer != null) {
         ManagementUtils.register(mBeanServer, mBean, mBean.getType(), mBean.getName());
      }
      mbeans.add(mBean);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void unmanage(JcloudsManagedBean mBean) {
      if (mBeanServer != null) {
         ManagementUtils.register(mBeanServer, mBean, mBean.getType(), mBean.getName());
      }
      mbeans.remove(mBean);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void bind(MBeanServer server) {
      this.mBeanServer = server;
      for(JcloudsManagedBean mBean : mbeans) {
         ManagementUtils.register(server, mBean, mBean.getType(), mBean.getName());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public synchronized void unbind(MBeanServer server) {
      for(JcloudsManagedBean mBean : mbeans) {
         ManagementUtils.unregister(server, mBean.getType(), mBean.getName());
      }
      this.mBeanServer = null;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public <C extends Context> void register(C context) {
      contexts.put(context.getName(), context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <C extends Context> void unregister(C context) {
      contexts.remove(context.getName());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<? extends Context> listContexts() {
      return contexts.values();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Context getContext(String name) {
      return contexts.get(name);
   }
}