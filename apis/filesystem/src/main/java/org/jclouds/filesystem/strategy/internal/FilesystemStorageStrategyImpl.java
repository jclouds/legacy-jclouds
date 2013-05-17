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
package org.jclouds.filesystem.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.BaseEncoding.base16;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.domain.Location;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.jclouds.filesystem.predicates.validators.FilesystemContainerNameValidator;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.util.Utils;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.ParamValidators;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemStorageStrategyImpl implements LocalStorageStrategy {

   private static final String BACK_SLASH = "\\";

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
   public Iterable<String> getAllContainerNames() {
      File[] files = new File(buildPathStartingFromBaseDir()).listFiles();
      if (files == null) {
         return ImmutableList.of();
      }
      ImmutableList.Builder<String> containers = ImmutableList.builder();
      for (File file : files) {
         if (file.isDirectory()) {
            containers.add(file.getName());
         }
      }
      return containers.build();
   }

   @Override
   public boolean createContainerInLocation(String container, Location location) {
      // TODO: implement location
      logger.debug("Creating container %s", container);
      filesystemContainerNameValidator.validate(container);
      return createDirectoryWithResult(container, null);
   }

   @Override
   public void deleteContainer(String container) {
      filesystemContainerNameValidator.validate(container);
      deleteDirectory(container, null);
   }

   @Override
   public void clearContainer(final String container) {
      clearContainer(container, ListContainerOptions.Builder.recursive());
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      filesystemContainerNameValidator.validate(container);
      // TODO implement options
      try {
         File containerFile = openFolder(container);
         File[] children = containerFile.listFiles();
         if (null != children) {
            for (File child : children)
               Utils.deleteRecursively(child);
         }
      } catch (IOException e) {
         logger.error(e, "An error occurred while clearing container %s", container);
         Throwables.propagate(e);
      }
   }

   @Override
   public boolean blobExists(String container, String key) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(key);
      return buildPathAndChecksIfFileExists(container, key);
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
      Set<String> blobNames = Sets.newHashSet();
      if (!containerExists(container)) {
         return blobNames;
      }

      File containerFile = openFolder(container);
      final int containerPathLength = containerFile.getAbsolutePath().length() + 1;
      populateBlobKeysInContainer(containerFile, blobNames, new Function<String, String>() {
         @Override
         public String apply(String string) {
            return string.substring(containerPathLength);
         }
      });
      return blobNames;
   }

   @Override
   public Blob getBlob(final String container, final String key) {
      BlobBuilder builder = blobBuilders.get();
      builder.name(key);
      File file = getFileForBlobKey(container, key);
      try {
         builder.payload(file).calculateMD5();
      } catch (IOException e) {
         logger.error("An error occurred calculating MD5 for blob %s from container ", key, container);
         Throwables.propagateIfPossible(e);
      }
      Blob blob = builder.build();
      if (blob.getPayload().getContentMetadata().getContentMD5() != null)
         blob.getMetadata().setETag(base16().lowerCase().encode(blob.getPayload().getContentMetadata().getContentMD5()));
      return blob;
   }

   @Override
   public String putBlob(final String containerName, final Blob blob) throws IOException {
      String blobKey = blob.getMetadata().getName();
      Payload payload = blob.getPayload();
      filesystemContainerNameValidator.validate(containerName);
      filesystemBlobKeyValidator.validate(blobKey);
      File outputFile = getFileForBlobKey(containerName, blobKey);
      try {
         Files.createParentDirs(outputFile);
         if (payload.getRawContent() instanceof File)
            Files.copy((File) payload.getRawContent(), outputFile);
         else {
            payload = Payloads.newPayload(ByteStreams.toByteArray(payload));
            Files.copy(payload, outputFile);
         }
         Payloads.calculateMD5(payload);
         String eTag = base16().lowerCase().encode(payload.getContentMetadata().getContentMD5());
         return eTag;
      } catch (IOException ex) {
         if (outputFile != null) {
            if (!outputFile.delete()) {
               logger.debug("Could not delete %s", outputFile);
            }
         }
         throw ex;
      } finally {
         payload.release();
      }
   }

   @Override
   public void removeBlob(final String container, final String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      logger.debug("Deleting blob %s", fileName);
      File fileToBeDeleted = new File(fileName);
      if (!fileToBeDeleted.delete()) {
         logger.debug("Could not delete %s", fileToBeDeleted);
      }

      // now examine if the key of the blob is a complex key (with a directory structure)
      // and eventually remove empty directory
      removeDirectoriesTreeOfBlobKey(container, blobKey);
   }

   @Override
   public Location getLocation(final String containerName) {
      return null;
   }

   @Override
   public String getSeparator() {
      return File.separator;
   }

   public boolean createContainer(String container) {
      filesystemContainerNameValidator.validate(container);
      return createContainerInLocation(container, null);
   }

   public Blob newBlob(@ParamValidators({ FilesystemBlobKeyValidator.class }) String name) {
      filesystemBlobKeyValidator.validate(name);
      return blobBuilders.get().name(name).build();
   }

   /**
    * Returns a {@link File} object that links to the blob
    *
    * @param container
    * @param blobKey
    * @return
    */
   public File getFileForBlobKey(String container, String blobKey) {
      filesystemContainerNameValidator.validate(container);
      filesystemBlobKeyValidator.validate(blobKey);
      String fileName = buildPathStartingFromBaseDir(container, blobKey);
      File blobFile = new File(fileName);
      return blobFile;
   }

   public boolean directoryExists(String container, String directory) {
      return buildPathAndChecksIfDirectoryExists(container, directory);
   }

   public void createDirectory(String container, String directory) {
      createDirectoryWithResult(container, directory);
   }

   public void deleteDirectory(String container, String directory) {
      // create complete dir path
      String fullDirPath = buildPathStartingFromBaseDir(container, directory);
      try {
         Utils.deleteRecursively(new File(fullDirPath));
      } catch (IOException ex) {
         logger.error("An error occurred removing directory %s.", fullDirPath);
         Throwables.propagate(ex);
      }
   }

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
   private static String normalize(String pathToBeNormalized) {
      if (null != pathToBeNormalized && pathToBeNormalized.contains(BACK_SLASH)) {
         if (!BACK_SLASH.equals(File.separator)) {
            return pathToBeNormalized.replace(BACK_SLASH, File.separator);
         }
      }
      return pathToBeNormalized;
   }

   private static String denormalize(String pathToDenormalize) {
      if (null != pathToDenormalize && pathToDenormalize.contains("/")) {
         if (BACK_SLASH.equals(File.separator)) {
              return pathToDenormalize.replace("/", BACK_SLASH);
         }
      }
      return pathToDenormalize;
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
      String normalizedBlobKey = denormalize(blobKey);
      // exists is no path is present in the blobkey
      if (!normalizedBlobKey.contains(File.separator))
         return;

      File file = new File(normalizedBlobKey);
      // TODO
      // "/media/data/works/java/amazon/jclouds/master/filesystem/aa/bb/cc/dd/eef6f0c8-0206-460b-8870-352e6019893c.txt"
      String parentPath = file.getParent();
      // no need to manage "/" parentPath, because "/" cannot be used as start
      // char of blobkey
      if (!isNullOrEmpty(parentPath)) {
         // remove parent directory only it's empty
         File directory = new File(buildPathStartingFromBaseDir(container, parentPath));
         String[] children = directory.list();
         if (null == children || children.length == 0) {
            if (!directory.delete()) {
               logger.debug("Could not delete %s", directory);
               return;
            }
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

   private static void populateBlobKeysInContainer(File directory, Set<String> blobNames,
         Function<String, String> function) {
      File[] children = directory.listFiles();
      for (File child : children) {
         if (child.isFile()) {
            blobNames.add(function.apply(child.getAbsolutePath()));
         } else if (child.isDirectory()) {
            populateBlobKeysInContainer(child, blobNames, function);
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

}
