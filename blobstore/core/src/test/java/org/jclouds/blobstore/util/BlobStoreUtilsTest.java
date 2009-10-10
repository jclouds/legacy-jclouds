package org.jclouds.blobstore.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.domain.Key;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BlobStoreUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "blobstore.BlobStoreUtilsTest")
public class BlobStoreUtilsTest {

   public void testParseKey() {
      Key key = BlobStoreUtils.parseKey(new Key("container", "key"));
      assertEquals(key.getContainer(), "container");
      assertEquals(key.getKey(), "key");
      key = BlobStoreUtils.parseKey(new Key("container", "container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");
      key = BlobStoreUtils.parseKey(new Key("container", "/container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");

   }
}
