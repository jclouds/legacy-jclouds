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

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.ProviderRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link BundleListener} that listens for {@link BundleEvent} and searches for {@link org.jclouds.providers.ProviderMetadata} in newly
 * installed Bundles. This is used as a workaround for OSGi environments where the ServiceLoader cannot cross bundle
 * boundaries.
 */
public class ProviderBundleListener implements BundleListener {

  private Map<Long, ProviderMetadata> bundleMetadataMap = new HashMap<Long, ProviderMetadata>();

  @Override
  public void bundleChanged(BundleEvent event) {
    ProviderMetadata metadata;
    switch (event.getType()) {
      case BundleEvent.STARTED:
        metadata = getProviderMetadata(event.getBundle());
        if (metadata != null) {
          ProviderRegistry.registerProvider(metadata);
          bundleMetadataMap.put(event.getBundle().getBundleId(), metadata);
        }
        break;
      case BundleEvent.STOPPING:
      case BundleEvent.STOPPED:
        metadata = bundleMetadataMap.get(event.getBundle().getBundleId());
        if (metadata != null) {
          ProviderRegistry.uRegisterProvider(metadata);
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
  public ProviderMetadata getProviderMetadata(Bundle bundle) {
    ProviderMetadata metadata = null;
    String className = getProviderMetadataClassName(bundle);
    if (className != null && !className.isEmpty()) {
      try {
        Class<? extends ProviderMetadata> provideClass = bundle.loadClass(className);
        metadata = provideClass.newInstance();
      } catch (ClassNotFoundException e) {
        // ignore
      } catch (InstantiationException e) {
        // ignore
      } catch (IllegalAccessException e) {
        // ignore
      }
    }
    return metadata;
  }

  /**
   * Retrieves the {@link ProviderMetadata} class name for the bundle if it exists.
   *
   * @param bundle
   * @return
   */
  public String getProviderMetadataClassName(Bundle bundle) {
    URL resource = bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata");
    InputStream is = null;
    InputStreamReader reader = null;
    BufferedReader bufferedReader = null;
    StringBuilder sb = new StringBuilder();

    try {
      is = resource.openStream();
      reader = new InputStreamReader(is, "UTF-8");
      bufferedReader = new BufferedReader(reader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (Throwable e) {
    } finally {
      try {
        if (reader != null)
          reader.close();
      } catch (Throwable e) {
      }
      try {
        if (bufferedReader != null)
          bufferedReader.close();
      } catch (Throwable e) {
      }
      try {
        is.close();
      } catch (Throwable e) {
      }

    }
    return sb.toString().trim();
  }
}