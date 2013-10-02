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
package org.jclouds.openstack.nova.v2_0.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.io.BaseEncoding.base64;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CreateServerOptions implements MapBinder {
   @Inject
   private BindToJsonPayload jsonBinder;

   static class File {
      private final String path;
      private final String contents;

      public File(String path, byte[] contents) {
         this.path = checkNotNull(path, "path");
         this.contents = base64().encode(checkNotNull(contents, "contents"));
         checkArgument(
               path.getBytes().length < 255,
               String.format("maximum length of path is 255 bytes.  Path specified %s is %d bytes", path,
                     path.getBytes().length));
         checkArgument(contents.length < 10 * 1024,
               String.format("maximum size of the file is 10KB.  Contents specified is %d bytes", contents.length));
      }

      public String getContents() {
         return contents;
      }

      public String getPath() {
         return path;
      }
      
      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         }
         if (object instanceof File) {
            final File other = File.class.cast(object);
            return equal(path, other.path);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(path);
      }

      @Override
      public String toString() {
         return toStringHelper("file").add("path", path).toString();
      }

   }

   private String keyName;
   private String adminPass;
   private Set<String> securityGroupNames = ImmutableSet.of();
   private Map<String, String> metadata = ImmutableMap.of();
   private List<File> personality = Lists.newArrayList();
   private byte[] userData;
   private String diskConfig;
   private Set<String> networks = ImmutableSet.of();

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof CreateServerOptions) {
         final CreateServerOptions other = CreateServerOptions.class.cast(object);
         return equal(keyName, other.keyName) && equal(securityGroupNames, other.securityGroupNames)
                  && equal(metadata, other.metadata) && equal(personality, other.personality)
                  && equal(adminPass, other.adminPass) && equal(diskConfig, other.diskConfig)
                  && equal(adminPass, other.adminPass) && equal(networks, other.networks);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(keyName, securityGroupNames, metadata, personality, adminPass, networks);
   }

   protected ToStringHelper string() {
      ToStringHelper toString = Objects.toStringHelper("").omitNullValues();
      toString.add("keyName", keyName);
      if (securityGroupNames.size() > 0)
         toString.add("securityGroupNames", securityGroupNames);
      if (metadata.size() > 0)
         toString.add("metadata", metadata);
      if (personality.size() > 0)
         toString.add("personality", personality);
      if (adminPass != null)
         toString.add("adminPassPresent", true);
      if (diskConfig != null)
         toString.add("diskConfig", diskConfig);
      toString.add("userData", userData == null ? null : new String(userData));
      if (!networks.isEmpty())
         toString.add("networks", networks);
      return toString;
   }

   @Override
   public String toString() {
      return string().toString();
   }

   static class ServerRequest {
      final String name;
      final String imageRef;
      final String flavorRef;
      String adminPass;
      Map<String, String> metadata;
      List<File> personality;
      String key_name;
      @Named("security_groups")
      Set<NamedThingy> securityGroupNames;
      String user_data;
      @Named("OS-DCF:diskConfig")
      String diskConfig;
      Set<Map<String, String>> networks;

      private ServerRequest(String name, String imageRef, String flavorRef) {
         this.name = name;
         this.imageRef = imageRef;
         this.flavorRef = flavorRef;
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest server = new ServerRequest(checkNotNull(postParams.get("name"), "name parameter not present").toString(),
            checkNotNull(postParams.get("imageRef"), "imageRef parameter not present").toString(),
            checkNotNull(postParams.get("flavorRef"), "flavorRef parameter not present").toString());
      if (metadata.size() > 0)
         server.metadata = metadata;
      if (personality.size() > 0)
         server.personality = personality;
      if (keyName != null)
         server.key_name = keyName;
      if (userData != null)
         server.user_data = base64().encode(userData);
      if (securityGroupNames.size() > 0) {
         server.securityGroupNames = Sets.newLinkedHashSet();
         for (String groupName : securityGroupNames) {
            server.securityGroupNames.add(new NamedThingy(groupName));
         }
      }
      if (adminPass != null) {
         server.adminPass = adminPass;
      }

      if (diskConfig != null) {
         server.diskConfig = diskConfig;
      }

      if (!networks.isEmpty()) {
         server.networks = Sets.newLinkedHashSet(); // ensures ordering is preserved - helps testing and more intuitive for users.
         for (String network : networks) {
            server.networks.add(ImmutableMap.of("uuid", network));
         }
      }

      return bindToRequest(request, ImmutableMap.of("server", server));
   }

   private static class NamedThingy extends ForwardingObject {
      private String name;

      private NamedThingy(String name) {
         this.name = name;
      }

      @Override
      protected Object delegate() {
         return name;
      }
   }

   /**
    * You may further customize a cloud server by injecting data into the file
    * system of the cloud server itself. This is useful, for example, for
    * inserting ssh keys, setting configuration files, or storing data that you
    * want to retrieve from within the instance itself. It is intended to
    * provide a minimal amount of launch-time personalization. If significant
    * customization is required, a custom image should be created. The max size
    * of the file path data is 255 bytes while the max size of the file contents
    * is 10KB. Note that the file contents should be encoded as a Base64 string
    * and the 10KB limit refers to the number of bytes in the decoded data not
    * the number of characters in the encoded data. The maximum number of file
    * path/content pairs that can be supplied is 5. Any existing files that
    * match the specified file will be renamed to include the extension bak
    * followed by a time stamp. For example, the file /etc/passwd will be backed
    * up as /etc/passwd.bak.1246036261.5785. All files will have root and the
    * root group as owner and group owner, respectively and will allow user and
    * group read access only (-r--r-----).
    */
   public CreateServerOptions writeFileToPath(byte[] contents, String path) {
      checkState(personality.size() < 5, "maximum number of files allowed is 5");
      personality.add(new File(path, contents));
      return this;
   }

   public CreateServerOptions adminPass(String adminPass) {
      checkNotNull(adminPass, "adminPass");
      this.adminPass = adminPass;
      return this;
   }

   /**
    * Custom cloud server metadata can also be supplied at launch time. This
    * metadata is stored in the API system where it is retrievable by querying
    * the API for server status. The maximum size of the metadata key and value
    * is each 255 bytes and the maximum number of key-value pairs that can be
    * supplied per server is 5.
    */
   public CreateServerOptions metadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      checkArgument(metadata.size() <= 5,
            "you cannot have more then 5 metadata values.  You specified: " + metadata.size());
      for (Entry<String, String> entry : metadata.entrySet()) {
         checkArgument(
               entry.getKey().getBytes().length < 255,
               String.format("maximum length of metadata key is 255 bytes.  Key specified %s is %d bytes",
                     entry.getKey(), entry.getKey().getBytes().length));
         checkArgument(entry.getKey().getBytes().length < 255, String.format(
               "maximum length of metadata value is 255 bytes.  Value specified for %s (%s) is %d bytes",
               entry.getKey(), entry.getValue(), entry.getValue().getBytes().length));
      }
      this.metadata = ImmutableMap.copyOf(metadata);
      return this;
   }

   /**
    * Custom user-data can be also be supplied at launch time.
    * It is retrievable by the instance and is often used for launch-time configuration
    * by instance scripts.
    */
   public CreateServerOptions userData(byte[] userData) {
       this.userData = userData;
       return this;
   }

   /**
    * A keypair name can be defined when creating a server. This key will be
    * linked to the server and used to SSH connect to the machine
    */
   public String getKeyPairName() {
      return keyName;
   }
   
   /**
    * @see #getKeyPairName()
    */
   public CreateServerOptions keyPairName(String keyName) {
      this.keyName = keyName;
      return this;
   }
   
   /**
    * 
    * Security groups the user specified to run servers with.
    * 
    * <h3>Note</h3>
    * 
    * This requires that {@link NovaApi#getSecurityGroupExtensionForZone(String)} to return
    * {@link Optional#isPresent present}
    */
   public Set<String> getSecurityGroupNames() {
      return securityGroupNames;
   }
   
   /**
    * 
    * Get custom networks specified for the server.
    * @return A set of uuids defined by Neutron (previously Quantum)
    * @see <a href="https://wiki.openstack.org/wiki/Neutron/APIv2-specification#Network">Neutron Networks<a/>
    *
    */
   public Set<String> getNetworks() {
      return networks;
   }

   /**
    *
    * @see #getSecurityGroupNames
    */
   public CreateServerOptions securityGroupNames(String... securityGroupNames) {
      return securityGroupNames(ImmutableSet.copyOf(checkNotNull(securityGroupNames, "securityGroupNames")));
   }

   /**
    * @see #getSecurityGroupNames
    */
   public CreateServerOptions securityGroupNames(Iterable<String> securityGroupNames) {
      for (String groupName : checkNotNull(securityGroupNames, "securityGroupNames"))
         checkNotNull(emptyToNull(groupName), "all security groups must be non-empty");
      this.securityGroupNames = ImmutableSet.copyOf(securityGroupNames);
      return this;
   }

   /**
    * When you create a server from an image with the diskConfig value set to 
    * {@link Server#DISK_CONFIG_AUTO}, the server is built with a single partition that is expanded to 
    * the disk size of the flavor selected. When you set the diskConfig attribute to 
    * {@link Server#DISK_CONFIG_MANUAL}, the server is built by using the partition scheme and file 
    * system that is in the source image.
    * <p/> 
    * If the target flavor disk is larger, remaining disk space is left unpartitioned. A server inherits the diskConfig
    * attribute from the image from which it is created. However, you can override the diskConfig value when you create
    * a server. This field is only present if the Disk Config extension is installed in your OpenStack deployment.
    */
   public String getDiskConfig() {
      return diskConfig;
   }
   
   /**
    * @see #getDiskConfig
    */
   public CreateServerOptions diskConfig(String diskConfig) {
      this.diskConfig = diskConfig;
      return this;
   }
   
   /**
    *
    * @see #getNetworks
    */
   public CreateServerOptions networks(String... networks) {
      return networks(ImmutableSet.copyOf(networks));
   }

   /**
    * @see #getNetworks
    */
   public CreateServerOptions networks(Iterable<String> networks) {
      for (String network : checkNotNull(networks, "networks"))
         checkNotNull(emptyToNull(network), "all networks must be non-empty");
      this.networks = ImmutableSet.copyOf(networks);
      return this;
   }

   public static class Builder {

      /**
       * @see CreateServerOptions#writeFileToPath
       */
      public static CreateServerOptions writeFileToPath(byte[] contents,String path) {
         CreateServerOptions options = new CreateServerOptions();
         return options.writeFileToPath(contents, path);
      }

      public static CreateServerOptions adminPass(String adminPass) {
         CreateServerOptions options = new CreateServerOptions();
         return options.adminPass(adminPass);
      }

      /**
       * @see CreateServerOptions#metadata(Map<String, String>)
       */
      public static CreateServerOptions metadata(Map<String, String> metadata) {
         CreateServerOptions options = new CreateServerOptions();
         return options.metadata(metadata);
      }

      /**
       * @see #getKeyPairName()
       */
      public static CreateServerOptions keyPairName(String keyName) {
         CreateServerOptions options = new CreateServerOptions();
         return options.keyPairName(keyName);
      }
      
      /**
       * @see CreateServerOptions#getSecurityGroupNames
       */
      public static CreateServerOptions securityGroupNames(String... groupNames) {
         CreateServerOptions options = new CreateServerOptions();
         return CreateServerOptions.class.cast(options.securityGroupNames(groupNames));
      }

      /**
       * @see CreateServerOptions#getSecurityGroupNames
       */
      public static CreateServerOptions securityGroupNames(Iterable<String> groupNames) {
         CreateServerOptions options = new CreateServerOptions();
         return CreateServerOptions.class.cast(options.securityGroupNames(groupNames));
      }

      /**
       * @see CreateServerOptions#getDiskConfig
       */
      public static CreateServerOptions diskConfig(String diskConfig) {
         CreateServerOptions options = new CreateServerOptions();
         return CreateServerOptions.class.cast(options.diskConfig(diskConfig));
      }

      /**
       * @see CreateServerOptions#getNetworks
       */
      public static CreateServerOptions networks(String... networks) {
         CreateServerOptions options = new CreateServerOptions();
         return CreateServerOptions.class.cast(options.networks(networks));
      }

      /**
       * @see CreateServerOptions#getNetworks
       */
      public static CreateServerOptions networks(Iterable<String> networks) {
         CreateServerOptions options = new CreateServerOptions();
         return CreateServerOptions.class.cast(options.networks(networks));
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
