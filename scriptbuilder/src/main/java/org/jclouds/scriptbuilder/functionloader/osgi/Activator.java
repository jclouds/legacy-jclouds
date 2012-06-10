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
package org.jclouds.scriptbuilder.functionloader.osgi;

import org.jclouds.scriptbuilder.functionloader.CurrentFunctionLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   BundleFunctionLoader functionLoader;

   /**
    * Called when this bundle is started so the Framework can perform the bundle-specific activities
    * necessary to start this bundle. This method can be used to register services or to allocate
    * any resources that this bundle needs.
    * <p/>
    * <p/>
    * This method must complete and return to its caller in a timely manner.
    * 
    * @param context
    *           The execution context of the bundle being started.
    * @throws Exception
    *            If this method throws an exception, this bundle is marked as stopped and the
    *            Framework will remove this bundle's listeners, unregister all services registered
    *            by this bundle, and release all services used by this bundle.
    */
   @Override
   public void start(BundleContext context) throws Exception {
      functionLoader = new BundleFunctionLoader(context);
      functionLoader.start();
      CurrentFunctionLoader.set(new ServiceFunctionLoader(context));
   }

   /**
    * Called when this bundle is stopped so the Framework can perform the bundle-specific activities
    * necessary to stop the bundle. In general, this method should undo the work that the
    * <code>BundleActivator.start</code> method started. There should be no active threads that were
    * started by this bundle when this bundle returns. A stopped bundle must not call any Framework
    * objects.
    * <p/>
    * <p/>
    * This method must complete and return to its caller in a timely manner.
    * 
    * @param context
    *           The execution context of the bundle being stopped.
    * @throws Exception
    *            If this method throws an exception, the bundle is still marked as stopped, and the
    *            Framework will remove the bundle's listeners, unregister all services registered by
    *            the bundle, and release all services used by the bundle.
    */
   @Override
   public void stop(BundleContext context) throws Exception {
      if (functionLoader != null) {
         CurrentFunctionLoader.reset();
         functionLoader.stop();
         functionLoader = null;
      }
   }
}
