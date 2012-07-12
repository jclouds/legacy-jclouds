package org.jclouds.iam;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.jclouds.collect.PaginatedIterable;
import org.jclouds.collect.PaginatedIterables;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserApi;
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
      UserApi userApi = createMock(UserApi.class);
      ListUsersOptions options = new ListUsersOptions();
      PaginatedIterable<User> response = PaginatedIterables.forward(ImmutableSet.of(createMock(User.class)));
      
      expect(userApi.list(options))
            .andReturn(response)
            .once();

      EasyMock.replay(userApi);

      Assert.assertEquals(1, Iterables.size(IAM.list(userApi, options)));
   }


   @Test
   public void testMultiPageResult() throws Exception {
      UserApi userApi = createMock(UserApi.class);
      ListUsersOptions options = new ListUsersOptions();
      PaginatedIterable<User> response1 = PaginatedIterables.forwardWithMarker(ImmutableSet.of(createMock(User.class)), "NEXTTOKEN");
      PaginatedIterable<User> response2 = PaginatedIterables.forward(ImmutableSet.of(createMock(User.class)));

      expect(userApi.list(anyObject(ListUsersOptions.class)))
            .andReturn(response1)
            .once();
      expect(userApi.list(anyObject(ListUsersOptions.class)))
            .andReturn(response2)
            .once();

      EasyMock.replay(userApi);

      Assert.assertEquals(2, Iterables.size(IAM.list(userApi, options)));
   }

}
