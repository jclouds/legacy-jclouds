package org.jclouds.cloudstack.predicates;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.predicates.UserPredicates.apiKeyEquals;
import static org.jclouds.cloudstack.predicates.UserPredicates.isAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isDomainAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isUserAccount;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class UserPredicatesTest {

   @Test
   public void testMatchApiKey() {
      assertTrue(apiKeyEquals("random-text").apply(
         User.builder().apiKey("random-text").build()
      ));
      assertFalse(apiKeyEquals("something-different").apply(
         User.builder().apiKey("random-text").build()
      ));
   }

   @Test
   public void testIsUserAccount() {
      User adminUser = User.builder().accountType(Account.Type.ADMIN).build();
      assertFalse(isUserAccount().apply(adminUser));
      assertFalse(isDomainAdminAccount().apply(adminUser));
      assertTrue(isAdminAccount().apply(adminUser));
   }

}
