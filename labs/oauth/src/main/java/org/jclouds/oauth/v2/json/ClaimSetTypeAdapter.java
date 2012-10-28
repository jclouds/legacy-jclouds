package org.jclouds.oauth.v2.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jclouds.oauth.v2.domain.ClaimSet;

import java.io.IOException;
import java.util.Map;

public class ClaimSetTypeAdapter extends TypeAdapter<ClaimSet> {

   @Override
   public void write(JsonWriter out, ClaimSet value) throws IOException {
      out.beginObject();
      for (Map.Entry<String, String> entry : value.getClaims().entrySet()) {
         out.name(entry.getKey());
         out.value(entry.getValue());
      }
      out.name("exp");
      out.value(value.getExpirationTime());
      out.name("iat");
      out.value(value.getEmissionTime());
      out.endObject();
   }

   @Override
   public ClaimSet read(JsonReader in) throws IOException {
      ClaimSet.Builder builder = new ClaimSet.Builder();
      in.beginObject();
      while (in.hasNext()) {
         String claimName = in.nextName();
         String claimValue = in.nextString();
         builder.addClaim(claimName, claimValue);
      }
      in.endObject();
      return builder.build();
   }
}
