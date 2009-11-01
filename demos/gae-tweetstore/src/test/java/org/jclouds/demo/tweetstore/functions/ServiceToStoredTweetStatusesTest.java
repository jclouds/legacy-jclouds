package org.jclouds.demo.tweetstore.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code ServiceToStoredTweetStatuses}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.ServiceToStoredTweetStatuses")
public class ServiceToStoredTweetStatusesTest {

   Map<String, BlobStoreContext<?>> createServices(String container) throws InterruptedException,
            ExecutionException {
      Map<String, BlobStoreContext<?>> services = Maps.newHashMap();
      for (String name : new String[] { "1", "2" }) {
         BlobStoreContext<BlobStore> context = new StubBlobStoreContextBuilder().buildContext();
         context.getBlobStore().createContainer(container).get();
         Blob blob = context.getBlobStore().newBlob();
         blob.getMetadata().setName("1");
         blob.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, "frank");
         blob.setData("I love beans!");
         context.getBlobStore().putBlob(container, blob).get();
         services.put(name, context);
      }
      return services;
   }

   public void testStoreTweets() throws IOException, InterruptedException, ExecutionException {
      String container = "container";
      Map<String, BlobStoreContext<?>> contexts = createServices(container);

      ServiceToStoredTweetStatuses function = new ServiceToStoredTweetStatuses(contexts, container);

      assertEquals(Iterables.getLast(function.apply("1")), new StoredTweetStatus("1", "localhost",
               container, "1", "frank", "I love beans!", null));

      assertEquals(Iterables.getLast(function.apply("2")), new StoredTweetStatus("2", "localhost",
               container, "1", "frank", "I love beans!", null));

   }
}
