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
package org.jclouds.osgi;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiRegistry;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.ProviderRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * A {@link BundleListener} that listens for {@link BundleEvent} and searches for {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} in newly
 * installed Bundles. This is used as a workaround for OSGi environments where the ServiceLoader cannot cross bundle
 * boundaries.
 */
public class MetadataBundleListener implements BundleListener {

   private Multimap<Long, ProviderMetadata> providerMetadataMap = ArrayListMultimap.create();
   private Multimap<Long, ApiMetadata> apiMetadataMap = ArrayListMultimap.create();


   public void start(BundleContext bundleContext) {
      bundleContext.addBundleListener(this);
      for (Bundle bundle : bundleContext.getBundles()) {
         if (bundle.getState() == Bundle.ACTIVE) {
            List<ProviderMetadata> providerMetadataList = getProviderMetadata(bundle);
            List<ApiMetadata> apiMetadataList = getApiMetadata(bundle);

            for (ProviderMetadata providerMetadata : providerMetadataList) {
               if (providerMetadata != null) {
                  ProviderRegistry.registerProvider(providerMetadata);
                  providerMetadataMap.put(bundle.getBundleId(), providerMetadata);
               }
            }

            for (ApiMetadata apiMetadata : apiMetadataList) {
               if (apiMetadata != null) {
                  ApiRegistry.registerApi(apiMetadata);
                  apiMetadataMap.put(bundle.getBundleId(), apiMetadata);
               }
            }
         }
      }
   }

   public void stop(BundleContext bundleContext) {
      providerMetadataMap.clear();
      apiMetadataMap.clear();
   }

   @Override
   public void bundleChanged(BundleEvent event) {
      Collection<ProviderMetadata> providerMetadataList = null;
      Collection<ApiMetadata> apiMetadataList = null;
      switch (event.getType()) {
         case BundleEvent.STARTED:
            providerMetadataList = getProviderMetadata(event.getBundle());
            apiMetadataList = getApiMetadata(event.getBundle());
            for (ProviderMetadata providerMetadata : providerMetadataList) {
               if (providerMetadata != null) {
                  ProviderRegistry.registerProvider(providerMetadata);
                  providerMetadataMap.put(event.getBundle().getBundleId(), providerMetadata);
               }
            }

            for (ApiMetadata apiMetadata : apiMetadataList) {
               if (apiMetadata != null) {
                  ApiRegistry.registerApi(apiMetadata);
                  apiMetadataMap.put(event.getBundle().getBundleId(), apiMetadata);
               }
            }
            break;
         case BundleEvent.STOPPING:
         case BundleEvent.STOPPED:
            providerMetadataList = providerMetadataMap.get(event.getBundle().getBundleId());
            apiMetadataList = apiMetadataMap.get(event.getBundle().getBundleId());

            if (providerMetadataList != null) {
               for (ProviderMetadata providerMetadata : providerMetadataList) {
                  ProviderRegistry.unregisterProvider(providerMetadata);
               }
            }
            if (apiMetadataList != null) {
               for (ApiMetadata apiMetadata : apiMetadataList) {
                  ApiRegistry.unRegisterApi(apiMetadata);
               }
            }
            break;
      }
   }

   /**
    * Creates an instance of {@link ProviderMetadata} from the {@link Bundle}.
    *
    * @param bundle
    * @return
    */
   public List<ProviderMetadata> getProviderMetadata(Bundle bundle) {
      List<ProviderMetadata> metadataList = Lists.newArrayList();
      String classNames = getProviderMetadataClassNames(bundle);
      if (classNames != null && !classNames.isEmpty()) {
         for (String className : classNames.split("\n")) {
            try {
               Class<? extends ProviderMetadata> providerMetadataClass = bundle.loadClass(className);
               //Classes loaded by other class loaders are not assignable.
               if (ProviderMetadata.class.isAssignableFrom(providerMetadataClass)) {
                  ProviderMetadata metadata = providerMetadataClass.newInstance();
                  metadataList.add(metadata);
               }
            } catch (ClassNotFoundException e) {
               // ignore
            } catch (InstantiationException e) {
               // ignore
            } catch (IllegalAccessException e) {
               // ignore
            }
         }
      }
      return metadataList;
   }

   /**
    * Creates an instance of {@link ApiMetadata} from the {@link Bundle}.
    *
    * @param bundle
    * @return
    */
   public List<ApiMetadata> getApiMetadata(Bundle bundle) {
      List<ApiMetadata> metadataList = Lists.newArrayList();
      String classNames = getApiMetadataClassNames(bundle);
      if (classNames != null && !classNames.isEmpty()) {
         for (String className : classNames.split("\n")) {
            try {
               Class<? extends ApiMetadata> apiMetadataClass = bundle.loadClass(className);
               //Classes loaded by other class loaders are not assignable.
               if (ApiMetadata.class.isAssignableFrom(apiMetadataClass)) {
                  ApiMetadata metadata = apiMetadataClass.newInstance();
                  metadataList.add(metadata);
               }
            } catch (ClassNotFoundException e) {
               // ignore
            } catch (InstantiationException e) {
               // ignore
            } catch (IllegalAccessException e) {
               // ignore
            }
         }
      }
      return metadataList;
   }


   public String getMetadataClassNames(Bundle bundle, String pathToMetadata) {
      URL resource = bundle.getEntry(pathToMetadata);
      InputStream is = null;
      InputStreamReader reader = null;
      BufferedReader bufferedReader = null;
      StringBuilder sb = new StringBuilder();

      try {
         is = resource.openStream();
         reader = new InputStreamReader(is, Charsets.UTF_8);
         bufferedReader = new BufferedReader(reader);
         String line;
         while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
         }
      } catch (Throwable e) {
      } finally {
         Closeables.closeQuietly(reader);
         Closeables.closeQuietly(bufferedReader);
         Closeables.closeQuietly(is);
      }
      return sb.toString().trim();
   }

   /**
    * Retrieves the {@link ProviderMetadata} class name for the bundle if it exists.
    *
    * @param bundle
    * @return
    */
   public String getProviderMetadataClassNames(Bundle bundle) {
      return getMetadataClassNames(bundle, "/META-INF/services/org.jclouds.providers.ProviderMetadata");
   }

   /**
    * Retrieves the {@link ProviderMetadata} class name for the bundle if it exists.
    *
    * @param bundle
    * @return
    */
   public String getApiMetadataClassNames(Bundle bundle) {
      return getMetadataClassNames(bundle, "/META-INF/services/org.jclouds.apis.ApiMetadata");
   }

}
