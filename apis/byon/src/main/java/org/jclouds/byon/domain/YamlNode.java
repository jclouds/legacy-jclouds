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
package org.jclouds.byon.domain;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.byon.Node;
import org.jclouds.util.Strings2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.io.Closeables;

/**
 * Serializes to the following
 * 
 * <pre>
 *       id: cluster-1
 *       name: cluster-1
 *       description: xyz
 *       hostname: cluster-1.mydomain.com
 *       location_id: virginia
 *       os_arch: x86
 *       os_family: linux
 *       os_description: redhat
 *       os_version: 5.3
 *       os_64bit: 5.3
 *       login_port: 2022
 *       group: hadoop
 *       tags:
 *           - vanilla
 *       metadata:
 *           key1: val1
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
public class YamlNode {
   public String id;
   public String name;
   public String description;
   public String hostname;
   public String location_id;
   public String os_arch;
   public String os_family;
   public String os_description;
   public String os_version;
   public int login_port = 22;
   public boolean os_64bit;
   public String group;
   public List<String> tags = Lists.newArrayList();
   public Map<String, String> metadata = Maps.newLinkedHashMap();
   public String username;
   public String credential;
   public String credential_url;
   public String sudo_password;

   public static final Function<YamlNode, Node> toNode = new Function<YamlNode, Node>() {
      @Override
      public Node apply(YamlNode arg0) {
         if (arg0 == null)
            return null;
         return Node.builder().id(arg0.id).name(arg0.name).description(arg0.description).locationId(arg0.location_id)
                  .hostname(arg0.hostname).osArch(arg0.os_arch).osFamily(arg0.os_family).osDescription(
                           arg0.os_description).osVersion(arg0.os_version).os64Bit(arg0.os_64bit).group(arg0.group)
                  .loginPort(arg0.login_port).tags(arg0.tags).metadata(arg0.metadata).username(arg0.username).credential(arg0.credential).credentialUrl(
                           arg0.credential_url != null ? URI.create(arg0.credential_url) : null).sudoPassword(
                           arg0.sudo_password).build();
      }
   };

   public Node toNode() {
      return toNode.apply(this);
   }

   public static final Function<InputStream, YamlNode> inputStreamToYamlNode = new Function<InputStream, YamlNode>() {
      @Override
      public YamlNode apply(InputStream in) {
         if (in == null)
            return null;
         // note that snakeyaml also throws nosuchmethod error when you use the non-deprecated
         // constructor
         try {
            return (YamlNode) new Yaml(new Loader(new Constructor(YamlNode.class))).load(in);
         } finally {
            Closeables.closeQuietly(in);
         }
      }
   };

   public static YamlNode fromYaml(InputStream in) {
      return inputStreamToYamlNode.apply(in);
   }

   public static final Function<YamlNode, InputStream> yamlNodeToInputStream = new Function<YamlNode, InputStream>() {
      @Override
      public InputStream apply(YamlNode in) {
         if (in == null)
            return null;
         Builder<String, Object> prettier = ImmutableMap.builder();
         if (in.id != null)
            prettier.put("id", in.id);
         if (in.name != null)
            prettier.put("name", in.name);
         if (in.description != null)
            prettier.put("description", in.description);
         if (in.hostname != null)
            prettier.put("hostname", in.hostname);
         if (in.location_id != null)
            prettier.put("location_id", in.location_id);
         if (in.os_arch != null)
            prettier.put("os_arch", in.os_arch);
         if (in.os_family != null)
            prettier.put("os_family", in.os_family);
         if (in.os_description != null)
            prettier.put("os_description", in.os_description);
         if (in.os_version != null)
            prettier.put("os_version", in.os_version);
         if (in.os_64bit)
            prettier.put("os_64bit", in.os_64bit);
         if (in.login_port != 22)
            prettier.put("login_port", in.login_port);
         if (in.group != null)
            prettier.put("group", in.group);
         if (in.tags.size() != 0)
            prettier.put("tags", in.tags);
         if (in.metadata.size() != 0)
            prettier.put("metadata", in.metadata);
         if (in.username != null)
            prettier.put("username", in.username);
         if (in.credential != null)
            prettier.put("credential", in.credential);
         if (in.credential_url != null)
            prettier.put("credential_url", in.credential_url);
         if (in.sudo_password != null)
            prettier.put("sudo_password", in.sudo_password);
         DumperOptions options = new DumperOptions();
         options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
         return Strings2.toInputStream(new Yaml(options).dump(prettier.build()));
      }
   };

   public InputStream toYaml() {
      return yamlNodeToInputStream.apply(this);
   }

   public static YamlNode fromNode(Node in) {
      return nodeToYamlNode.apply(in);
   }

   public static final Function<Node, YamlNode> nodeToYamlNode = new Function<Node, YamlNode>() {
      @Override
      public YamlNode apply(Node arg0) {
         if (arg0 == null)
            return null;
         YamlNode yaml = new YamlNode();
         yaml.id = arg0.getId();
         yaml.name = arg0.getName();
         yaml.description = arg0.getDescription();
         yaml.hostname = arg0.getHostname();
         yaml.location_id = arg0.getLocationId();
         yaml.os_arch = arg0.getOsArch();
         yaml.os_family = arg0.getOsFamily();
         yaml.os_description = arg0.getOsDescription();
         yaml.os_version = arg0.getOsVersion();
         yaml.os_64bit = arg0.isOs64Bit();
         yaml.login_port = arg0.getLoginPort();
         yaml.group = arg0.getGroup();
         yaml.tags = ImmutableList.copyOf(arg0.getTags());
         yaml.metadata = ImmutableMap.copyOf(arg0.getMetadata());
         yaml.username = arg0.getUsername();
         yaml.credential = arg0.getCredential();
         yaml.credential_url = arg0.getCredentialUrl() != null ? arg0.getCredentialUrl().toASCIIString() : null;
         yaml.sudo_password = arg0.getSudoPassword();
         return yaml;
      }
   };

}
