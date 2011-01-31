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

package org.jclouds.byon.functions;

import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Parses the following syntax.
 * 
 * <pre>
 * nodes:
 *     - id: cluster-1:
 *       name: cluster-1
 *       description: xyz
 *       hostname: cluster-1.mydomain.com
 *       os_arch: x86
 *       os_family: linux
 *       os_description: redhat
 *       os_version: 5.3
 *       group: hadoop
 *       tags:
 *           - vanilla
 *       username: kelvin
 *       credential: password_or_rsa
 *         or
 *       credential_url: password_or_rsa_file ex. resource:///id_rsa will get the classpath /id_rsa; file://path/to/id_rsa
 *       sudo_password: password
 * </pre>
 * 
 * @author Kelvin Kakugawa
 */
@Singleton
public class NodesFromYaml implements Function<InputStream, Map<String, Node>> {

   /**
    * Type-safe config class for YAML
    * 
    */
   public static class Config {
      public List<Node> nodes;
   }

   @Override
   public Map<String, Node> apply(InputStream source) {
      Constructor constructor = new Constructor(Config.class);

      TypeDescription nodeDesc = new TypeDescription(Node.class);
      nodeDesc.putListPropertyType("tags", String.class);
      constructor.addTypeDescription(nodeDesc);

      TypeDescription configDesc = new TypeDescription(Config.class);
      configDesc.putListPropertyType("nodes", Node.class);
      constructor.addTypeDescription(configDesc);

      Yaml yaml = new Yaml(new Loader(constructor));
      Config config = (Config) yaml.load(source);
      checkState(config != null, "missing config: class");
      checkState(config.nodes != null, "missing nodes: collection");

      return Maps.uniqueIndex(config.nodes, new Function<Node, String>() {
         public String apply(Node node) {
            return node.getId();
         }
      });
   }
}
