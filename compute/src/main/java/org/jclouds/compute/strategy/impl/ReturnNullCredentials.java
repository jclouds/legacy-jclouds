package org.jclouds.compute.strategy.impl;

import javax.inject.Singleton;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;

/**
 * @author Adrian Cole
 */
@Singleton
public class ReturnNullCredentials implements PopulateDefaultLoginCredentialsForImageStrategy {

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      return null;
   }
}
