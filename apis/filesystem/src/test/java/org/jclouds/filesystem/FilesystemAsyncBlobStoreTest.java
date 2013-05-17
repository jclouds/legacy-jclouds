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
package org.jclouds.filesystem;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.filesystem.util.Utils;
import org.jclouds.filesystem.utils.TestUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.PhantomPayload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.inject.CreationException;

/**
 * Test class for {@link FilesystemAsyncBlobStore} class
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Test(groups = "unit", testName = "filesystem.FilesystemAsyncBlobStoreTest", singleThreaded = true)
public class FilesystemAsyncBlobStoreTest {

    private static final String CONTAINER_NAME = "fun-blobstore-test";
    private static final String TARGET_CONTAINER_NAME = TestUtils.TARGET_BASE_DIR + CONTAINER_NAME;
    private static final String LOGGING_CONFIG_KEY = "java.util.logging.config.file";
    private static final String LOGGING_CONFIG_VALUE = "src/main/resources/logging.properties";

    private static final String PROVIDER = "filesystem";

    static {
        System.setProperty(LOGGING_CONFIG_KEY, LOGGING_CONFIG_VALUE);
    }

    private BlobStoreContext context = null;
    private BlobStore blobStore = null;

    @BeforeMethod
    protected void setUp() throws Exception {
        // create context for filesystem container
        Properties prop = new Properties();
        prop.setProperty(FilesystemConstants.PROPERTY_BASEDIR, TestUtils.TARGET_BASE_DIR);
        context = ContextBuilder.newBuilder(PROVIDER).overrides(prop).build(BlobStoreContext.class);
        // create a container in the default location
        blobStore = context.getBlobStore();
        new File(TestUtils.TARGET_BASE_DIR).mkdir();
        TestUtils.createResources();
    }

    @AfterMethod
    protected void tearDown() throws IOException {
        context.close();
        Utils.deleteRecursively(new File(TestUtils.TARGET_BASE_DIR));
    }

    /**
     * Checks if context parameters are managed in the correct way
     */
    public void testParameters() {
        // no base directory declared in properties
        try {
            Properties props = new Properties();
            context = ContextBuilder.newBuilder(PROVIDER).overrides(props).build(BlobStoreContext.class);
            fail("No error if base directory is not specified");
        } catch (CreationException e) {
        }

        // no base directory declared in properties
        try {
            Properties props = new Properties();
            props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, null);
            context = ContextBuilder.newBuilder(PROVIDER).overrides(props).build(BlobStoreContext.class);
            fail("No error if base directory is null in the option");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test of list method of the root context
     */
    public void testList_Root() throws IOException {
        PageSet<? extends StorageMetadata> containersRetrieved;
        Set<String> containersCreated = Sets.newHashSet();

        // Testing list with no containers
        containersRetrieved = blobStore.list();
        assertTrue(containersRetrieved.isEmpty(), "List operation returns a not empty set of container");

        // Testing list with some containers
        String[] containerNames = new String[]{"34343", "aaaa", "bbbbb"};
        containersCreated = Sets.newHashSet();
        for (String containerName : containerNames) {
            blobStore.createContainerInLocation(null, containerName);
            containersCreated.add(containerName);
        }

        containersRetrieved = blobStore.list();
        assertEquals(containersCreated.size(), containersRetrieved.size(), "Different numbers of container");

        for (StorageMetadata data : containersRetrieved) {
            String containerName = data.getName();
            if (!containersCreated.remove(containerName)) {
                fail("Container list contains unexpected value [" + containerName + "]");
            }
        }
        assertTrue(containersCreated.isEmpty(), "List operation doesn't return all values.");

        for (String containerName : containerNames) {
            // delete all creaded containers
            blobStore.deleteContainer(containerName);
        }
        containersRetrieved = blobStore.list();
        assertTrue(containersRetrieved.isEmpty(), "List operation returns a not empty set of container");
    }

    /**
     * Test of list method, of class FilesystemAsyncBlobStore.
     */
    public void testList_NoOptionSingleContainer() throws IOException {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        // Testing list for an empty container
        checkForContainerContent(CONTAINER_NAME, null);

        // creates blobs in first container
        Set<String> blobsExpected = TestUtils.createBlobsInContainer(CONTAINER_NAME, "bbb" + File.separator + "ccc"
                + File.separator + "ddd" + File.separator + "1234.jpg", "4rrr.jpg", "rrr" + File.separator + "sss"
                + File.separator + "788.jpg", "xdc" + File.separator + "wert.kpg");

        checkForContainerContent(CONTAINER_NAME, blobsExpected);
    }

    public void testList_NotExistingContainer() {
        // Testing list for a not existing container
        try {
            blobStore.list(CONTAINER_NAME);
            fail("Found a not existing container");
        } catch (ContainerNotFoundException e) {
            // ok if arriver here
        }
    }

    /**
     * Test of list method, of class FilesystemAsyncBlobStore.
     */
    public void testList_NoOptionDoubleContainer() throws IOException {
        final String CONTAINER_NAME2 = "container2";

        // create first container
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        // checks for empty container
        checkForContainerContent(CONTAINER_NAME, null);

        // create second container
        blobStore.createContainerInLocation(null, CONTAINER_NAME2);
        // checks for empty
        checkForContainerContent(CONTAINER_NAME2, null);

        // creates blobs in first container

        Set<String> blobNamesCreatedInContainer1 = TestUtils.createBlobsInContainer(CONTAINER_NAME, "bbb"
                + File.separator + "ccc" + File.separator + "ddd" + File.separator + "1234.jpg",
                TestUtils.createRandomBlobKey(), "rrr" + File.separator + "sss" + File.separator + "788.jpg", "xdc"
                + File.separator + "wert.kpg");

        // creates blobs in second container
        blobStore.createContainerInLocation(null, CONTAINER_NAME2);
        Set<String> blobNamesCreatedInContainer2 = TestUtils.createBlobsInContainer(CONTAINER_NAME2, "asd"
                + File.separator + "bbb" + File.separator + "ccc" + File.separator + "ddd" + File.separator + "1234.jpg",
                TestUtils.createRandomBlobKey(), "rrr" + File.separator + "sss" + File.separator + "788.jpg", "xdc"
                + File.separator + "wert.kpg");

        // test blobs in first container
        checkForContainerContent(CONTAINER_NAME, blobNamesCreatedInContainer1);
        // test blobs in second container
        checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);
    }

    public void testList_Subdirectory() throws IOException {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        // Testing list for an empty container
        checkForContainerContent(CONTAINER_NAME, null);

        // creates blobs in first container
        Set<String> blobsExpected = TestUtils.createBlobsInContainer(CONTAINER_NAME, "bbb" + File.separator + "ccc"
                + File.separator + "ddd" + File.separator + "1234.jpg", "4rrr.jpg", "rrr" + File.separator + "sss"
                + File.separator + "788.jpg", "rrr" + File.separator + "wert.kpg");

        // remove not expected values
        blobsExpected.remove("bbb" + File.separator + "ccc" + File.separator + "ddd" + File.separator + "1234.jpg");
        blobsExpected.remove("4rrr.jpg");

        checkForContainerContent(CONTAINER_NAME, "rrr", blobsExpected);
    }

    /**
     * TODO Should throws an exception?
     */
    public void testClearContainer_NotExistingContainer() {
        blobStore.clearContainer(CONTAINER_NAME);
    }

    /**
     * Integration test, because clearContainer is not redefined in
     * {@link FilesystemAsyncBlobStore} class
     */
    public void testClearContainer_NoOptions() throws IOException {
        final String CONTAINER_NAME2 = "containerToClear";

        // create containers
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        blobStore.createContainerInLocation(null, CONTAINER_NAME2);

        // creates blobs in first container
        Set<String> blobNamesCreatedInContainer1 = TestUtils.createBlobsInContainer(CONTAINER_NAME, "bbb"
                + File.separator + "ccc" + File.separator + "ddd" + File.separator + "1234.jpg",
                TestUtils.createRandomBlobKey(), "rrr" + File.separator + "sss" + File.separator + "788.jpg", "xdc"
                + File.separator + "wert.kpg");

        // creates blobs in second container
        blobStore.createContainerInLocation(null, CONTAINER_NAME2);
        Set<String> blobNamesCreatedInContainer2 = TestUtils.createBlobsInContainer(CONTAINER_NAME2, "asd"
                + File.separator + "bbb" + File.separator + "ccc" + File.separator + "ddd" + File.separator + "1234.jpg",
                TestUtils.createRandomBlobKey(), "rrr" + File.separator + "sss" + File.separator + "788.jpg", "xdc"
                + File.separator + "wert.kpg");

        // test blobs in containers
        checkForContainerContent(CONTAINER_NAME, blobNamesCreatedInContainer1);
        checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);

        // delete blobs in first container
        blobStore.clearContainer(CONTAINER_NAME);
        checkForContainerContent(CONTAINER_NAME, null);
        checkForContainerContent(CONTAINER_NAME2, blobNamesCreatedInContainer2);
        // delete blobs in second container
        blobStore.clearContainer(CONTAINER_NAME2);
        checkForContainerContent(CONTAINER_NAME2, null);
    }

    /**
     * Integration test, because countBlobs is not redefined in
     * {@link FilesystemAsyncBlobStore} class
     */
    public void testCountBlobs_NotExistingContainer() {
        try {
            blobStore.countBlobs(PROVIDER);
            fail("Magically the method was implemented... Wow!");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Integration test, because countBlobs is not redefined in
     * {@link FilesystemAsyncBlobStore} class
     */
    public void testCountBlobs_NoOptionsEmptyContainer() {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        try {
            blobStore.countBlobs(PROVIDER);
            fail("Magically the method was implemented... Wow!");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Integration test, because countBlobs is not redefined in
     * {@link FilesystemAsyncBlobStore} class
     */
    public void testCountBlobs_NoOptions() {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        try {
            blobStore.countBlobs(PROVIDER);
            fail("Magically the method was implemented... Wow!");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testRemoveBlob_SimpleBlobKey() throws IOException {
        final String BLOB_KEY = TestUtils.createRandomBlobKey(null, ".txt");
        boolean result;

        blobStore.createContainerInLocation(null, CONTAINER_NAME);

        // checks that blob doesn't exists
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertFalse(result, "Blob exists");

        // create the blob
        TestUtils.createBlobsInContainer(CONTAINER_NAME, BLOB_KEY);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertTrue(result, "Blob exists");

        // remove it
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertFalse(result, "Blob still exists");
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY, false);
    }

    public void testRemoveBlob_TwoSimpleBlobKeys() throws IOException {
        final String BLOB_KEY1 = TestUtils.createRandomBlobKey(null, null);
        final String BLOB_KEY2 = TestUtils.createRandomBlobKey(null, null);
        boolean result;

        // create the container and checks that blob doesn't exists
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertFalse(result, "Blob1 exists");
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertFalse(result, "Blob2 exists");

        // create the blob
        TestUtils.createBlobsInContainer(CONTAINER_NAME, BLOB_KEY1, BLOB_KEY2);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertTrue(result, "Blob " + BLOB_KEY1 + " doesn't exist");
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertTrue(result, "Blob " + BLOB_KEY2 + " doesn't exist");

        // remove first blob
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY1);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertFalse(result, "Blob1 still exists");
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertTrue(result, "Blob2 doesn't exist");
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY1, false);
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY2, true);
        // remove second blob
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY2);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertFalse(result, "Blob2 still exists");
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY2, false);
    }

    /**
     * Test of removeBlob method, with only one blob with a complex path as key
     */
    @Test
    public void testRemoveBlob_ComplexBlobKey() throws IOException {
        final String BLOB_KEY = TestUtils.createRandomBlobKey("aa/bb/cc/dd/", null);
        boolean result;

        // checks that blob doesn't exists
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertFalse(result, "Blob exists");
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY, false);

        // create the blob
        TestUtils.createBlobsInContainer(CONTAINER_NAME, BLOB_KEY);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertTrue(result, "Blob doesn't exist");

        // remove it
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY);
        assertFalse(result, "Blob still exists");
        // file removed
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY, false);
        // also the entire directory structure was removed
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + "/aa", false);
    }

    /**
     * Test of removeBlob method, with two blobs with a complex path as key and
     * when first blob is removed, not all of its key's path is removed, because
     * it is shared with the second blob's key
     */
    @Test
    public void testRemoveBlob_TwoComplexBlobKeys() throws IOException {
        final String BLOB_KEY1 = TestUtils.createRandomBlobKey("aa/bb/cc/dd/", null);
        final String BLOB_KEY2 = TestUtils.createRandomBlobKey("aa/bb/ee/ff/", null);
        boolean result;

        blobStore.createContainerInLocation(null, CONTAINER_NAME);

        // checks that blob doesn't exist
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertFalse(result, "Blob1 exists");
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertFalse(result, "Blob2 exists");

        // create the blobs
        TestUtils.createBlobsInContainer(CONTAINER_NAME, BLOB_KEY1, BLOB_KEY2);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertTrue(result, "Blob " + BLOB_KEY1 + " doesn't exist");
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertTrue(result, "Blob " + BLOB_KEY2 + " doesn't exist");

        // remove first blob
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY1);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY1);
        assertFalse(result, "Blob still exists");
        // first file deleted, not the second
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY1, false);
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY2, true);
        // only partial directory structure was removed, because it shares a path
        // with the second blob created
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + "/aa/bb/cc/dd", false);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + "/aa/bb", true);
        // remove second blob
        blobStore.removeBlob(CONTAINER_NAME, BLOB_KEY2);
        result = blobStore.blobExists(CONTAINER_NAME, BLOB_KEY2);
        assertFalse(result, "Blob still exists");
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + BLOB_KEY2, false);
        // now all the directory structure is empty
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + "/aa", false);
    }

    /**
     * Test of containerExists method, of class FilesystemAsyncBlobStore.
     */
    public void testContainerExists() throws IOException {
        boolean result;

        result = blobStore.containerExists(CONTAINER_NAME);
        assertFalse(result, "Container exists");

        // create container
        TestUtils.createContainerAsDirectory(CONTAINER_NAME);

        result = blobStore.containerExists(CONTAINER_NAME);
        assertTrue(result, "Container doesn't exist");
    }

    /**
     * Test of createContainerInLocation method, of class
     * FilesystemAsyncBlobStore.
     */
    public void testCreateContainerInLocation() throws IOException {
        final String CONTAINER_NAME2 = "funambol-test-2";
        final String TARGET_CONTAINER_NAME2 = TestUtils.TARGET_BASE_DIR + CONTAINER_NAME2;

        boolean result;

        result = blobStore.containerExists(CONTAINER_NAME);
        assertFalse(result, "Container exists");
        result = blobStore.createContainerInLocation(null, CONTAINER_NAME);
        assertTrue(result, "Container not created");
        result = blobStore.containerExists(CONTAINER_NAME);
        assertTrue(result, "Container doesn't exist");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);

        result = blobStore.containerExists(CONTAINER_NAME2);
        assertFalse(result, "Container exists");
        result = blobStore.createContainerInLocation(null, CONTAINER_NAME2);
        assertTrue(result, "Container not created");
        result = blobStore.containerExists(CONTAINER_NAME2);
        assertTrue(result, "Container doesn't exist");
        TestUtils.directoryExists(TestUtils.TARGET_BASE_DIR + CONTAINER_NAME2, true);
    }

    /**
     * Test of putBlob method, of class FilesystemAsyncBlobStore. with a simple
     * filename - no path in the filename, eg filename.jpg
     */
    public void testPutBlobSimpleName() {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("putBlob-", ".jpg"));
    }

    /**
     * Test of putBlob method with a complex key, with path in the filename, eg
     * picture/filename.jpg
     */
    public void testPutBlobComplexName1() {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("picture/putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("video/putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("video/putBlob-", ".jpg"));
    }

    /**
     * Test of putBlob method with a complex key, with path in the filename, eg
     * picture/filename.jpg
     */
    public void testPutBlobComplexName2() {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("aa/bb/cc/dd/ee/putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("aa/bb/cc/dd/ee/putBlob-", ".jpg"));
        putBlobAndCheckIt(TestUtils.createRandomBlobKey("putBlob-", ".jpg"));
    }

    /**
     * Test of blobExists method, of class FilesystemAsyncBlobStore.
     */
    public void testBlobExists() throws IOException {
        boolean result;
        String blobKey;

        // when location doesn't exists
        blobKey = TestUtils.createRandomBlobKey();
        try {
            blobStore.blobExists(CONTAINER_NAME, blobKey);
            fail();
        } catch (ContainerNotFoundException cnfe) {
            // expected
        }

        // when location exists
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        result = blobStore.blobExists(CONTAINER_NAME, blobKey);
        assertFalse(result, "Blob exists");

        // create blob
        TestUtils.createBlobAsFile(CONTAINER_NAME, blobKey, TestUtils.getImageForBlobPayload());
        result = blobStore.blobExists(CONTAINER_NAME, blobKey);
        assertTrue(result, "Blob doesn't exist");

        // complex path test
        blobKey = TestUtils.createRandomBlobKey("ss/asdas/", "");
        result = blobStore.blobExists(CONTAINER_NAME, blobKey);
        assertFalse(result, "Blob exists");
        TestUtils.createBlobAsFile(CONTAINER_NAME, blobKey, TestUtils.getImageForBlobPayload());
        result = blobStore.blobExists(CONTAINER_NAME, blobKey);
        assertTrue(result, "Blob doesn't exist");
    }

    public void testGetBlob_NotExistingContainer() {
        try {
            blobStore.getBlob(CONTAINER_NAME, TestUtils.createRandomBlobKey(), null);
            fail("Retrieve must fail, container does not exist.");
        } catch (ContainerNotFoundException e) {
            // correct if arrive here
        }
    }

    /**
     * Test of getBlob method, of class FilesystemAsyncBlobStore.
     */
    public void testGetBlob() throws IOException {
        String blobKey = TestUtils.createRandomBlobKey();
        GetOptions options = null;
        Blob resultBlob;

        blobStore.createContainerInLocation(null, CONTAINER_NAME);

        resultBlob = blobStore.getBlob(CONTAINER_NAME, blobKey, options);
        assertNull(resultBlob, "Blob exists");

        // create blob
        TestUtils.createBlobsInContainer(CONTAINER_NAME, blobKey);

        resultBlob = blobStore.getBlob(CONTAINER_NAME, blobKey, options);

        assertNotNull(resultBlob, "Blob exists");
        // checks file content
        InputSupplier<FileInputStream> expectedFile =
                Files.newInputStreamSupplier(new File(
                TARGET_CONTAINER_NAME, blobKey));
        assertTrue(ByteStreams.equal(expectedFile, resultBlob.getPayload()),
                "Blob payload differs from file content");
        // metadata are verified in the test for blobMetadata, so no need to
        // perform a complete test here
        assertNotNull(resultBlob.getMetadata(), "Metadata null");
        MutableBlobMetadata metadata = resultBlob.getMetadata();
        assertEquals(blobKey, metadata.getName(), "Wrong blob metadata");
    }

    public void testBlobMetadata_withDefaultMetadata() throws IOException {
        String BLOB_KEY = TestUtils.createRandomBlobKey(null, null);
        // create the blob
        TestUtils.createBlobsInContainer(CONTAINER_NAME, BLOB_KEY);

        BlobMetadata metadata = blobStore.blobMetadata(CONTAINER_NAME, BLOB_KEY);
        assertNotNull(metadata, "Metadata null");

        assertEquals(metadata.getName(), BLOB_KEY, "Wrong blob name");
        assertEquals(metadata.getType(), StorageType.BLOB, "Wrong blob type");
        assertEquals(metadata.getContentMetadata().getContentType(), "application/unknown", "Wrong blob content-type");
        assertEquals(base16().lowerCase().encode(metadata.getContentMetadata().getContentMD5()), metadata.getETag(),
                "Wrong blob MD5");
        assertEquals(metadata.getLocation(), null, "Wrong blob location");
        assertEquals(metadata.getProviderId(), null, "Wrong blob provider id");
        assertEquals(metadata.getUri(), null, "Wrong blob URI");
        assertNotNull(metadata.getUserMetadata(), "No blob UserMetadata");
        assertEquals(metadata.getUserMetadata().size(), 0, "Wrong blob UserMetadata");
        // metadata.getLastModified()
        File file = new File(TARGET_CONTAINER_NAME, BLOB_KEY);
        assertEquals(metadata.getContentMetadata().getContentLength(), Long.valueOf(file.length()), "Wrong blob size");
    }

    public void testDeleteContainer_NotExistingContainer() {
        try {
            blobStore.deleteContainer(CONTAINER_NAME);
            fail("No error when container doesn't exist");
        } catch (Exception e) {
        }
    }

    public void testDeleteContainer_EmptyContanier() {
        boolean result;
        blobStore.createContainerInLocation(null, CONTAINER_NAME);

        result = blobStore.containerExists(CONTAINER_NAME);
        assertTrue(result, "Container doesn't exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);

        // delete container
        blobStore.deleteContainer(CONTAINER_NAME);
        result = blobStore.containerExists(CONTAINER_NAME);
        assertFalse(result, "Container still exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
    }

    public void testDeleteContainer() throws IOException {
        boolean result;
        String CONTAINER_NAME2 = "container-to-delete";
        String TARGET_CONTAINER_NAME2 = TestUtils.TARGET_BASE_DIR + CONTAINER_NAME2;
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        blobStore.createContainerInLocation(null, CONTAINER_NAME2);

        result = blobStore.containerExists(CONTAINER_NAME);
        assertTrue(result, "Container [" + CONTAINER_NAME + "] doesn't exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);
        result = blobStore.containerExists(CONTAINER_NAME2);
        assertTrue(result, "Container [" + CONTAINER_NAME2 + "] doesn't exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME2, true);

        // create blobs inside container
        TestUtils.createBlobsInContainer(CONTAINER_NAME, TestUtils.createRandomBlobKey("testutils-", null),
                TestUtils.createRandomBlobKey("testutils-", null),
                TestUtils.createRandomBlobKey("ab123s" + File.separator + "testutils-", null));
        TestUtils.createBlobsInContainer(CONTAINER_NAME, TestUtils.createRandomBlobKey("testutils-", null),
                TestUtils.createRandomBlobKey("testutils-", null),
                TestUtils.createRandomBlobKey("asda123s" + File.separator + "testutils-", null),
                TestUtils.createRandomBlobKey("123-_3s" + File.separator + "testutils-", null));

        // delete first container
        blobStore.deleteContainer(CONTAINER_NAME);
        result = blobStore.containerExists(CONTAINER_NAME);
        assertFalse(result, "Container [" + CONTAINER_NAME + "] still exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        result = blobStore.containerExists(CONTAINER_NAME2);
        assertTrue(result, "Container [" + CONTAINER_NAME2 + "] still exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME2, true);
        // delete second container
        blobStore.deleteContainer(CONTAINER_NAME2);
        result = blobStore.containerExists(CONTAINER_NAME2);
        assertFalse(result, "Container [" + CONTAINER_NAME2 + "] still exists");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME2, false);
    }

    @Test
    public void testInvalidContainerName() {
	String containerName = "file" + File.separator + "system";
        try {
            blobStore.createContainerInLocation(null, containerName);
            fail("Wrong container name not recognized");
        } catch (IllegalArgumentException e) {
        }
        try {
            blobStore.containerExists(containerName);
            fail("Wrong container name not recognized");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testRanges() throws IOException {
        blobStore.createContainerInLocation(null, CONTAINER_NAME);
        String input = "abcdefgh";
        Payload payload;
        Blob blob = blobStore.blobBuilder("test").payload(new StringPayload(input)).build();
        blobStore.putBlob(CONTAINER_NAME, blob);

        GetOptions getOptionsRangeStartAt = new GetOptions();
        getOptionsRangeStartAt.startAt(1);
        Blob blobRangeStartAt = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsRangeStartAt);
        payload = blobRangeStartAt.getPayload();
        try {
            assertEquals(input.substring(1), Strings2.toString(payload));
        } finally {
            Closeables.closeQuietly(payload);
        }

        GetOptions getOptionsRangeTail = new GetOptions();
        getOptionsRangeTail.tail(3);
        Blob blobRangeTail = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsRangeTail);
        payload = blobRangeTail.getPayload();
        try {
            assertEquals(input.substring(5), Strings2.toString(payload));
        } finally {
            Closeables.closeQuietly(payload);
        }

        GetOptions getOptionsFragment = new GetOptions();
        getOptionsFragment.range(4, 6);
        Blob blobFragment = blobStore.getBlob(CONTAINER_NAME, blob.getMetadata().getName(), getOptionsFragment);
        payload = blobFragment.getPayload();
        try {
            assertEquals(input.substring(4, 7), Strings2.toString(payload));
        } finally {
            Closeables.closeQuietly(payload);
        }
    }

    /** Test that BlobRequestSigner creates expected URIs.  */
    public void testBlobRequestSigner() throws Exception {
        String containerName = "container";
        String blobName = "blob";
        URI endPoint = new URI("http", "localhost",
                String.format("/transient/%s/%s", containerName, blobName),
                /*fragment=*/ null);
        BlobRequestSigner signer = context.getSigner();
        HttpRequest request;
        HttpRequest expected;

        request = signer.signGetBlob(containerName, blobName);
        expected = HttpRequest.builder()
                .method("GET")
                .endpoint(endPoint)
                .headers(request.getHeaders())
                .build();
        assertEquals(expected, request);

        request = signer.signRemoveBlob(containerName, blobName);
        expected = HttpRequest.builder()
                .method("DELETE")
                .endpoint(endPoint)
                .headers(request.getHeaders())
                .build();
        assertEquals(expected, request);

        Blob blob = blobStore.blobBuilder(blobName).forSigning().build();
        request = signer.signPutBlob(containerName, blob);
        expected = HttpRequest.builder()
                .method("PUT")
                .endpoint(endPoint)
                .headers(request.getHeaders())
                .payload(new PhantomPayload())
                .build();
        assertEquals(expected, request);
    }

    // public void testInvalidBlobKey() {
    // try {
    // blobStore.newBlob(File.separator + "testwrongblobkey");
    // fail("Wrong blob key not recognized");
    // } catch (IllegalArgumentException e) {}
    //
    // try {
    // blobStore.newBlob("testwrongblobkey" + File.separator);
    // fail("Wrong blob key not recognized");
    // } catch (IllegalArgumentException e) {}
    // }

    // ---------------------------------------------------------- Private Methods

    /**
     * Creates a {@link Blob} object filled with data from a file
     *
     * @param keyName
     * @param fileContent
     * @return
     */
    private Blob createBlob(String keyName, File filePayload) {
        return blobStore.blobBuilder(keyName).payload(filePayload).build();
    }

    /**
     * Tests if container contains only the expected blobs
     *
     * @param containerName
     * @param expectedBlobKeys
     */
    private void checkForContainerContent(final String containerName, Set<String> expectedBlobKeys) {
        checkForContainerContent(containerName, null, expectedBlobKeys);
    }

    private void checkForContainerContent(final String containerName, String inDirectory, Set<String> expectedBlobKeys) {
        ListContainerOptions options = ListContainerOptions.Builder.recursive();
        if (null != inDirectory && !"".equals(inDirectory))
            options.inDirectory(inDirectory);

        PageSet<? extends StorageMetadata> blobsRetrieved = blobStore.list(containerName, options);

        // nothing expected
        if (null == expectedBlobKeys || 0 == expectedBlobKeys.size()) {
            assertTrue(blobsRetrieved.isEmpty(), "Wrong blob number retrieved in the container [" + containerName + "]");
            return;
        }

        // copies values
        Set<String> expectedBlobKeysCopy = Sets.newHashSet();
        for (String value : expectedBlobKeys) {
            expectedBlobKeysCopy.add(value);
        }
        assertEquals(blobsRetrieved.size(), expectedBlobKeysCopy.size(),
                "Wrong blob number retrieved in the container [" + containerName + "]");
        for (StorageMetadata data : blobsRetrieved) {
            String blobName = data.getName();
            if (!expectedBlobKeysCopy.remove(blobName)) {
                fail("List for container [" + containerName + "] contains unexpected value [" + blobName + "]");
            }
        }
        assertTrue(expectedBlobKeysCopy.isEmpty(), "List operation for container [" + containerName
                + "] doesn't return all values.");
    }

    /**
     * Create a blob with putBlob method
     */
    private void putBlobAndCheckIt(String blobKey) {
        Blob blob;

        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blobKey, false);

        // create the blob
        blob = createBlob(blobKey, TestUtils.getImageForBlobPayload());
        String eTag = blobStore.putBlob(CONTAINER_NAME, blob);
        assertNotNull(eTag, "putBlob result null");
        assertNotSame(eTag, "", "putBlob result empty");

        // checks if the blob exists
        TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blobKey, true);
    }

}
