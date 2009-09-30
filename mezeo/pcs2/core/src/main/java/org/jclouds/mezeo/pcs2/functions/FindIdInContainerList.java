package org.jclouds.mezeo.pcs2.functions;

import java.util.List;

import javax.inject.Inject;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

public class FindIdInContainerList implements Function<String, String> {
   private PCSBlobStore connection;

   @Inject
   public FindIdInContainerList(PCSBlobStore connection) {
      this.connection = connection;
   }

   public String apply(String key) {
      List<ContainerMetadata> response = connection.listContainers();
      return idForNameInListOrException(key, response);
   }

   @VisibleForTesting
   String idForNameInListOrException(String toFind, List<ContainerMetadata> containerMetadataList) {
      for (ContainerMetadata data : containerMetadataList) {
         if (toFind.equals(data.getName())) {
            String path = data.getUrl().getPath();
            int indexAfterContainersSlash = path.indexOf("containers/") + "containers/".length();
            return path.substring(indexAfterContainersSlash);
         }
      }
      throw new ContainerNotFoundException(toFind);
   }

}