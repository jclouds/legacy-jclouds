package org.jclouds.hpcloud.objectstorage.internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "unit", testName = "HPCloudObjectStorageExpectTest")
public class HPCloudObjectStorageExpectTest extends BaseRestClientExpectTest<BlobStore> {


    protected HttpRequest keystoneAuthWithUsernameAndPassword;
    protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
    protected String authToken;
    protected HttpResponse responseWithKeystoneAccess;

    public HPCloudObjectStorageExpectTest() {
        provider = "hpcloud-objectstorage";
        keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
                credential);
        keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKey(identity,
                credential);
        authToken = KeystoneFixture.INSTANCE.getAuthToken();
        responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();

        identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
    }

    public BlobStore createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
        return new BlobStoreContextFactory(setupRestProperties())
                .createContext(provider, identity, credential, ImmutableSet.<Module>of(new ExpectModule(fn),
                        new NullLoggingModule(), module), props)
                .getBlobStore();
    }

    public void testListObjectsWhenResponseIs2xx() throws Exception {
        Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder().put(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess).build();

        BlobStore clientWhenServersExist = requestsSendResponses(requestResponseMap);

        Set<? extends Location> locations = clientWhenServersExist.listAssignableLocations();
        assertNotNull(locations);
        assertEquals(locations.size(), 1);
        assertEquals(locations.iterator().next().getId(), "region-a.geo-1");
    }
}
