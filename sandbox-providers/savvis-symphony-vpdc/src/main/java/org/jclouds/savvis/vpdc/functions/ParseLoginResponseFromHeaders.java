package org.jclouds.savvis.vpdc.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.internal.VCloudSession;
import org.jclouds.savvis.vpdc.internal.VCloudToken;
import org.jclouds.savvis.vpdc.xml.OrgListHandler;

import com.google.common.base.Function;

/**
 * This parses {@link VCloudSession} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseLoginResponseFromHeaders implements Function<HttpResponse, VCloudSession> {
   static final Pattern pattern = Pattern.compile("vcloud-token=([^;]+);.*");

   private final ParseSax.Factory factory;
   private final Provider<OrgListHandler> orgHandlerProvider;

   @Inject
   private ParseLoginResponseFromHeaders(Factory factory,
            Provider<OrgListHandler> orgHandlerProvider) {
      this.factory = factory;
      this.orgHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link VCloudSession} object.
    */
   public VCloudSession apply(HttpResponse from) {
      String cookieHeader = checkNotNull(from.getFirstHeaderOrNull(HttpHeaders.SET_COOKIE),
               HttpHeaders.SET_COOKIE);

      final Matcher matcher = pattern.matcher(cookieHeader);
      boolean matchFound = matcher.find();
      try {
         if (matchFound) {
            final Set<Resource> org = factory.create(orgHandlerProvider.get()).parse(
                     from.getPayload().getInput());

            return new VCloudSession() {
               @VCloudToken
               public String getVCloudToken() {
                  return matcher.group(1);
               }

               public Set<Resource> getOrgs() {
                  return org;
               }
            };

         }
      } finally {
         releasePayload(from);
      }
      throw new HttpResponseException("not found ", null, from);
   }
}
