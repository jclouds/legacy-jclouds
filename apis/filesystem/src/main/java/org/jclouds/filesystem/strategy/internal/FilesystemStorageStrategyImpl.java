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

package org.jclouds.filesystem.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Provider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.strategy.FilesystemStorageStrategy;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemStorageStrategyImpl implements FilesystemStorageStrategy {

   private static final String BACK_SLASH = "\\";
   /** The buffer size used to copy an InputStream to an OutputStream */
   private static final int COPY_BUFFER_SIZE = 1024;

   @Resource
   protected Logger logger = Logger.NULL;

   protected final Provider<BlobBuilder> blobBuilders;
   protected final String baseDirectory;
   protected final FilesystemContainerNameValidator filesystemContainerNameValidator;
   protected final FilesystemBlobKeyValidator filesystemBlobKeyValidator;

   @Inject
   protected FilesystemStorageStrategyImpl(Provider<BlobBuilder> blobBuilders,
         @Named(FilesystemConstants.PROPERTY_BASEDIR) String baseDir,
         FilesystemContainerNameValidator filesystemContainerNameValidator,
         FilesystemBlobKeyValidator filesystemBlobKeyValidator) {
      this.blobBuilders = checkNotNull(blobBuilders, "filesystem storage strategy blobBuilders");
      this.baseDirectory = checkNotNull(baseDir, "filesystem storage strategy base directory");
      this.filesystemContainerNameValidator = checkNotNull(filesystemContainerNameValidator,
            "filesystem container name validator");
      this.filesystemBlobKeyValidator = checkNotNull(filesystemBlobKeyValidator, "filesystem blob key validator");
   }

   @Override
   public boolean containerExists(String container) {
      filesystemContainerNameValidator.validate(container);
      return directoryExists(container, null);
   }

   @Override
   public boolean blobExists(String container, String key) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(key);
      return buildPathAndChecksIfFileExists(container, key);
   }

   @Override
   public boolean createContainer(String container) {
      logger.debug("Creating container %s", container);
      filesystemContainerNameValidator.validate(container);
      return createDirectoryWithResult(container, null);
   }

   @Override
   public void deleteContainer(String container) {
      filesystemContainerNameValidator.validate(container);
      deleteDirectory(container, null);
   }

   /**
    * Empty the directory of its content (files and subdirectories)
    * 
    * @param container
    */
   @Override
   public void clearContainer(final String container) {
      filesystemContainerNameValidator.validate(container);
      clearContainer(container, ListContainerOptions.Builder.recursive());
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      filesystemContainerNameValidator.validate(container);
      // TODO
      // now all is deleted, check it based on options
      try {
         File containerFile = openFolder(container);
         File[] children = containerFile.listFiles();
         if (null != children) {
            for (File child : children) {
               FileUtils.forceDelete(child);
            }
         }
      } catch (IOException e) {
         logger.error(e, "An error occurred while clearing container %s", container);
         Throwables.propagate(e);
      }
   }

   @Override
   public Blob newBlob(@ParamValidators({ FilesystemBlobKeyValidator.class }) String name) {
      filesystemBlobKeyValidator.validate(name);
      return blobBuilders.get().name(name).build();
   }

   @Override
   public void removeBlob(final String container, final String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      logger.debug("Deleting blob %s", fileName);
      File fileToBeDeleted = new File(fileName);
      fileToBeDeleted.delete();

      // now examins if the key of the blob is a complex key (with a directory structure)
      // and eventually remove empty directory
      removeDirectoriesTreeOfBlobKey(container, blobKey);
   }

   /**
    * Return an iterator that reports all the containers under base path
    * 
    * @return
    */
   @Override
   public Iterable<String> getAllContainerNames() {
      Iterable<String> containers = new Iterable<String>() {
         @Override
         public Iterator<String> iterator() {
            return new FileIterator(buildPathStartingFromBaseDir(), DirectoryFileFilter.INSTANCE);
         }
      };

      return containers;
   }

   /**
    * Returns a {@link File} object that links to the blob
    * 
    * @param container
    * @param blobKey
    * @return
    */
   @Override
   public File getFileForBlobKey(String container, String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      File blobFile = new File(fileName);
      return blobFile;
   }

   /**
    * Write a {@link Blob} {@link Payload} into a file
    * 
    * @param container
    * @param blobKey
    * @param payload
    * @throws IOException
    */
   @Override
   public void writePayloadOnFile(String container, String blobKey, Payload payload) throws IOException {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      File outputFile = null;
      OutputStream output = null;
      InputStream input = null;
      try {
         outputFile = getFileForBlobKey(container, blobKey);
         File parentDirectory = outputFile.getParentFile();
         if (!parentDirectory.exists()) {
            if (!parentDirectory.mkdirs()) {
               throw new IOException("An error occurred creating directory [" + parentDirectory.getName() + "].");
            }
         }
         output = new FileOutputStream(outputFile);
         input = payload.getInput();
         copy(input, output);

      } catch (IOException ex) {
         if (outputFile != null) {
            outputFile.delete();
         }
         throw ex;
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException ex) {
               // Does nothing
            }
         }
         if (output != null) {
            try {
               output.close();
            } catch (IOException ex) {
               // Does nothing
            }
         }
      }
   }

   /**
    * Returns all the blobs key inside a container
    * 
    * @param container
    * @return
    * @throws IOException
    */
   @Override
   public Iterable<String> getBlobKeysInsideContainer(String container) throws IOException {
      filesystemContainerNameValidator.validate(container);
      // check if container exists
      // TODO maybe an error is more appropriate
      if (!containerExists(container)) {
         return new HashSet<String>();
      }

      File containerFile = openFolder(container);
      final int containerPathLenght = containerFile.getAbsolutePath().length() + 1;
      Set<String> blobNames = new HashSet<String>() {

         private static final long serialVersionUID = 3152191346558570795L;

         @Override
         public boolean add(String e) {
            return super.add(e.substring(containerPathLenght));
         }
      };
      populateBlobKeysInContainer(containerFile, blobNames);
      return blobNames;
   }

   @Override
   public boolean directoryExists(String container, String directory) {
      return buildPathAndChecksIfDirectoryExists(container, directory);
   }

   @Override
   public void createDirectory(String container, String directory) {
      createDirectoryWithResult(container, directory);
   }

   @Override
   public void deleteDirectory(String container, String directory) {
      // create complete dir path
      String fullDirPath = buildPathStartingFromBaseDir(container, directory);
      try {
         FileUtils.forceDelete(new File(fullDirPath));
      } catch (IOException ex) {
         logger.error("An error occurred removing directory %s.", fullDirPath);
         Throwables.propagate(ex);
      }
   }

   @Override
   public long countBlobs(String container, ListContainerOptions options) {
      // TODO
      throw new UnsupportedOperationException("Not supported yet.");
   }

   // ---------------------------------------------------------- Private methods

   private boolean buildPathAndChecksIfFileExists(String... tokens) {
      String path = buildPathStartingFromBaseDir(tokens);
      File file = new File(path);
      boolean exists = file.exists() || file.isFile();
      return exists;
   }

   /**
    * Check if the file system resource whose name is obtained applying buildPath on the input path
    * tokens is a directory, otherwise a RuntimeException is thrown
    * 
    * @param tokens
    *           the tokens that make up the name of the resource on the file system
    */
   private boolean buildPathAndChecksIfDirectoryExists(String... tokens) {
      String path = buildPathStartingFromBaseDir(tokens);
      File file = new File(path);
      boolean exists = file.exists() || file.isDirectory();
      return exists;
   }

   /**
    * Facility method used to concatenate path tokens normalizing separators
    * 
    * @param pathTokens
    *           all the string in the proper order that must be concatenated in order to obtain the
    *           filename
    * @return the resulting string
    */
   protected String buildPathStartingFromBaseDir(String... pathTokens) {
      String normalizedToken = removeFileSeparatorFromBorders(normalize(baseDirectory), true);
      StringBuilder completePath = new StringBuilder(normalizedToken);
      if (pathTokens != null && pathTokens.length > 0) {
         for (int i = 0; i < pathTokens.length; i++) {
            if (pathTokens[i] != null) {
               normalizedToken = removeFileSeparatorFromBorders(normalize(pathTokens[i]), false);
               completePath.append(File.separator).append(normalizedToken);
            }
         }
      }
      return completePath.toString();
   }

   /**
    * Substitutes all the file separator occurrences in the path with a file separator for the
    * current operative system
    * 
    * @param pathToBeNormalized
    * @return
    */
   private String normalize(String pathToBeNormalized) {
      if (null != pathToBeNormalized && pathToBeNormalized.contains(BACK_SLASH)) {
         if (!BACK_SLASH.equals(File.separator)) {
            return pathToBeNormalized.replaceAll(BACK_SLASH, File.separator);
         }
      }
      return pathToBeNormalized;
   }

   /**
    * Remove leading and trailing {@link File.separator} character from the string.
    * 
    * @param pathToBeCleaned
    * @param remove
    *           only trailing separator char from path
    * @return
    */
   private String removeFileSeparatorFromBorders(String pathToBeCleaned, boolean onlyTrailing) {
      if (null == pathToBeCleaned || pathToBeCleaned.equals(""))
         return pathToBeCleaned;

      int beginIndex = 0;
      int endIndex = pathToBeCleaned.length();

      // search for separator chars
      if (!onlyTrailing) {
         if (pathToBeCleaned.substring(0, 1).equals(File.separator))
            beginIndex = 1;
      }
      if (pathToBeCleaned.substring(pathToBeCleaned.length() - 1).equals(File.separator))
         endIndex--;

      return pathToBeCleaned.substring(beginIndex, endIndex);
   }

   /**
    * Removes recursively the directory structure of a complex blob key, only if the directory is
    * empty
    * 
    * @param container
    * @param normalizedKey
    */
   private void removeDirectoriesTreeOfBlobKey(String container, String blobKey) {
      String normalizedBlobKey = normalize(blobKey);
      // exists is no path is present in the blobkey
      if (!normalizedBlobKey.contains(File.separator))
         return;

      File file = new File(normalizedBlobKey);
      // TODO
      // "/media/data/works/java/amazon/jclouds/master/filesystem/aa/bb/cc/dd/eef6f0c8-0206-460b-8870-352e6019893c.txt"
      String parentPath = file.getParent();
      // no need to manage "/" parentPath, because "/" cannot be used as start
      // char of blobkey
      if (null != parentPath || "".equals(parentPath)) {
         // remove parent directory only it's empty
         File directory = new File(buildPathStartingFromBaseDir(container, parentPath));
         String[] children = directory.list();
         if (null == children || children.length == 0) {
            directory.delete();
            // recursively call for removing other path
            removeDirectoriesTreeOfBlobKey(container, parentPath);
         }
      }
   }

   private File openFolder(String folderName) throws IOException {
      String baseFolderName = buildPathStartingFromBaseDir(folderName);
      File folder = new File(baseFolderName);
      if (folder.exists()) {
         if (!folder.isDirectory()) {
            throw new IOException("Resource " + baseFolderName + " isn't a folder.");
         }
      }
      return folder;
   }

   private class FileIterator implements Iterator<String> {
      int currentFileIndex = 0;
      File[] children = new File[0];
      File currentFile = null;

      public FileIterator(String fileName, FileFilter filter) {
         File file = new File(fileName);
         if (file.exists() && file.isDirectory()) {
            children = file.listFiles(filter);
         }
      }

      @Override
      public boolean hasNext() {
         return currentFileIndex < children.length;
      }

      @Override
      public String next() {
         currentFile = children[currentFileIndex++];
         return currentFile.getName();
      }

      @Override
      public void remove() {
         if (currentFile != null && currentFile.exists()) {
            if (!currentFile.delete()) {
               throw new RuntimeException("An error occurred deleting " + currentFile.getName());
            }
         }
      }
   }

   private void populateBlobKeysInContainer(File directory, Set<String> blobNames) {
      File[] children = directory.listFiles();
      for (File child : children) {
         if (child.isFile()) {
            blobNames.add(child.getAbsolutePath());
         } else if (child.isDirectory()) {
            populateBlobKeysInContainer(child, blobNames);
         }
      }
   }

   /**
    * Creates a directory and returns the result
    * 
    * @param container
    * @param directory
    * @return true if the directory was created, otherwise false
    */
   protected boolean createDirectoryWithResult(String container, String directory) {
      String directoryFullName = buildPathStartingFromBaseDir(container, directory);
      logger.debug("Creating directory %s", directoryFullName);

      // cannot use directoryFullName, because the following method rebuild
      // another time the path starting from base directory
      if (buildPathAndChecksIfDirectoryExists(container, directory)) {
         logger.debug("Directory %s already exists", directoryFullName);
         return false;
      }

      File directoryToCreate = new File(directoryFullName);
      boolean result = directoryToCreate.mkdirs();
      return result;
   }

   /**
    * Copy from an InputStream to an OutputStream.
    * 
    * @param input
    *           The InputStream
    * @param output
    *           The OutputStream
    * @return the number of bytes copied
    * @throws IOException
    *            if an error occurs
    */
   private long copy(InputStream input, OutputStream output) throws IOException {
      byte[] buffer = new byte[COPY_BUFFER_SIZE];
      long count = 0;
      while (true) {
         int read = input.read(buffer);
         if (read < 0) {
            break;
         }
         count += read;

         output.write(buffer, 0, read);
      }
      output.flush();
      return count;
   }

}
