package org.jclouds.demo.tweetstore.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code KeyToStoredTweetStatus}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.KeyToStoredTweetStatusTest")
public class KeyToStoredTweetStatusTest {

   BlobMap createMap() throws InterruptedException, ExecutionException {
      BlobStoreContext<BlobStore> context = new StubBlobStoreContextBuilder().buildContext();
      context.getBlobStore().createContainer("test1").get();
      return context.createBlobMap("test1");
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      BlobMap map = createMap();
      Blob blob = map.newBlob();
      blob.getMetadata().setName("1");
      blob.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, "frank");
      blob.setData("I love beans!");
      map.put("1", blob);
      String host = "localhost";
      String service = "stub";
      String container = "tweetstore";

      KeyToStoredTweetStatus function = new KeyToStoredTweetStatus(map, service, host, container);
      StoredTweetStatus result = function.apply("1");

      StoredTweetStatus expected = new StoredTweetStatus(service, host, container, "1", "frank",
               "I love beans!", null);

      assertEquals(result, expected);

   }
}
