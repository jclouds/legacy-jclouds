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
package org.jclouds.osgi;

import static org.jclouds.osgi.Bundles.instantiateAvailableClasses;
import static org.jclouds.osgi.Bundles.stringsForResourceInBundle;
import static org.osgi.framework.BundleEvent.STARTED;
import static org.osgi.framework.BundleEvent.STOPPED;
import static org.osgi.framework.BundleEvent.STOPPING;

import java.util.List;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * A {@link BundleListener} that listens for {@link BundleEvent} and searches for
 * {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} in newly installed Bundles.
 * This is used as a workaround for OSGi environments where the ServiceLoader cannot cross bundle boundaries.
 * 
 * @author iocanel
 */
public class MetadataBundleListener implements BundleListener {

   private final Multimap<Long, ProviderMetadata> providerMetadataMap = ArrayListMultimap.create();
   private final Multimap<Long, ApiMetadata> apiMetadataMap = ArrayListMultimap.create();

   private final List<ProviderListener> providerListeners = Lists.newArrayList();
   private final List<ApiListener> apiListeners = Lists.newArrayList();

   /**
    * Starts the listener. Checks the bundles that are already active and registers {@link ProviderMetadata} and
    * {@link ApiMetadata} found. Registers the itself as a {@link BundleListener}.
    * 
    * @param bundleContext
    */
   public synchronized void start(BundleContext bundleContext) {
      bundleContext.addBundleListener(this);
      for (Bundle bundle : bundleContext.getBundles()) {
         if (bundle.getState() == Bundle.ACTIVE) {
            addBundle(bundle);
         }
      }
      bundleContext.addBundleListener(this);
   }

   /**
    * Stops the listener. Removes itself from the {@link BundleListener}s. Clears metadata maps and listeners lists.
    * 
    * @param bundleContext
    */
   public void stop(BundleContext bundleContext) {
      bundleContext.removeBundleListener(this);
      providerMetadataMap.clear();
      apiMetadataMap.clear();
      apiListeners.clear();
      providerListeners.clear();
   }

   @Override
   public synchronized void bundleChanged(BundleEvent event) {
      switch (event.getType()) {
      case STARTED:
         addBundle(event.getBundle());
         break;
      case STOPPING:
      case STOPPED:
         removeBundle(event.getBundle());
         break;
      }
   }

   /**
    * Searches for {@link ProviderMetadata} and {@link ApiMetadata} inside the {@link Bundle}. If metadata are found
    * they are registered in the {@link ProviderRegistry} and {@link ApiRegistry}. Also the {@link ProviderListener} and
    * {@link ApiListener} are notified.
    * 
    * @param bundle
    */
   private synchronized void addBundle(Bundle bundle) {
      for (ProviderMetadata providerMetadata : listProviderMetadata(bundle)) {
         if (providerMetadata != null) {
            ProviderRegistry.registerProvider(providerMetadata);
            providerMetadataMap.put(bundle.getBundleId(), providerMetadata);
            for (ProviderListener listener : providerListeners) {
               listener.added(providerMetadata);
            }
         }
      }

      for (ApiMetadata apiMetadata : listApiMetadata(bundle)) {
         if (apiMetadata != null) {
            ApiRegistry.registerApi(apiMetadata);
            apiMetadataMap.put(bundle.getBundleId(), apiMetadata);
            for (ApiListener listener : apiListeners) {
               listener.added(apiMetadata);
            }
         }
      }
   }

   /**
    * Searches for {@link ProviderMetadata} and {@link ApiMetadata} registered under the {@link Bundle} id. If metadata
    * are found they are removed the {@link ProviderRegistry} and {@link ApiRegistry}. Also the {@link ProviderListener}
    * and {@link ApiListener} are notified.
    * 
    * @param bundle
    */
   private synchronized void removeBundle(Bundle bundle) {
      for (ProviderMetadata providerMetadata : providerMetadataMap.removeAll(bundle.getBundleId())) {
         ProviderRegistry.unregisterProvider(providerMetadata);
         for (ProviderListener listener : providerListeners) {
            listener.removed(providerMetadata);
         }
      }
      for (ApiMetadata apiMetadata : apiMetadataMap.removeAll(bundle.getBundleId())) {
         ApiRegistry.unRegisterApi(apiMetadata);
         for (ApiListener listener : apiListeners) {
            listener.removed(apiMetadata);
         }
      }
   }

   /**
    * Creates an instance of {@link ProviderMetadata} from the {@link Bundle}.
    * 
    * @param bundle
    * @return
    */
   public Iterable<ProviderMetadata> listProviderMetadata(Bundle bundle) {
      Iterable<String> classNames = stringsForResourceInBundle(
            "/META-INF/services/org.jclouds.providers.ProviderMetadata", bundle);
      return instantiateAvailableClasses(bundle, classNames, ProviderMetadata.class);
   }

   /**
    * Creates an instance of {@link ApiMetadata} from the {@link Bundle}.
    * 
    * @param bundle
    * @return
    */
   public Iterable<ApiMetadata> listApiMetadata(Bundle bundle) {
      Iterable<String> classNames = stringsForResourceInBundle("/META-INF/services/org.jclouds.apis.ApiMetadata", bundle);
      return instantiateAvailableClasses(bundle, classNames, ApiMetadata.class);
   }

   /**
    * Adds a {@link ProviderListener} and notifies it of existing {@link ProviderMetadata}.
    * 
    * @param listener
    *           The listener.
    */
   public synchronized void addProviderListener(ProviderListener listener) {
      providerListeners.add(listener);
      for (ProviderMetadata metadata : providerMetadataMap.values()) {
         listener.added(metadata);
      }
   }

   /**
    * Removes the {@link ProviderListener}
    * 
    * @param listener
    *           The listener
    */
   public synchronized void removeProviderListener(ProviderListener listener) {
      providerListeners.remove(listener);
   }

   /**
    * Adds a {@link ApiListener} and notifies it of existing {@link ApiMetadata}.
    * 
    * @param listener
    */
   public synchronized void addApiListenerListener(ApiListener listener) {
      apiListeners.add(listener);
      for (ApiMetadata metadata : apiMetadataMap.values()) {
         listener.added(metadata);
      }
   }

   /**
    * Removes the {@link ApiListener}
    * 
    * @param listener
    */
   public synchronized void removeApiListenerListener(ApiListener listener) {
      apiListeners.remove(listener);
   }

}
