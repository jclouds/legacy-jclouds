package org.jclouds.demo.tweetstore.controller;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.integration.StubBlobStoreContextBuilder;
import org.jclouds.demo.tweetstore.domain.StoredTweetStatus;
import org.jclouds.demo.tweetstore.functions.ServiceToStoredTweetStatuses;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AddTweetsController}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "tweetstore.AddTweetsControllerTest")
public class AddTweetsControllerTest {

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
      AddTweetsController controller = new AddTweetsController(contexts, function);
      List<StoredTweetStatus> list = controller.apply(ImmutableSet.of("1", "2"));
      assertEquals(list.size(), 2);
      assertEquals(list, ImmutableList.of(new StoredTweetStatus("1", "localhost", container, "1",
               "frank", "I love beans!", null), new StoredTweetStatus("2", "localhost", container,
               "1", "frank", "I love beans!", null)));

   }
}
