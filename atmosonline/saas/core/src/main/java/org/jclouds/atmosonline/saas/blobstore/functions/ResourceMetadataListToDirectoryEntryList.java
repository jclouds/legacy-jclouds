package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.internal.BoundedTreeSet;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceMetadataListToDirectoryEntryList
         implements
         Function<org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>, BoundedSortedSet<? extends DirectoryEntry>> {

   public BoundedSortedSet<DirectoryEntry> apply(
            org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> from) {

      return new BoundedTreeSet<DirectoryEntry>(Iterables.transform(from,
               new Function<ResourceMetadata, DirectoryEntry>() {
                  public DirectoryEntry apply(ResourceMetadata from) {
                     FileType type = (from.getType() == ResourceType.FOLDER || from.getType() == ResourceType.RELATIVE_PATH) ? FileType.DIRECTORY
                              : FileType.REGULAR;
                     return new DirectoryEntry(from.getId(), type, from.getName());
                  }

               }), from.getMarker());

   }
}