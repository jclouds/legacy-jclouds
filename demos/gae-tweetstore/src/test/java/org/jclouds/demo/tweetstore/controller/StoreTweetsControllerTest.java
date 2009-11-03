package org.jclouds.demo.tweetstore.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.domain.User;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code StoreTweetsController}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.StoreTweetsControllerTest")
public class StoreTweetsControllerTest {

   TwitterClient createTwitterClient() {
      return createMock(TwitterClient.class);
   }

   Map<String, BlobStoreContext<?>> createBlobStores() throws InterruptedException,
            ExecutionException {
      Map<String, BlobStoreContext<?>> contexts = ImmutableMap.<String, BlobStoreContext<?>> of(
               "test1", new StubBlobStoreContextBuilder().buildContext(), "test2",
               new StubBlobStoreContextBuilder().buildContext());
      for (BlobStoreContext<?> blobstore : contexts.values()) {
         blobstore.getBlobStore().createContainer("favo").get();
      }
      return contexts;
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      Map<String, BlobStoreContext<?>> stores = createBlobStores();
      StoreTweetsController function = new StoreTweetsController(stores, "favo",
               createTwitterClient());

      SortedSet<Status> allAboutMe = Sets.newTreeSet();
      User frank = new User();
      frank.setScreenName("frank");
      Status frankStatus = new Status();
      frankStatus.setId(1);
      frankStatus.setUser(frank);
      frankStatus.setText("I love beans!");

      User jimmy = new User();
      jimmy.setScreenName("jimmy");
      Status jimmyStatus = new Status();
      jimmyStatus.setId(2);
      jimmyStatus.setUser(jimmy);
      jimmyStatus.setText("cloud is king");

      allAboutMe.add(frankStatus);
      allAboutMe.add(jimmyStatus);

      function.addMyTweets("test1", allAboutMe);
      function.addMyTweets("test2", allAboutMe);

      for (Entry<String, BlobStoreContext<?>> entry : stores.entrySet()) {
         BlobMap map = entry.getValue().createBlobMap("favo");
         Blob frankBlob = map.get("1");
         assertEquals(frankBlob.getMetadata().getName(), "1");
         assertEquals(frankBlob.getMetadata().getUserMetadata()
                  .get(TweetStoreConstants.SENDER_NAME), "frank");
         assertEquals(frankBlob.getMetadata().getContentType(), "text/plain");
         assertEquals(IOUtils.toString((InputStream) frankBlob.getData()), "I love beans!");

         Blob jimmyBlob = map.get("2");
         assertEquals(jimmyBlob.getMetadata().getName(), "2");
         assertEquals(jimmyBlob.getMetadata().getUserMetadata()
                  .get(TweetStoreConstants.SENDER_NAME), "jimmy");
         assertEquals(jimmyBlob.getMetadata().getContentType(), "text/plain");
         assertEquals(IOUtils.toString((InputStream) jimmyBlob.getData()), "cloud is king");
      }

   }
}
