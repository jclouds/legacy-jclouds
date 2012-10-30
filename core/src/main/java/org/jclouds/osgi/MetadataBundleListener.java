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
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link BundleListener} that listens for {@link BundleEvent} and searches for {@link org.jclouds.providers.ProviderMetadata} and {@link org.jclouds.apis.ApiMetadata} in newly
 * installed Bundles. This is used as a workaround for OSGi environments where the ServiceLoader cannot cross bundle
 * boundaries.
 */
public class MetadataBundleListener implements BundleListener {

  private Map<Long, ProviderMetadata> providerMetadataMap = new HashMap<Long, ProviderMetadata>();
  private Map<Long, ApiMetadata> apiMetadataMap = new HashMap<Long, ApiMetadata>();


  public void start(BundleContext bundleContext) {
    bundleContext.addBundleListener(this);
    for (Bundle bundle : bundleContext.getBundles()) {
      if (bundle.getState() == Bundle.ACTIVE) {
        ProviderMetadata providerMetadata = getProviderMetadata(bundle);
        ApiMetadata apiMetadata = getApiMetadata(bundle);

        if (providerMetadata != null) {
          ProviderRegistry.registerProvider(providerMetadata);
          providerMetadataMap.put(bundle.getBundleId(), providerMetadata);
        }
        if (apiMetadata != null) {
          ApiRegistry.registerApi(apiMetadata);
          apiMetadataMap.put(bundle.getBundleId(), apiMetadata);
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
    ProviderMetadata providerMetadata;
    ApiMetadata apiMetadata;
    switch (event.getType()) {
      case BundleEvent.STARTED:
        providerMetadata = getProviderMetadata(event.getBundle());
        apiMetadata = getApiMetadata(event.getBundle());
        if (providerMetadata != null) {
          ProviderRegistry.registerProvider(providerMetadata);
          providerMetadataMap.put(event.getBundle().getBundleId(), providerMetadata);
        }
        if (apiMetadata != null) {
          ApiRegistry.registerApi(apiMetadata);
          apiMetadataMap.put(event.getBundle().getBundleId(), apiMetadata);
        }
        break;
      case BundleEvent.STOPPING:
      case BundleEvent.STOPPED:
        providerMetadata = providerMetadataMap.get(event.getBundle().getBundleId());
        apiMetadata = apiMetadataMap.get(event.getBundle().getBundleId());
        if (providerMetadata != null) {
          ProviderRegistry.unregisterProvider(providerMetadata);
        }
        if (apiMetadata != null) {
          ApiRegistry.unRegisterApi(apiMetadata);
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
        Class<? extends ProviderMetadata> providerMetadataClass = bundle.loadClass(className);
        metadata = providerMetadataClass.newInstance();
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
   * Creates an instance of {@link ApiMetadata} from the {@link Bundle}.
   *
   * @param bundle
   * @return
   */
  public ApiMetadata getApiMetadata(Bundle bundle) {
    ApiMetadata metadata = null;
    String className = getApiMetadataClassName(bundle);
    if (className != null && !className.isEmpty()) {
      try {
        Class<? extends ApiMetadata> apiMetadataClass = bundle.loadClass(className);
        metadata = apiMetadataClass.newInstance();
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


  public String getMetadataClassName(Bundle bundle, String pathToMetadata) {
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

  /**
   * Retrieves the {@link ProviderMetadata} class name for the bundle if it exists.
   *
   * @param bundle
   * @return
   */
  public String getProviderMetadataClassName(Bundle bundle) {
    return getMetadataClassName(bundle, "/META-INF/services/org.jclouds.providers.ProviderMetadata");
  }

  /**
   * Retrieves the {@link ProviderMetadata} class name for the bundle if it exists.
   *
   * @param bundle
   * @return
   */
  public String getApiMetadataClassName(Bundle bundle) {
    return getMetadataClassName(bundle, "/META-INF/services/org.jclouds.apis.ApiMetadata");
  }

}
