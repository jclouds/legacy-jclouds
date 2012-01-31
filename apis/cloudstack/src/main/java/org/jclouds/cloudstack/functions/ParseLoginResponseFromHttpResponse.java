package org.jclouds.cloudstack.functions;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;

/**
 * @author Andrei Savu
 */
public class ParseLoginResponseFromHttpResponse implements Function<HttpResponse, LoginResponse> {

   private ParseFirstJsonValueNamed<LoginResponse> parser;

   @Inject
   ParseLoginResponseFromHttpResponse(GsonWrapper gson) {
      this.parser = new ParseFirstJsonValueNamed<LoginResponse>(checkNotNull(gson, "gsonWrapper"),
         new TypeLiteral<LoginResponse>(){}, "loginresponse");
   }

   @Override
   public LoginResponse apply(HttpResponse response) {
      checkNotNull(response, "response");

      LoginResponse login =  parser.apply(response);
      checkNotNull(login, "loginResponse");

      String jSessionId = get(Splitter.on("=").split(get(Splitter.on(";").trimResults().split(
         getOnlyElement(response.getHeaders().get("Set-Cookie"))), 0)), 1);
      
      return LoginResponse.builder().copyOf(login).jSessionId(jSessionId).build();
   }
}
