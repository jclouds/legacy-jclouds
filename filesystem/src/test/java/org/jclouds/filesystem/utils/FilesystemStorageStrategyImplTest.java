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
package org.jclouds.filesystem.utils;

import java.util.List;
import java.util.Set;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.Blob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.io.payloads.FilePayload;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Test class for {@link FilesystemStorageStrategyImpl } class
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Test(groups = "unit", testName = "filesystem.FilesystemBlobUtilsTest", sequential = true)
public class FilesystemStorageStrategyImplTest {
    private static final String CONTAINER_NAME          = "funambol-test";
    private static final String TARGET_CONTAINER_NAME   = TestUtils.TARGET_BASE_DIR + CONTAINER_NAME;

    private static final String LOGGING_CONFIG_KEY = "java.util.logging.config.file";
    private static final String LOGGING_CONFIG_VALUE = "src/main/resources/logging.properties";

    static  {
        System.setProperty(LOGGING_CONFIG_KEY,
                           LOGGING_CONFIG_VALUE);
    }

    private FilesystemStorageStrategy storageStrategy;

    @BeforeMethod
    protected void setUp() throws Exception {
        storageStrategy = new FilesystemStorageStrategyImpl(
                new Blob.Factory() {
                    @Override
                    public Blob create(MutableBlobMetadata metadata) {
                        return new BlobImpl(metadata != null ? metadata : new MutableBlobMetadataImpl());
                    }
                },
                TestUtils.TARGET_BASE_DIR);
        TestUtils.cleanDirectoryContent(TestUtils.TARGET_BASE_DIR);
    }


    @AfterMethod
    protected void tearDown() throws IOException {
        TestUtils.cleanDirectoryContent(TestUtils.TARGET_BASE_DIR);
    }


    public void testCreateDirectory() {
        storageStrategy.createDirectory(CONTAINER_NAME, null);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);

