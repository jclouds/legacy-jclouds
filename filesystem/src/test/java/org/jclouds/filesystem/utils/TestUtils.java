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

package org.jclouds.filesystem.utils;

import java.util.Arrays;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import static org.testng.Assert.*;

/**
 * Utility class for test
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class TestUtils {

    private static final String TARGET_RESOURCE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    /** All the files available for the tests */
    private static String[] imageResource = new String[]{
        TARGET_RESOURCE_DIR + "image1.jpg",
        TARGET_RESOURCE_DIR + "image2.jpg",
        TARGET_RESOURCE_DIR + "image3.jpg",
        TARGET_RESOURCE_DIR + "image4.jpg"
    };
    private static int imageResourceIndex = 0;

    public static final String TARGET_BASE_DIR = "." + File.separator + "target" + File.separator + "basedir" + File.separator;


    /**
     * Generate a random blob key simple name (with no path in the key)
     * @return 
     */
    public static String createRandomBlobKey() {
        return createRandomBlobKey("", "");
    }

    /**
     * Generate a random blob key simple name (with no path in the key)
     * @param prefix a prefix to the id, default "testkey-"
     * @param extension a extension for the blob key, default ".jpg"
     * @return
     */
    public static String createRandomBlobKey(String prefix, String extension) {
        String okPrefix = (null != prefix && !"".equals(prefix)) ? prefix : "testkey-";
        String okExtension = (null != extension && !"".equals(extension)) ? extension : ".jpg";
        return okPrefix + UUID.randomUUID().toString() + okExtension;
    }

    /**
     * Creates blobs in container
     * @param containerName
     * @param blobNames
     * @return a Set with all blobs created
     * @throws IOException
     */
    public static Set<String> createBlobsInContainer(String containerName, String[] blobNames) throws IOException {
        Set<String> blobNamesCreatedInContainer = new HashSet<String>();
        for (String blobName : blobNames) {
            createBlobAsFile(containerName, blobName, getImageForBlobPayload());
            blobNamesCreatedInContainer.add(blobName);
        }
        return blobNamesCreatedInContainer;
    }

    /**
     * Creates a container object creating a directory
     * @param containerName
     * @throws IOException
     */
    public static void createContainerAsDirectory(String containerName) throws IOException {
        FileUtils.forceMkdir(new File(TARGET_BASE_DIR + containerName));
    }

    /**
     *
     * @param directoryFullPath the directory path
     * @return
     */
    public static boolean directoryExists(String directoryFullPath) {
        File file = new File(directoryFullPath);
        boolean exists = file.exists() || file.isDirectory();

        return exists;
    }

    /**
     * 
     * @param directoryFullPath
     * @param checkResult
     * @param expectedResult
     * @return
     */
    public static boolean directoryExists(String directoryFullPath, boolean expectedResult) {
        boolean exists = directoryExists(directoryFullPath);

        if (expectedResult) {
            assertTrue(exists, "Directory " + directoryFullPath + " doens't exists");
        } else {
            assertFalse(exists, "Directory " + directoryFullPath + " still exists");
        }
        return exists;
    }


    public static boolean fileExists(String fileFullName) {
        File file = new File(fileFullName);
        boolean exists = file.exists() || file.isFile();
        return exists;
    }

    /**
     *
     * @param fileFullName
     * @param checkResult
     * @param expectedResult
     * @return
     */
    public static boolean fileExists(String fileFullName, boolean expectedResult) {
        boolean exists = fileExists(fileFullName);

        if (expectedResult) {
            assertTrue(exists, "File " + fileFullName + " doens't exists");
        } else {
            assertFalse(exists, "File " + fileFullName + " still exists");
        }
        return exists;
    }


    /**
     * Empty a directory
     * 
     * @param directoryName
     * @throws IOException
     */
    public static void cleanDirectoryContent(String directoryName) throws IOException {
        File parentDirectory = new File(directoryName);
        File[] children = parentDirectory.listFiles();
        if (null != children) {
            for(File child:children) {
                FileUtils.forceDelete(child);
            }
        }
    }

    /**
     * Create a blob object from a given file
     * @param source
     * @param containerName
     * @param blobKey
     * @throws IOException
     */
    public static void createBlobAsFile(String containerName, String blobKey, File source) throws IOException {
        String filePath;
        if (blobKey.startsWith("\\"))
            filePath = containerName  + blobKey;
        else
            filePath = containerName + File.separator + blobKey;
        FileUtils.copyFile(source, new File(TARGET_BASE_DIR + filePath));
    }


    /**
     * Returns a pointer to an image, cycling between the ones that are available
     * @return
     */
    public static File getImageForBlobPayload() {
        String fileName = imageResource[imageResourceIndex++];
        if (imageResourceIndex >= imageResource.length) imageResourceIndex = 0;
        return new File(fileName);
    }


  /**
   * Compare two input stream
   *
   * @param input1 the first stream
   * @param input2 the second stream
   * @return true if the streams contain the same content, or false otherwise
   * @throws IOException
   * @throws IllegalArgumentException if the stream is null
   */
  public static boolean isSame(InputStream input1, InputStream input2 ) throws IOException {
      boolean error = false;
      try {
          byte[] buffer1 = new byte[1024];
          byte[] buffer2 = new byte[1024];
          try {
              int numRead1 = 0;
              int numRead2 = 0;
              while (true) {
                  numRead1 = input1.read(buffer1);
                  numRead2 = input2.read(buffer2);
                  if (numRead1 > -1) {
                      if (numRead2 != numRead1) return false;
                      // Otherwise same number of bytes read
                      if (!Arrays.equals(buffer1, buffer2)) return false;
                      // Otherwise same bytes read, so continue ...
                  } else {
                      // Nothing more in stream 1 ...
                      return numRead2 < 0;
                  }
              }
          } finally {
              input1.close();
          }
      } catch (IOException e) {
          error = true; // this error should be thrown, even if there is an error closing stream 2
          throw e;
      } catch (RuntimeException e) {
          error = true; // this error should be thrown, even if there is an error closing stream 2
          throw e;
      } finally {
          try {
              input2.close();
          } catch (IOException e) {
              if (!error) throw e;
          }
      }
  }
}
