package org.jclouds.atmosonline.saas.blobstore.strategy;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * Recursively remove a path.
 * 
 * @author Adrian Cole
 */
@Singleton
public class RecursiveRemove implements ClearListStrategy, ClearContainerStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final AtmosStorageClient connection;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public RecursiveRemove(AtmosStorageClient connection) {
      this.connection = connection;
   }

   public void execute(String containerName) {
      logger.debug("clearing container ", containerName);
      execute(containerName, new ListContainerOptions().recursive());
      logger.trace("cleared container " + containerName);
   }

   private Future<Void> rm(final String fullPath, FileType type, boolean recursive)
            throws InterruptedException, ExecutionException, TimeoutException {
      Set<Future<Void>> deletes = Sets.newHashSet();
      if ((type == FileType.DIRECTORY) && recursive) {
         for (DirectoryEntry child : connection.listDirectory(fullPath).get(10, TimeUnit.SECONDS)) {
            deletes.add(rm(fullPath + "/" + child.getObjectName(), child.getType(), true));
         }
      }
      for (Future<Void> isdeleted : deletes) {
         isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      }
      return new FutureFunctionWrapper<Void, Void>(connection.deletePath(fullPath),
               new Function<Void, Void>() {

                  public Void apply(Void from) {
                     try {
                        if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
                           public Boolean get() {
                              return !connection.pathExists(fullPath);
                           }
                        }, requestTimeoutMilliseconds)) {
                           throw new IllegalStateException(fullPath + " still exists after deleting!");
                        }
                        return null;
                     } catch (InterruptedException e) {
                        throw new IllegalStateException(fullPath + " still exists after deleting!",e);
                     }
                  }

               });
   }

   public void execute(final String containerName, ListContainerOptions options) {
      String path = containerName;
      if (options.getPath() != null)
         path += "/" + options.getPath();
      Set<Future<Void>> deletes = Sets.newHashSet();
      try {
         for (DirectoryEntry md : connection.listDirectory(path).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS)) {
            deletes.add(rm(path + "/" + md.getObjectName(), md.getType(), options.isRecursive()));
         }
         for (Future<Void> isdeleted : deletes) {
            isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         }
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error deleting path: " + path, e);
      }
   }

}