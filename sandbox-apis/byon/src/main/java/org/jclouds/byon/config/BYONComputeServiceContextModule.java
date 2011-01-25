/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.byon.config;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.internal.BYONComputeServiceAdapter;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
@SingleThreaded
public class BYONComputeServiceContextModule extends
      JCloudsNativeComputeServiceAdapterContextModule<Supplier, Supplier> {
   @Resource
   protected Logger logger = Logger.NULL;

   public BYONComputeServiceContextModule() {
      super(Supplier.class, Supplier.class, BYONComputeServiceAdapter.class);
   }

   @Provides
   @Singleton
   Supplier<Map<String, Node>> provideNodeList(@Provider final URI uri) {
      return new Supplier<Map<String, Node>> (){
         @Override
         public Map<String, Node> get() {
            Constructor constructor = new Constructor(Config.class);

            TypeDescription nodeDesc = new TypeDescription(Node.class);
            nodeDesc.putListPropertyType("tags", String.class);
            constructor.addTypeDescription(nodeDesc);

            TypeDescription configDesc = new TypeDescription(Config.class);
            configDesc.putMapPropertyType("nodes", String.class, Node.class);
            constructor.addTypeDescription(configDesc);

            Yaml yaml = new Yaml(new Loader(constructor));

            Config config;
            try {
               URL url = uri.toURL();
               InputStream input = url.openStream();
               config = (Config)yaml.load(input);
            } catch(MalformedURLException e) {
               logger.error(e, "URI is not a URL: %s", uri);
               return ImmutableMap.<String, Node> of();
            } catch(IOException e) {
               logger.error(e, "URI could not be read: %s", uri);
               return ImmutableMap.<String, Node> of();
            }

            return config.nodes;
         }
      };
   }

   @Provides
   @Singleton
   Supplier provideApi(Supplier<Map<String, Node>> in) {
      return in;
   }

}
