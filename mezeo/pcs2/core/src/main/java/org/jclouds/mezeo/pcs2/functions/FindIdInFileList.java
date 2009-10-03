package org.jclouds.mezeo.pcs2.functions;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpException;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

public class FindIdInFileList implements Function<Key, String> {
   private PCSBlobStore connection;

   @Inject
   public FindIdInFileList(PCSBlobStore connection) {
      this.connection = connection;
   }

   public String apply(Key key) {
      SortedSet<FileMetadata> response;
      try {
         response = connection.listBlobs(key.getContainer()).get(10, TimeUnit.SECONDS);
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException("could not list blobs for " + Arrays.asList(key.getContainer()), e);
      }
      return idForNameInListOrException(key.getContainer(), key.getKey(), response);
   }

   @VisibleForTesting
   String idForNameInListOrException(String container, String toFind, SortedSet<FileMetadata> response) {
      for (FileMetadata data : response) {
         if (toFind.equals(data.getKey())) {
            String path = data.getUrl().getPath();
            int indexAfterContainersSlash = path.indexOf("files/") + "files/".length();
            return path.substring(indexAfterContainersSlash);
         }
      }
      throw new KeyNotFoundException(container, toFind);
   }

}