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
package org.jclouds.filesystem.utils;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.jclouds.filesystem.util.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * Utility class for test
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class TestUtils {

    private static final String TARGET_RESOURCE_DIR = "." + File.separator + "target" + File.separator + "resources" + File.separator;

    /** All the files available for the tests */
    private static final Iterable<File> IMAGE_RESOURCES = ImmutableList.of(
                    new File(TARGET_RESOURCE_DIR + "image1.jpg"),
                    new File(TARGET_RESOURCE_DIR + "image2.jpg"),
                    new File(TARGET_RESOURCE_DIR + "image3.jpg"),
                    new File(TARGET_RESOURCE_DIR + "image4.jpg"));

    public static final String TARGET_BASE_DIR = "." + File.separator + "target" + File.separator + "basedir" + File.separator;

    private static final Iterator<File> IMAGE_RESOURCES_ITERATOR =
            Iterators.cycle(IMAGE_RESOURCES);

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
   public static Set<String> createBlobsInContainer(String containerName, String... blobNames) throws IOException {
        Set<String> blobNamesCreatedInContainer = Sets.newHashSet();
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
        File file = new File(TARGET_BASE_DIR, containerName);
        if (!file.mkdirs()) {
            throw new IOException("Could not mkdir: " + file);
        };
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
            for (File child : children) {
                Utils.deleteRecursively(child);
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
        File file = new File(TARGET_BASE_DIR + filePath);
        Files.createParentDirs(file);
        Files.copy(source, file);
    }


    /**
     * Returns a pointer to an image, cycling between the ones that are available
     * @return
     */
    public static File getImageForBlobPayload() {
        return IMAGE_RESOURCES_ITERATOR.next();
    }

    /** Create resources used by tests. */
    public static void createResources() throws IOException {
        File resourceDir = new File(TestUtils.TARGET_RESOURCE_DIR);
        if (resourceDir.exists()) {
            Utils.deleteRecursively(resourceDir);
        }
        if (!resourceDir.mkdir()) {
            throw new IOException("Could not create: " + TARGET_RESOURCE_DIR);
        }
        Random random = new Random();
        for (File file : IMAGE_RESOURCES) {
            byte[] buffer = new byte[random.nextInt(2 * 1024 * 1024)];
            random.nextBytes(buffer);
            Files.copy(ByteStreams.newInputStreamSupplier(buffer), file);
        }
    }
}
