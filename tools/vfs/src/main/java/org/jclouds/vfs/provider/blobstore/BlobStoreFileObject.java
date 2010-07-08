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
package org.jclouds.vfs.provider.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.blobstore.util.BlobStoreUtils.newBlob;
import static org.jclouds.util.Patterns.LEADING_SLASHES;
import static org.jclouds.util.Patterns.TRAILING_SLASHES;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileNotFolderException;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.FileTypeHasNoContentException;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.util.FileObjectUtils;
import org.apache.commons.vfs.util.MonitorOutputStream;
import org.apache.commons.vfs.util.RandomAccessMode;
import org.apache.log4j.Logger;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.ConcatenateContainerLists;
import org.jclouds.util.Utils;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class BlobStoreFileObject extends AbstractFileObject {
   private final BlobStoreContext context;
   private final ConcatenateContainerLists lister;
   private final String container;
   private StorageMetadata metadata;
   private static final Logger logger = Logger.getLogger(BlobStoreFileObject.class);
   private static final Pattern UNDESCRIBED = Pattern.compile("[^/]*//*");

   public BlobStoreFileObject(FileName fileName, BlobStoreFileSystem fileSystem,
            BlobStoreContext context, String container) throws FileSystemException {
      super(fileName, fileSystem);
      this.context = checkNotNull(context, "context");
      this.container = checkNotNull(container, "container");
      this.lister = checkNotNull(new ConcatenateContainerLists(context.getBlobStore()), "lister");

   }

   private class BlobStoreOutputStream extends MonitorOutputStream {

      private final BlobStore context;
      private final Blob blob;
      private final File file;

      public BlobStoreOutputStream(File file, BlobStore context, Blob blob)
               throws FileNotFoundException {
         super(Channels.newOutputStream(new RandomAccessFile(file, "rw").getChannel()));
         this.context = context;
         this.file = file;
         this.blob = blob;
      }

      protected void onClose() throws IOException {
         try {
            blob.setPayload(file);
            context.getContext().utils().encryption().generateMD5BufferingIfNotRepeatable(blob);
            logger.info(String.format(">> put: %s/%s %d bytes", getContainer(),
                     getNameTrimLeadingSlashes(), blob.getPayload().getContentLength()));
            String tag = context.putBlob(getContainer(), blob);
            logger.info(String.format("<< tag %s: %s/%s", tag, getContainer(),
                     getNameTrimLeadingSlashes()));
         } finally {
            file.delete();
         }
      }
   }

   public static File allocateFile() throws IOException {
      return File.createTempFile("jclouds.", ".blobstore");
   }

   @Override
   protected long doGetContentSize() throws Exception {
      if (metadata == null || metadata.getSize() == null || metadata.getSize() == 0) {
         getMetadataAtPath(getNameTrimLeadingSlashes());
      }
      return metadata.getSize() != null ? metadata.getSize() : 0;
   }

   @Override
   protected InputStream doGetInputStream() throws Exception {
      if (!getType().hasContent()) {
         throw new FileSystemException("vfs.provider/read-not-file.error", getName());
      }
      if (metadata != null && metadata.getType() != StorageType.BLOB) {
         throw new FileTypeHasNoContentException(getName());
      }
      logger.info(String.format(">> get: %s/%s", getContainer(), getNameTrimLeadingSlashes()));
      Blob blob = getBlobStore().getBlob(getContainer(), getNameTrimLeadingSlashes());
      return blob.getPayload().getInput();
   }

   String getNameTrimLeadingSlashes() {
      return Utils.replaceAll(getName().getPath(), LEADING_SLASHES, "");
   }

   @Override
   protected FileType doGetType() throws Exception {
      if (metadata == null)
         return FileType.IMAGINARY;
      if (getNameTrimLeadingSlashes().equals("") || getName().getParent() == null)
         return FileType.FOLDER;
      return (metadata.getType() == StorageType.BLOB) ? FileType.FILE : FileType.FOLDER;
   }

   @Override
   protected FileObject[] doListChildrenResolved() throws Exception {
      // if metadata is null, then the path does not exist, as doAttach would have certainly set
      // this.
      doAttach();
      if (metadata == null)
         throw new FileNotFolderException(getName());
      ListContainerOptions options = new ListContainerOptions();
      String name = getNameTrimLeadingSlashes();
      if (!name.equals("") && !name.equals("/")) {
         options.inDirectory(name + "/");
         logger.info(String.format(">> list: %s[%s]", getContainer(), name));
      } else {
         logger.info(String.format(">> list: %s", getContainer()));
      }
      Iterable<? extends StorageMetadata> list = lister.execute(getContainer(), options);
      Set<BlobStoreFileObject> children = Sets.newHashSet();
      loop: for (StorageMetadata md : list) {
         if (!md.getName().equals("")) {
            if (name.equals(md.getName()) && md.getType() != StorageType.BLOB) {
               continue loop;
            }
            String childName = Utils.replaceAll(md.getName(), UNDESCRIBED, "");
            BlobStoreFileObject fo = (BlobStoreFileObject) FileObjectUtils
                     .getAbstractFileObject(getFileSystem().resolveFile(
                              getFileSystem().getFileSystemManager().resolveName(getName(),
                                       childName, NameScope.CHILD)));
            children.add(fo);
         }
      }
      logger.info(String.format("<< list: %s", children));
      return children.toArray(new BlobStoreFileObject[] {});
   }

   @Override
   public FileObject[] getChildren() throws FileSystemException {
      if (metadata != null && metadata.getType() == StorageType.BLOB)
         throw new FileNotFolderException(getName());
      if (metadata == null) {
         try {
            FileType type = doGetType();
            if (type == FileType.FILE) {
               throw new FileNotFolderException(getName());
            }
         } catch (Exception ex) {
            Throwables.propagateIfPossible(ex, FileNotFolderException.class);
            throw new FileNotFolderException(getName(), ex);
         }
      }
      return super.getChildren();
   }

   /**
    * Lists the children of the file.
    */
   protected String[] doListChildren() throws Exception {
      // use doListChildrenResolved for performance
      return null;
   }

   @Override
   protected void doDelete() throws Exception {
      logger.info(String.format(">> delete: %s/%s", getContainer(), getNameTrimLeadingSlashes()));
      if (metadata != null)
         deleteBasedOnType();
      else
         deleteBlob(getNameTrimLeadingSlashes());
   }

   private void deleteBasedOnType() {
      if (metadata.getType() != StorageType.CONTAINER) {
         deleteBlob(metadata.getProviderId());
      } else {
         getBlobStore().deleteContainer(getContainer());
         logger.info(String.format("<< deleted container: %s", getContainer()));
      }
   }

   private void deleteBlob(String id) {
      getBlobStore().removeBlob(getContainer(), getNameTrimLeadingSlashes());
      logger.info(String.format("<< deleted blob: %s/%s", getContainer(),
               getNameTrimLeadingSlashes()));
   }

   @Override
   protected OutputStream doGetOutputStream(boolean bAppend) throws Exception {
      File file = allocateFile();
      checkState(file != null, "file was null");
      if (metadata != null) {
         return new BlobStoreOutputStream(file, getBlobStore(), newBlob(getBlobStore(), metadata));
      } else {
         return new BlobStoreOutputStream(file, getBlobStore(), getBlobStore().newBlob(
                  getNameTrimLeadingSlashes()));
      }
   }

   @Override
   protected void doCreateFolder() throws Exception {
      logger.info(String
               .format(">> put folder: %s/%s", getContainer(), getNameTrimLeadingSlashes()));
      getBlobStore().createDirectory(getContainer(), getNameTrimLeadingSlashes());
   }

   @Override
   protected void doAttach() throws Exception {
      String name = getNameTrimLeadingSlashes();
      if (name.equals("")) {
         logger.info(String.format(">> head: %s", getContainer()));
         getContainer(name);
      } else {
         logger.info(String.format(">> head: %s/%s", getContainer(), name));
         try {
            getMetadataAtPath(name);
         } catch (KeyNotFoundException e) {
            tryDirectoryAtPath(name);
         }
      }
   }

   private void getContainer(String name) {
      metadata = Iterables.find(getBlobStore().list(), new Predicate<StorageMetadata>() {
         @Override
         public boolean apply(StorageMetadata input) {
            return input.getType() == StorageType.CONTAINER && input.getName().equals(container);
         }
      });
      logger.info(String.format("<< container: %s/%s", container, name));
   }

   private void getMetadataAtPath(String name) {
      metadata = getBlobStore().blobMetadata(getContainer(), name);
      if (metadata.getType() != StorageType.BLOB) {
         logger.info(String.format("<< dir: %s/%s", getContainer(), name));
      } else {
         logger.info(String.format("<< blob: %s/%s", getContainer(), name));
      }
   }

   private void tryDirectoryAtPath(final String name) {
      ListContainerOptions options = new ListContainerOptions();
      if (getName().getParent() != null) {
         String dir = trimLeadingAndTrailingSlashes(getName().getParent().getPath());
         if (!dir.equals(""))
            options.inDirectory(dir);
      }
      try {
         metadata = Iterables.find(lister.execute(getContainer(), options),
                  new Predicate<StorageMetadata>() {
                     @Override
                     public boolean apply(StorageMetadata input) {
                        return input.getType() != StorageType.BLOB && input.getName().equals(name);
                     }
                  });
         logger.info(String.format("<< dir: %s/%s", getContainer(), name));
      } catch (NoSuchElementException nse) {
         metadata = null;
         logger.info(String.format("<< not found: %s/%s", getContainer(), name));
      } catch (ContainerNotFoundException cnfe) {
         metadata = null;
         logger.info(String.format("<< not found: %s", getContainer()));
      }
   }

   public String trimLeadingAndTrailingSlashes(String in) {
      return Utils.replaceAll(Utils.replaceAll(in, LEADING_SLASHES, ""), TRAILING_SLASHES, "");
   }

   @Override
   protected RandomAccessContent doGetRandomAccessContent(final RandomAccessMode mode)
            throws Exception {
      return new BlobStoreRandomAccessContent(this, mode);
   }

   protected void doDetach() throws Exception {
      metadata = null;
   }

   @Override
   protected long doGetLastModifiedTime() throws Exception {
      if (metadata == null || metadata.getLastModified() == null) {
         getMetadataAtPath(getNameTrimLeadingSlashes());
      }
      return metadata.getLastModified() != null ? metadata.getLastModified().getTime() : super
               .doGetLastModifiedTime();
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Map doGetAttributes() throws Exception {
      if (metadata == null || metadata.getUserMetadata() == null) {
         getMetadataAtPath(getNameTrimLeadingSlashes());
      }
      return metadata.getUserMetadata() != null ? metadata.getUserMetadata() : super
               .doGetAttributes();
   }

   @Override
   protected void doSetAttribute(String atttrName, Object value) throws Exception {
      if (metadata == null || metadata.getUserMetadata() == null) {
         metadata.getUserMetadata().put(atttrName, value.toString());
      }
      super.doSetAttribute(atttrName, value);
   }

   public BlobStore getBlobStore() {
      return context.getBlobStore();
   }

   public String getContainer() {
      return container;
   }
}
