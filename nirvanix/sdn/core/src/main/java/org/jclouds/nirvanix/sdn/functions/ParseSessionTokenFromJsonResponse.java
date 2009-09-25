package org.jclouds.nirvanix.sdn.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseJson;

import com.google.gson.Gson;

/**
 * This parses the Nirvanix SessionToken from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseSessionTokenFromJsonResponse extends ParseJson<String> {

   @Inject
   public ParseSessionTokenFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class SessionTokenResponse {
      Integer ResponseCode;
      String SessionToken;
   }

   public String apply(InputStream stream) {

      try {
         SessionTokenResponse response = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  SessionTokenResponse.class);
         if (response.ResponseCode == null || response.ResponseCode != 0)
            throw new RuntimeException("bad response code: " + response.ResponseCode);
         return response.SessionToken;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}