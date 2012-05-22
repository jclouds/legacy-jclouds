package org.jclouds.hpcloud.objectstorage.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.domain.Location;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageProviderMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

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


    public void testListObjectsWhenResponseIs2xx() throws Exception {
        Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder().put(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess).build();

        BlobStore clientWhenServersExist = requestsSendResponses(requestResponseMap);

        Set<? extends Location> locations = clientWhenServersExist.listAssignableLocations();
        assertNotNull(locations);
        assertEquals(locations.size(), 1);
        // TODO: does this location make sense?
        assertEquals(locations.iterator().next().getId(), "hpcloud-objectstorage");
    }
    
    @Override
    public BlobStore createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
       return createInjector(fn, module, props).getInstance(BlobStore.class);
    }
    
    @Override public ProviderMetadata createProviderMetadata(){
       return new HPCloudObjectStorageProviderMetadata();
    }
}
