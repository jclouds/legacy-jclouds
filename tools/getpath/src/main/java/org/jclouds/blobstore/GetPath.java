/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map.Entry;

import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpUtils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

/**
 * 
 * Usage is: java GetPath blobstore://identity:key@service/container/path destinationPath
 * 
 * @author Adrian Cole
 */
public class GetPath {

   public static String INVALID_SYNTAX = "Invalid parameters. Syntax is: blobstore://identity:key@service/container/path destinationPath";

   public static void main(String... args) throws IOException {
      if (args.length < 2)
         throw new IllegalArgumentException(INVALID_SYNTAX);
      URI uri;
      try {
         uri = HttpUtils.createUri(args[0]);
         checkArgument(uri.getScheme().equals("blobstore"), "wrong scheme");
      } catch (IllegalArgumentException e) {
         throw new IllegalArgumentException(String.format("%s%n%s", e.getMessage(), INVALID_SYNTAX));
      }
      checkArgument(args[1] != null, String.format("destination must be specified%n%s",
               INVALID_SYNTAX));

      File destinationDir = new File(args[1]);
      destinationDir.mkdirs();

      String provider = uri.getHost();
      Credentials creds = Credentials.parse(uri);

      BlobStoreContext context = new BlobStoreContextFactory().createContext(provider,
               creds.identity, creds.credential);

      String path = uri.getPath();
      if (path.startsWith("/"))
         path = path.substring(1);
      String container = BlobStoreUtils.parseContainerFromPath(path);
      String directory = BlobStoreUtils.parsePrefixFromPath(path);
      copyDirectoryToDestination(context, container, directory, destinationDir);
   }

   private static void copyDirectoryToDestination(BlobStoreContext context, String container,
            String directory, File destinationDir) throws FileNotFoundException, IOException {
      InputStream input = null;

      try {
         checkState(context.getBlobStore().containerExists(container), String.format(
                  "source container %s does not exist", directory, container));
         checkState(context.getBlobStore().directoryExists(container, directory), String.format(
                  "source directory %s does not exist in container %s", directory, container));

         String path = container + "/" + directory;
         InputStreamMap map = context.createInputStreamMap(path);
         System.out.printf("fetching %d entries from %s %s%n", map.size(), context
                  .getProviderSpecificContext().getIdentity(), path);
         for (Entry<String, InputStream> entry : map.entrySet()) {
            System.out.printf("getting file: %s/%s%n", path, entry.getKey());
            input = entry.getValue();
            File file = new File(destinationDir, entry.getKey());
            OutputStream out = new FileOutputStream(file);
            ByteStreams.copy(input, out);
            out.flush();
            out.close();
         }

      } finally {
         // Close connecton
         Closeables.closeQuietly(input);
      }
   }

}
