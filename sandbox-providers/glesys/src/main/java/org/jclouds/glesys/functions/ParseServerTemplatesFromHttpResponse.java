package org.jclouds.glesys.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.glesys.domain.ServerTemplate;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseServerTemplatesFromHttpResponse implements Function<HttpResponse, Set<ServerTemplate>> {
   private final ParseFirstJsonValueNamed<Map<String, Set<ServerTemplate>>> parser;

   @Inject
   public ParseServerTemplatesFromHttpResponse(GsonWrapper gsonWrapper) {
      this.parser = new ParseFirstJsonValueNamed<Map<String, Set<ServerTemplate>>>(checkNotNull(gsonWrapper,
               "gsonWrapper"), new TypeLiteral<Map<String, Set<ServerTemplate>>>() {
      }, "templates");
   }

   public Set<ServerTemplate> apply(HttpResponse response) {
      checkNotNull(response, "response");
      Map<String, Set<ServerTemplate>> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      return ImmutableSet.copyOf(Iterables.concat(toParse.values()));
   }
}
