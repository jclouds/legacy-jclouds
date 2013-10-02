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
package org.jclouds.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.BaseEncoding.base64;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
         checkArgument(path.getBytes().length < 255, String.format(
                  "maximum length of path is 255 bytes.  Path specified %s is %d bytes", path, path.getBytes().length));
         checkArgument(contents.length < 10 * 1024, String.format(
                  "maximum size of the file is 10KB.  Contents specified is %d bytes", contents.length));
      }

      public String getContents() {
         return contents;
      }

      public String getPath() {
         return path;
      }

   }

   @SuppressWarnings("unused")
   private static class ServerRequest {
      final String name;
      final int imageId;
      final int flavorId;
      Map<String, String> metadata;
      List<File> personality;
      Integer sharedIpGroupId;
      Addresses addresses;

      private ServerRequest(String name, int imageId, int flavorId) {
         this.name = name;
         this.imageId = imageId;
         this.flavorId = flavorId;
      }

   }

   private Map<String, String> metadata = Maps.newHashMap();
   private List<File> files = Lists.newArrayList();
   private Integer sharedIpGroupId;
   private String publicIp;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ServerRequest server = new ServerRequest(checkNotNull(postParams.get("name"), "name parameter not present").toString(),
               Integer.parseInt(checkNotNull(postParams.get("imageId"), "imageId parameter not present").toString()),
               Integer.parseInt(checkNotNull(postParams.get("flavorId"), "flavorId parameter not present").toString()));
      if (metadata.size() > 0)
         server.metadata = metadata;
      if (files.size() > 0)
         server.personality = files;
      if (sharedIpGroupId != null)
         server.sharedIpGroupId = this.sharedIpGroupId;
      if (publicIp != null) {
         server.addresses = Addresses.builder().publicAddresses(ImmutableSet.of(publicIp)).build();
      }
      return bindToRequest(request, ImmutableMap.of("server", server));
   }

   /**
    * You may further customize a cloud server by injecting data into the file system of the cloud
    * server itself. This is useful, for example, for inserting ssh keys, setting configuration
    * files, or storing data that you want to retrieve from within the instance itself. It is
    * intended to provide a minimal amount of launch-time personalization. If significant
    * customization is required, a custom image should be created. The max size of the file path
    * data is 255 bytes while the max size of the file contents is 10KB. Note that the file contents
    * should be encoded as a Base64 string and the 10KB limit refers to the number of bytes in the
    * decoded data not the number of characters in the encoded data. The maximum number of file
    * path/content pairs that can be supplied is 5. Any existing files that match the specified file
    * will be renamed to include the extension bak followed by a time stamp. For example, the file
    * /etc/passwd will be backed up as /etc/passwd.bak.1246036261.5785. All files will have root and
    * the root group as owner and group owner, respectively and will allow user and group read
    * access only (-r--r-----).
    */
   public CreateServerOptions withFile(String path, byte[] contents) {
      checkState(files.size() < 5, "maximum number of files allowed is 5");
      files.add(new File(path, contents));
      return this;
   }

   /**
    * A shared IP group is a collection of servers that can share IPs with other members of the
    * group. Any server in a group can share one or more public IPs with any other server in the
    * group. With the exception of the first server in a shared IP group, servers must be launched
    * into shared IP groups. A server may only be a member of one shared IP group.
    * 
    * <p/>
    * Servers in the same shared IP group can share public IPs for various high availability and
    * load balancing configurations. To launch an HA server, include the optional sharedIpGroupId
    * element and the server will be launched into that shared IP group.
    * <p />
    * 
    * Note: sharedIpGroupId is an optional parameter and for optimal performance, should ONLY be
    * specified when intending to share IPs between servers.
    * 
    * @see #withSharedIp(String)
    */
   public CreateServerOptions withSharedIpGroup(int id) {
      checkArgument(id > 0, "id must be positive or zero.  was: " + id);
      this.sharedIpGroupId = id;
      return this;
   }

   /**
    * Custom cloud server metadata can also be supplied at launch time. This metadata is stored in
    * the API system where it is retrievable by querying the API for server status. The maximum size
    * of the metadata key and value is each 255 bytes and the maximum number of key-value pairs that
    * can be supplied per server is 5.
    */
   public CreateServerOptions withMetadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      checkArgument(metadata.size() <= 5, "you cannot have more then 5 metadata values.  You specified: "
               + metadata.size());
      for (Entry<String, String> entry : metadata.entrySet()) {
         checkArgument(entry.getKey().getBytes().length < 255, String.format(
                  "maximum length of metadata key is 255 bytes.  Key specified %s is %d bytes", entry.getKey(), entry
                           .getKey().getBytes().length));
         checkArgument(entry.getKey().getBytes().length < 255, String.format(
                  "maximum length of metadata value is 255 bytes.  Value specified for %s (%s) is %d bytes", entry
                           .getKey(), entry.getValue(), entry.getValue().getBytes().length));
      }
      this.metadata = metadata;
      return this;
   }

   /**
    * Public IP addresses can be shared across multiple servers for use in various high availability
    * scenarios. When an IP address is shared to another server, the cloud network restrictions are
    * modified to allow each server to listen to and respond on that IP address (you may optionally
    * specify that the target server network configuration be modified). Shared IP addresses can be
    * used with many standard heartbeat facilities (e.g. keepalived) that monitor for failure and
    * manage IP failover.
    * 
    * <p/>
    * If you intend to use a shared IP on the server being created and have no need for a separate
    * public IP address, you may launch the server into a shared IP group and specify an IP address
    * from that shared IP group to be used as its public IP. You can accomplish this by specifying
    * the public shared IP address in your request. This is optional and is only valid if
    * sharedIpGroupId is also supplied.
    */
   public CreateServerOptions withSharedIp(String publicIp) {
      checkState(sharedIpGroupId != null, "sharedIp is invalid unless a shared ip group is specified.");
      this.publicIp = checkNotNull(publicIp, "ip");
      return this;
   }

   public static class Builder {

      /**
       * @see CreateServerOptions#withFile(String,byte[])
       */
      public static CreateServerOptions withFile(String path, byte[] contents) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withFile(path, contents);
      }

      /**
       * @see CreateServerOptions#withSharedIpGroup(int)
       */
      public static CreateServerOptions withSharedIpGroup(int id) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withSharedIpGroup(id);
      }

      /**
       * @see CreateServerOptions#withMetadata(Map<String, String>)
       */
      public static CreateServerOptions withMetadata(Map<String, String> metadata) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withMetadata(metadata);
      }

      /**
       * @see CreateServerOptions#withSharedIp(String)
       */
      public static CreateServerOptions withSharedIp(String publicIp) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withSharedIp(publicIp);
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}
