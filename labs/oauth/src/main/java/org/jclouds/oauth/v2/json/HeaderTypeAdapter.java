package org.jclouds.oauth.v2.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jclouds.oauth.v2.domain.Header;

import java.io.IOException;

public class HeaderTypeAdapter extends TypeAdapter<Header> {

   @Override
   public void write(JsonWriter out, Header value) throws IOException {
      out.beginObject();
      out.name("alg");
      out.value(value.getSignerAlgorithm());
      out.name("typ");
      out.value(value.getType());
      out.endObject();
   }

   @Override
   public Header read(JsonReader in) throws IOException {
      Header.Builder builder = new Header.Builder();
      in.beginObject();
      in.nextName();
      builder.signer(in.nextString());
      in.nextName();
      builder.type(in.nextString());
      in.endObject();
      return builder.build();
   }
}
