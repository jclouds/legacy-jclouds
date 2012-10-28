package org.jclouds.oauth.v2.config;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.functions.DefaultAuthenticator;
import org.jclouds.oauth.v2.functions.OAuthCredentialsFromPKCS12File;
import org.jclouds.oauth.v2.functions.SignerFunction;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.oauth.v2.OAuthConstants.SIGNATURE_ALGORITHM;

public class OAuthBaseModule extends AbstractModule {

   private Map<String, String> OAUTH_ALGORITHM_NAMES_TO_CRYPTO_ALGORITHM_NAMES = ImmutableMap.of("RS256", "SHA256withRSA");

   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<byte[], byte[]>>() {}).to(SignerFunction.class);
      bind(new TypeLiteral<Map<Type, Object>>() {
      }).toInstance(ImmutableMap.<Type, Object>of(Header.class, new HeaderTypeAdapter(), ClaimSet.class,
              new ClaimSetTypeAdapter()));
      bind(new TypeLiteral<Supplier<OAuthCredentials>>() {}).to(OAuthCredentialsFromPKCS12File.class);
      bind(new TypeLiteral<Function<Credentials, Token>>() {}).to(DefaultAuthenticator.class);
   }

   @Provides
   public Supplier<Signature> provideSignature(@Named(SIGNATURE_ALGORITHM) String algoName, Crypto crypto)
           throws NoSuchAlgorithmException {
      if (!OAUTH_ALGORITHM_NAMES_TO_CRYPTO_ALGORITHM_NAMES.containsKey(algoName)) {
         throw new NoSuchAlgorithmException("Unsupported signature algorithm: " + algoName);
      }
      return Suppliers.ofInstance(crypto.signature(OAUTH_ALGORITHM_NAMES_TO_CRYPTO_ALGORITHM_NAMES.get(algoName)));
   }

   // TODO: the token actually includes the expiration time (<= 1 hr) cache should be changed accordingly
   @Provides
   @Singleton
   public LoadingCache<Credentials, Token> provideAccessCache(Function<Credentials, Token> getAccess) {
      return CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build(CacheLoader.from(getAccess));
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   @Authentication
   protected Supplier<Token> provideAccessSupplier(final LoadingCache<Credentials, Token> cache,
                                                   @org.jclouds.location.Provider final Credentials creds) {
      return new Supplier<Token>() {
         @Override
         public Token get() {
            try {
               return cache.get(creds);
            } catch (ExecutionException e) {
               throw propagate(e.getCause());
            }
         }
      };
   }
}