        storageStrategy.createDirectory(CONTAINER_NAME, "subdir");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "subdir", true);

        storageStrategy.createDirectory(CONTAINER_NAME, "subdir1" + File.separator);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "subdir1", true);

        storageStrategy.createDirectory(CONTAINER_NAME, File.separator + "subdir2");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "subdir2", true);

        storageStrategy.createDirectory(CONTAINER_NAME, "subdir3" + File.separator + "subdir4");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "subdir2", true);
    }

    public void testCreateDirectory_DirectoryAlreadyExists() {
        storageStrategy.createDirectory(CONTAINER_NAME, null);
        storageStrategy.createDirectory(CONTAINER_NAME, null);
    }

    public void testCreateDirectory_WrongDirectoryName() {
        try {
            storageStrategy.createDirectory(CONTAINER_NAME, "$%&!'`\\");
            fail("No exception throwed");
        } catch(Exception e) {
        }
    }


    public void testCreateContainer() {
        boolean result;

        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        result = storageStrategy.createContainer(CONTAINER_NAME);
        assertTrue(result, "Container not created");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);
    }

    public void testCreateContainer_ContainerAlreadyExists() {
        boolean result;

        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        result = storageStrategy.createContainer(CONTAINER_NAME);
        assertTrue(result, "Container not created");
        result = storageStrategy.createContainer(CONTAINER_NAME);
        assertFalse(result, "Container not created");
    }


    public void testDeleteDirectory() throws IOException {
        TestUtils.createContainerAsDirectory(CONTAINER_NAME);
        TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("lev1/lev2/lev3/", ".txt"),
                    TestUtils.createRandomBlobKey("lev1/lev2/lev4/", ".jpg")
                }
        );

        //delete directory in different ways
        storageStrategy.deleteDirectory(CONTAINER_NAME, "lev1/lev2/lev4");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "lev1/lev2/lev4", false);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "lev1/lev2", true);

        storageStrategy.deleteDirectory(CONTAINER_NAME, "lev1/lev2/lev3/");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "lev1/lev2/lev3", false);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "lev1/lev2", true);

        storageStrategy.deleteDirectory(CONTAINER_NAME, "/lev1");
        TestUtils.directoryExists(TARGET_CONTAINER_NAME + File.separator + "lev1", false);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);

        //delete the directory and all the files inside
        TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("lev1/lev2/lev3/", ".txt"),
                    TestUtils.createRandomBlobKey("lev1/lev2/lev4/", ".jpg")
                }
        );
        storageStrategy.deleteDirectory(CONTAINER_NAME, null);
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
    }

    public void testDeleteDirectory_ErrorWhenNotExists(){
        try {
            storageStrategy.deleteDirectory(CONTAINER_NAME, null);
            fail("No exception throwed");
        } catch(Exception e) {
        }
    }

    
    public void testDirectoryExists() throws IOException {
        final String SUBDIRECTORY_NAME = "ad" + File.separator + "sda" + File.separator + "asd";
        boolean result;

        result = storageStrategy.directoryExists(CONTAINER_NAME, null);
        assertFalse(result, "Directory exist");

        //create the container
        TestUtils.createContainerAsDirectory(CONTAINER_NAME);
        //check if exists
        result = storageStrategy.directoryExists(CONTAINER_NAME, null);
        assertTrue(result, "Directory doesn't exist");
        result = storageStrategy.directoryExists(CONTAINER_NAME + File.separator, null);
        assertTrue(result, "Directory doesn't exist");


        result = storageStrategy.directoryExists(CONTAINER_NAME, SUBDIRECTORY_NAME);
        assertFalse(result, "Directory exist");

        //create subdirs inside the container
        TestUtils.createContainerAsDirectory(CONTAINER_NAME + File.separator + SUBDIRECTORY_NAME);
        //check if exists
        result = storageStrategy.directoryExists(CONTAINER_NAME, SUBDIRECTORY_NAME);
        assertTrue(result, "Directory doesn't exist");
        result = storageStrategy.directoryExists(CONTAINER_NAME, File.separator + SUBDIRECTORY_NAME);
        assertTrue(result, "Directory doesn't exist");
        result = storageStrategy.directoryExists(CONTAINER_NAME, SUBDIRECTORY_NAME + File.separator);
        assertTrue(result, "Directory doesn't exist");
        result = storageStrategy.directoryExists(CONTAINER_NAME + File.separator, File.separator + SUBDIRECTORY_NAME);
        assertTrue(result, "Directory doesn't exist");

    }


    public void testClearContainer() throws IOException{
        storageStrategy.createContainer(CONTAINER_NAME);
        Set<String> blobs = TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("clean_container-", ".jpg"),
                    TestUtils.createRandomBlobKey("bf/sd/as/clean_container-", ".jpg")}
                );
        //test if file exits
        for(String blob:blobs) {
            TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blob, true);
        }

        //clear the container
        storageStrategy.clearContainer(CONTAINER_NAME);
        //test if container still exits
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);
        //test if file was cleared
        for(String blob:blobs) {
            TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blob, false);
        }
    }

    
    public void testClearContainer_NotExistingContainer() throws IOException{
        //test if container still exits
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        //clear the container
        storageStrategy.clearContainer(CONTAINER_NAME);
        //test if container still exits
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
    }


    public void testClearContainerAndThenDeleteContainer() throws IOException{
        storageStrategy.createContainer(CONTAINER_NAME);
        Set<String> blobs = TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("clean_container-", ".jpg"),
                    TestUtils.createRandomBlobKey("bf/sd/as/clean_container-", ".jpg")}
                );
        //test if file exits
        for(String blob:blobs) {
            TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blob, true);
        }

        //clear the container
        storageStrategy.clearContainer(CONTAINER_NAME);
        //test if container still exits
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, true);
        //test if file was cleared
        for(String blob:blobs) {
            TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + blob, false);
        }

        //delete the container
        storageStrategy.deleteContainer(CONTAINER_NAME);
        //test if container still exits
        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        assertFalse(storageStrategy.containerExists(CONTAINER_NAME), "Container still exists");
    }


    public void testDeleteContainer() throws IOException {
        final String BLOB_KEY1 = "blobName.jpg";
        final String BLOB_KEY2 = "aa/bb/cc/dd/ee/ff/23/blobName.jpg";
        boolean result;

        result = storageStrategy.createContainer(CONTAINER_NAME);

        //put data inside the container
        TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[] {BLOB_KEY1, BLOB_KEY2}
        );

        storageStrategy.deleteContainer(CONTAINER_NAME);
        assertTrue(result, "Cannot delete container");
        TestUtils.directoryExists(CONTAINER_NAME, false);
    }

    public void testDeleteContainer_EmptyContainer() {
        boolean result;

        result = storageStrategy.createContainer(CONTAINER_NAME);
        assertTrue(result, "Cannot create container");

        storageStrategy.deleteContainer(CONTAINER_NAME);
        TestUtils.directoryExists(CONTAINER_NAME, false);
    }

    public void testDeleteContainer_ErrorWhenNotExists() {
        try {
            storageStrategy.deleteContainer(CONTAINER_NAME);
            fail("Exception not throwed");
        } catch (Exception e) {
        }
    }


    public void testGetAllContainerNames() {
        Iterable<String> resultList;

        //no container
        resultList = storageStrategy.getAllContainerNames();
        assertNotNull(resultList, "Result is null");
        assertFalse(resultList.iterator().hasNext(), "Containers detected");

        //create containers
        storageStrategy.createContainer(CONTAINER_NAME + "1");
        storageStrategy.createContainer(CONTAINER_NAME + "2");
        storageStrategy.createContainer(CONTAINER_NAME + "3");

        List<String> containers = new ArrayList<String>();
        resultList = storageStrategy.getAllContainerNames();
        Iterator<String> containersIterator = resultList.iterator();
        while(containersIterator.hasNext()){
            containers.add(containersIterator.next());
        }
        assertEquals(containers.size(), 3, "Different containers number");
        assertTrue(containers.contains(CONTAINER_NAME + "1"), "Containers doesn't exist");
        assertTrue(containers.contains(CONTAINER_NAME + "2"), "Containers doesn't exist");
        assertTrue(containers.contains(CONTAINER_NAME + "3"), "Containers doesn't exist");
    }


    public void testContainerExists(){
        boolean result;

        TestUtils.directoryExists(TARGET_CONTAINER_NAME, false);
        result = storageStrategy.containerExists(CONTAINER_NAME);
        assertFalse(result, "Container exists");
        storageStrategy.createContainer(CONTAINER_NAME);
        result = storageStrategy.containerExists(CONTAINER_NAME);
        assertTrue(result, "Container exists");
    }


    public void testNewBlob() {
        String blobKey;
        Blob newBlob;

        blobKey = TestUtils.createRandomBlobKey("blobtest-", ".txt");
        newBlob = storageStrategy.newBlob(blobKey);
        assertNotNull(newBlob, "Created blob was null");
        assertNotNull(newBlob.getMetadata(), "Created blob metadata were null");
        assertEquals(newBlob.getMetadata().getName(), blobKey, "Created blob name is different");

        blobKey = TestUtils.createRandomBlobKey("blobtest-", "");
        newBlob = storageStrategy.newBlob(blobKey);
        assertEquals(newBlob.getMetadata().getName(), blobKey, "Created blob name is different");

        blobKey = TestUtils.createRandomBlobKey("asd/asd/asdasd/afadsf-", "");
        newBlob = storageStrategy.newBlob(blobKey);
        assertEquals(newBlob.getMetadata().getName(), blobKey, "Created blob name is different");
    }


    public void testWritePayloadOnFile() throws IOException {
        String blobKey;
        File sourceFile;
        FilePayload filePayload;

        blobKey = TestUtils.createRandomBlobKey("writePayload-", ".img");
        sourceFile = TestUtils.getImageForBlobPayload();
        filePayload = new FilePayload(sourceFile);
        //write files
        storageStrategy.writePayloadOnFile(CONTAINER_NAME, blobKey, filePayload);
        //verify that the files is equal
        String blobFullPath = TARGET_CONTAINER_NAME + File.separator + blobKey;
        InputStream expectedInput = new FileInputStream(sourceFile);
        InputStream currentInput = new FileInputStream(blobFullPath);
        assertTrue(TestUtils.isSame(expectedInput, currentInput), "Files aren't equals");
    }

    public void testWritePayloadOnFile_SourceFileDoesntExist() {
        File sourceFile = new File("asdfkjsadkfjasdlfasdflk.asdfasdfas");
        try {
            FilePayload filePayload = new FilePayload(sourceFile);
            fail("Exception not throwed");
        } catch (Exception ex) {
        }
    }


    public void testGetFileForBlobKey() {
        String blobKey;
        File fileForPayload;
        String fullPath = (new File(TARGET_CONTAINER_NAME).getAbsolutePath()) + File.separator;

        blobKey = TestUtils.createRandomBlobKey("getFileForBlobKey-", ".img");
        fileForPayload = storageStrategy.getFileForBlobKey(CONTAINER_NAME, blobKey);
        assertNotNull(fileForPayload, "Result File object is null");
        assertEquals(fileForPayload.getAbsolutePath(), fullPath + blobKey, "Wrong file path");

        blobKey = TestUtils.createRandomBlobKey("asd/vmad/andsnf/getFileForBlobKey-", ".img");
        fileForPayload = storageStrategy.getFileForBlobKey(CONTAINER_NAME, blobKey);
        assertEquals(fileForPayload.getAbsolutePath(), fullPath + blobKey, "Wrong file path");
    }


    public void testBlobExists() throws IOException {
        String[] sourceBlobKeys = new String[]{
            TestUtils.createRandomBlobKey("blobExists-", ".jpg"),
            TestUtils.createRandomBlobKey("blobExists-", ".jpg"),
            TestUtils.createRandomBlobKey("afasd" + File.separator + "asdma" + File.separator + "blobExists-", ".jpg")
        };

        for(String blobKey:sourceBlobKeys) {
            assertFalse(storageStrategy.blobExists(CONTAINER_NAME, blobKey), "Blob " + blobKey + " exists");
        }
        TestUtils.createBlobsInContainer(CONTAINER_NAME, sourceBlobKeys);
        for(String blobKey:sourceBlobKeys) {
            assertTrue(storageStrategy.blobExists(CONTAINER_NAME, blobKey), "Blob " + blobKey + " doesn't exist");
        }
    }


    public void testRemoveBlob() throws IOException {
        storageStrategy.createContainer(CONTAINER_NAME);
        Set<String> blobKeys = TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("removeBlob-", ".jpg"),
                    TestUtils.createRandomBlobKey("removeBlob-", ".jpg"),
                    TestUtils.createRandomBlobKey("346" + File.separator + "g3sx2" + File.separator + "removeBlob-", ".jpg"),
                    TestUtils.createRandomBlobKey("346" + File.separator + "g3sx2" + File.separator + "removeBlob-", ".jpg")
                });

        Set<String> remainingBlobKeys = new HashSet<String>();
        for(String key:blobKeys) {
            remainingBlobKeys.add(key);
        }
        for (String blobKeyToRemove:blobKeys) {
            storageStrategy.removeBlob(CONTAINER_NAME, blobKeyToRemove);
            //checks if the blob was removed
            TestUtils.fileExists(blobKeyToRemove, false);
            remainingBlobKeys.remove(blobKeyToRemove);
            //checks if all other blobs still exists
            for(String remainingBlobKey:remainingBlobKeys) {
                TestUtils.fileExists(TARGET_CONTAINER_NAME + File.separator + remainingBlobKey, true);
            }
        }
    }

    public void testRemoveBlob_ContainerNotExists() {
        storageStrategy.removeBlob("asdasdasd", "sdfsdfsdfasd");
    }

    public void testRemoveBlob_BlobNotExists() {
        storageStrategy.createContainer(CONTAINER_NAME);
        storageStrategy.removeBlob(CONTAINER_NAME, "sdfsdfsdfasd");
    }


    public void testGetBlobKeysInsideContainer() throws IOException {
        Iterable<String> resultList;

        //no container
        resultList = storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME);
        assertNotNull(resultList, "Result is null");
        assertFalse(resultList.iterator().hasNext(), "Blobs detected");

        //create blobs
        storageStrategy.createContainer(CONTAINER_NAME);
        Set<String> createBlobKeys = TestUtils.createBlobsInContainer(
                CONTAINER_NAME,
                new String[]{
                    TestUtils.createRandomBlobKey("GetBlobKeys-", ".jpg"),
                    TestUtils.createRandomBlobKey("GetBlobKeys-", ".jpg"),
                    TestUtils.createRandomBlobKey("563" + File.separator + "g3sx2" + File.separator + "removeBlob-", ".jpg"),
                    TestUtils.createRandomBlobKey("563" + File.separator + "g3sx2" + File.separator + "removeBlob-", ".jpg")
                });
        storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME);

        List<String> retrievedBlobKeys = new ArrayList<String>();
        resultList = storageStrategy.getBlobKeysInsideContainer(CONTAINER_NAME);
        Iterator<String> containersIterator = resultList.iterator();
        while(containersIterator.hasNext()){
            retrievedBlobKeys.add(containersIterator.next());
        }
        assertEquals(retrievedBlobKeys.size(), createBlobKeys.size(), "Different blobs number");
        for(String createdBlobKey:createBlobKeys) {
            assertTrue(retrievedBlobKeys.contains(createdBlobKey), "Blob " + createdBlobKey + " not found");
        }
    }


    public void testCountsBlob() {
        try {
            storageStrategy.countBlobs(CONTAINER_NAME, ListContainerOptions.NONE);
            fail("Magically the method was implemented... Wow!");
        } catch (UnsupportedOperationException e) {
        }
    }


    //---------------------------------------------------------- Private methods



}
