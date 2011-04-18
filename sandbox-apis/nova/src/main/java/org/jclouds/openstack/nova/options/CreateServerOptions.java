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

package org.jclouds.openstack.nova.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CreateServerOptions extends BindToJsonPayload {

   static class File {
      private final String path;
      private final String contents;

      public File(String path, byte[] contents) {
         this.path = checkNotNull(path, "path");
         this.contents = Base64.encodeBytes(checkNotNull(contents, "contents"));
         checkArgument(path.getBytes().length < 255, String.format(
                  "maximum length of path is 255 bytes.  Path specified %s is %d bytes", path, path
                           .getBytes().length));
         checkArgument(contents.length < 10 * 1024, String.format(
                  "maximum size of the file is 10KB.  Contents specified is %d bytes",
                  contents.length));
      }

      public String getContents() {
         return contents;
      }

      public String getPath() {
         return path;
      }

   }

   @SuppressWarnings("unused")
   private class ServerRequest {
      final String name;
      final String imageRef;
      final String flavorRef;
      Map<String, String> metadata;
      List<File> personality;

      private ServerRequest(String name, String imageRef, String flavorRef) {
         this.name = name;
         this.imageRef = imageRef;
         this.flavorRef = flavorRef;
      }

   }

   private Map<String, String> metadata = Maps.newHashMap();
   private List<File> files = Lists.newArrayList();

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      ServerRequest server = new ServerRequest(checkNotNull(postParams.get("name"),
               "name parameter not present"), checkNotNull(postParams
               .get("imageRef"), "imageRef parameter not present"), checkNotNull(
            postParams.get("flavorRef"), "flavorRef parameter not present"));
      if (metadata.size() > 0)
         server.metadata = metadata;
      if (files.size() > 0)
         server.personality = files;

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
    * Custom cloud server metadata can also be supplied at launch time. This metadata is stored in
    * the API system where it is retrievable by querying the API for server status. The maximum size
    * of the metadata key and value is each 255 bytes and the maximum number of key-value pairs that
    * can be supplied per server is 5.
    */
   public CreateServerOptions withMetadata(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      checkArgument(metadata.size() <= 5,
               "you cannot have more then 5 metadata values.  You specified: " + metadata.size());
      for (Entry<String, String> entry : metadata.entrySet()) {
         checkArgument(entry.getKey().getBytes().length < 255, String.format(
                  "maximum length of metadata key is 255 bytes.  Key specified %s is %d bytes",
                  entry.getKey(), entry.getKey().getBytes().length));
         checkArgument(
                  entry.getKey().getBytes().length < 255,
                  String
                           .format(
                                    "maximum length of metadata value is 255 bytes.  Value specified for %s (%s) is %d bytes",
                                    entry.getKey(), entry.getValue(),
                                    entry.getValue().getBytes().length));
      }
      this.metadata = metadata;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateServerOptions#withFile(String,byte [])
       */
      public static CreateServerOptions withFile(String path, byte[] contents) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withFile(path, contents);
      }

      /**
       * @see CreateServerOptions#withMetadata(Map<String, String>)
       */
      public static CreateServerOptions withMetadata(Map<String, String> metadata) {
         CreateServerOptions options = new CreateServerOptions();
         return options.withMetadata(metadata);
      }
   }
}
