package org.jclouds.gogrid.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import static com.google.common.base.Preconditions.*;
import static org.jclouds.gogrid.reference.GoGridQueryParams.SERVER_TYPE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IS_SANDBOX_KEY;

/**
 * @author Oleksiy Yarmula
 */
public class GetServerListOptions extends BaseHttpRequestOptions {

    public final static GetServerListOptions NONE = new GetServerListOptions();

    public GetServerListOptions limitServerTypeTo(String serverType) {
        checkState(!queryParameters.containsKey(SERVER_TYPE_KEY), "Can't have duplicate server type limit");
        queryParameters.put(SERVER_TYPE_KEY, serverType);
        return this;
    }

    public GetServerListOptions onlySandboxServers() {
        checkState(!queryParameters.containsKey(IS_SANDBOX_KEY), "Can't have duplicate sandbox type limit");
        queryParameters.put(IS_SANDBOX_KEY, "true");
        return this;
    }

    public GetServerListOptions excludeSandboxServers() {
        checkState(!queryParameters.containsKey(IS_SANDBOX_KEY), "Can't have duplicate sandbox type limit");
        queryParameters.put(IS_SANDBOX_KEY, "false");
        return this;
    }

    public static class Builder {

        public GetServerListOptions limitServerTypeTo(String serverType) {
            GetServerListOptions getServerListOptions = new GetServerListOptions();
            getServerListOptions.limitServerTypeTo(checkNotNull(serverType));
            return getServerListOptions;
        }

        public GetServerListOptions onlySandboxServers() {
            GetServerListOptions getServerListOptions = new GetServerListOptions();
            getServerListOptions.onlySandboxServers();
            return getServerListOptions;
        }

        public GetServerListOptions excludeSandboxServers() {
            GetServerListOptions getServerListOptions = new GetServerListOptions();
            getServerListOptions.excludeSandboxServers();
            return getServerListOptions;
        }

    }


}
