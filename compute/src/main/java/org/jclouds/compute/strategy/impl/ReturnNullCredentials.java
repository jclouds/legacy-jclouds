package org.jclouds.compute.strategy.impl;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;

/**
 * @author Adrian Cole
 */
@Singleton
public class ReturnNullCredentials implements PopulateDefaultLoginCredentialsForImageStrategy {

   public static final Pattern USER_PASSWORD_PATTERN = Pattern
            .compile(".*[Uu]sername: ([a-z]+) ?.*\n[Pp]assword: ([^ ]+) ?\n.*");

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      return null;
   }
}
