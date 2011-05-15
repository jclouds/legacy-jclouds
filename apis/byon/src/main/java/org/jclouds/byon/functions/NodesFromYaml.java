/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
 *       location_id: virginia
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
 * @author Adrian Cole
 */
@Singleton
public class NodesFromYaml implements Function<InputStream, Map<String, Node>> {

   /**
    * Type-safe config class for YAML
    * 
    */
   public static class Config {
      public List<CrappyNode> nodes;
   }

   // crappy, as snakeyaml..
   // 1. complains on illegalaccesserror for extends Constructor.ConstructMapping, so we can't use a
   // real constructor
   // 2. cannot set non-public fields, or fill non-public classes
   // 3. doesn't support a SerializedName annotation, so your fields need to be named the same as
   // the text
   public static class CrappyNode {
      public String id;
      public String name;
      public String description;
      public String hostname;
      public String location_id;
      public String os_arch;
      public String os_family;
      public String os_description;
      public String os_version;
      public String group;
      public List<String> tags = Lists.newArrayList();
      public String username;
      public String credential;
      public String credential_url;
      public String sudo_password;
   }

   @Override
   public Map<String, Node> apply(InputStream source) {

      Constructor constructor = new Constructor(Config.class);

      TypeDescription nodeDesc = new TypeDescription(CrappyNode.class);
      nodeDesc.putListPropertyType("tags", String.class);
      constructor.addTypeDescription(nodeDesc);

      TypeDescription configDesc = new TypeDescription(Config.class);
      configDesc.putListPropertyType("nodes", CrappyNode.class);
      constructor.addTypeDescription(configDesc);
      // note that snakeyaml also throws nosuchmethod error when you use the non-deprecated
      // constructor
      Yaml yaml = new Yaml(new Loader(constructor));
      Config config = (Config) yaml.load(source);
      checkState(config != null, "missing config: class");
      checkState(config.nodes != null, "missing nodes: collection");

      return Maps.uniqueIndex(Iterables.transform(config.nodes, new Function<CrappyNode, Node>() {

         @Override
         public Node apply(CrappyNode arg0) {
            return Node.builder().id(arg0.id).name(arg0.name).description(arg0.description)
                     .locationId(arg0.location_id).hostname(arg0.hostname).osArch(arg0.os_arch)
                     .osFamily(arg0.os_family).osDescription(arg0.os_description).osVersion(arg0.os_version).group(
                              arg0.group).tags(arg0.tags).username(arg0.username).credential(arg0.credential)
                     .credentialUrl(arg0.credential_url != null ? URI.create(arg0.credential_url) : null).sudoPassword(
                              arg0.sudo_password).build();
         }

      }), new Function<Node, String>() {
         public String apply(Node node) {
            return node.getId();
         }
      });
   }
}
