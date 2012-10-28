package org.jclouds.oauth.v2;

import java.util.Properties;

import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_ASSERTION_DESCRIPTION;
import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_SCOPE;

public class OAuthTestUtils {

   public static Properties defaultProperties(Properties properties) {
      properties = properties == null ? new Properties() : properties;
      properties.put("oauth.identity", "foo");
      properties.put("oauth.credential", "target/test-classes/test.p12");
      properties.put("oauth.endpoint", "http://localhost:5000/o/oauth2/token");
      properties.put(TOKEN_SCOPE, "https://www.googleapis.com/auth/prediction");
      properties.put(TOKEN_ASSERTION_DESCRIPTION, "https://accounts.google.com/o/oauth2/token");
      return properties;
   }
}
