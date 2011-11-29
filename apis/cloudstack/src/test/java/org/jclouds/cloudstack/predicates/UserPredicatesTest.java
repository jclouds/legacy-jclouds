package org.jclouds.cloudstack.predicates;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.predicates.UserPredicates.apiKeyEquals;
import static org.jclouds.cloudstack.predicates.UserPredicates.isAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isDomainAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isUserAccount;
import static org.testng.Assert.assertEquals;
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

   @DataProvider(name = "accountType")
   public Object[][] getAccountTypes() {
      return new Object[][] {
         /* Type ID, isUser, isDomainAdmin, isAdmin */
         {Account.Type.USER, true, false, false},
         {Account.Type.DOMAIN_ADMIN, false, true, false},
         {Account.Type.ADMIN, false, false, true},
         {Account.Type.UNRECOGNIZED, false, false, false}
      };
   }

   @Test(dataProvider = "accountType")
   public void testAccountType(Account.Type type, boolean isUser, boolean isDomainAdmin, boolean isAdmin) {
      User testUser = User.builder().accountType(type).build();
      assertEquals(isUserAccount().apply(testUser), isUser);
      assertEquals(isDomainAdminAccount().apply(testUser), isDomainAdmin);
      assertEquals(isAdminAccount().apply(testUser), isAdmin);
   }

}
