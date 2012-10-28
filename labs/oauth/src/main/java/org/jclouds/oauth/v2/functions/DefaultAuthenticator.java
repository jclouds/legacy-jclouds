package org.jclouds.oauth.v2.functions;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jclouds.domain.Credentials;
import org.jclouds.oauth.v2.OAuthClient;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.domain.TokenRequestFormat;

import javax.inject.Singleton;
import java.util.Map;

import static org.jclouds.oauth.v2.OAuthConstants.ADDITIONAL_CLAIMS;
import static org.jclouds.oauth.v2.OAuthConstants.SIGNATURE_ALGORITHM;
import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_ASSERTION_DESCRIPTION;
import static org.jclouds.oauth.v2.OAuthConstants.TOKEN_SCOPE;

/**
 * The default authenticator.
 * <p/>
 * Builds the default token request with the following claims: iss,scope,aud,iat,exp.
 * <p/>
 * TODO scopes etc should come from the REST method and not from a global property
 */
@Singleton
public class DefaultAuthenticator extends BaseAuthenticator {

   private final String scope;
   private final String assertionTargetDescription;
   private final String signatureAlgorithm;
   private final TokenRequestFormat tokenRequestFormat;
   @Inject(optional = true)
   @Named(ADDITIONAL_CLAIMS)
   protected Map<String, String> additionalClaims;

   @Inject
   public DefaultAuthenticator(OAuthClient oauthClient,
                               @Named(TOKEN_SCOPE) String scope,
                               @Named(TOKEN_ASSERTION_DESCRIPTION) String assertionTargetDescription,
                               @Named(SIGNATURE_ALGORITHM) String signatureAlgorithm,
                               TokenRequestFormat tokenRequestFormat) {
      super(oauthClient);
      this.scope = scope;
      this.assertionTargetDescription = assertionTargetDescription;
      this.signatureAlgorithm = signatureAlgorithm;
      this.tokenRequestFormat = tokenRequestFormat;
      this.additionalClaims = additionalClaims == null ? ImmutableMap.<String, String>of() : additionalClaims;
   }

   protected TokenRequest buildTokenRequest(Credentials creds, long now) {
      // fetch the token
      Header header = new Header.Builder()
              .signer(signatureAlgorithm)
              .type(tokenRequestFormat.getTypeName())
              .build();

      ClaimSet claimSet = new ClaimSet.Builder(this.tokenRequestFormat.requiredClaimSet())
              .addClaim("iss", creds.identity)
              .addClaim("scope", scope)
              .addClaim("aud", assertionTargetDescription)
              .emissionTime(now)
              .expirationTime(now + 3600)
              .addAllClaims(additionalClaims)
              .build();

      return new TokenRequest.Builder()
              .header(header)
              .claimSet(claimSet)
              .build();
   }

}
