package org.jclouds.iam;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.jclouds.collect.PaginatedSet;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserClient;
import org.jclouds.iam.options.ListUsersOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code IAM}.
 *
 * @author Adrian Cole
 */
@Test(testName = "IAMTest")
public class IAMTest {


   @Test
   public void testSinglePageResult() throws Exception {
      UserClient userClient = createMock(UserClient.class);
      ListUsersOptions options = new ListUsersOptions();
      PaginatedSet<User> response = PaginatedSet.copyOf(ImmutableSet.of(createMock(User.class)));
      
      expect(userClient.list(options))
            .andReturn(response)
            .once();

      EasyMock.replay(userClient);

      Assert.assertEquals(1, Iterables.size(IAM.list(userClient, options)));
   }


   @Test
   public void testMultiPageResult() throws Exception {
      UserClient userClient = createMock(UserClient.class);
      ListUsersOptions options = new ListUsersOptions();
      PaginatedSet<User> response1 = PaginatedSet.copyOfWithMarker(ImmutableSet.of(createMock(User.class)), "NEXTTOKEN");
      PaginatedSet<User> response2 = PaginatedSet.copyOf(ImmutableSet.of(createMock(User.class)));

      expect(userClient.list(anyObject(ListUsersOptions.class)))
            .andReturn(response1)
            .once();
      expect(userClient.list(anyObject(ListUsersOptions.class)))
            .andReturn(response2)
            .once();

      EasyMock.replay(userClient);

      Assert.assertEquals(2, Iterables.size(IAM.list(userClient, options)));
   }

}
