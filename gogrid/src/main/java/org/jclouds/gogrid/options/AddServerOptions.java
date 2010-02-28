package org.jclouds.gogrid.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.*;

/**
 * @author Oleksiy Yarmula
 */
public class AddServerOptions extends BaseHttpRequestOptions {

    public AddServerOptions setDescription(String description) {
        checkState(!queryParameters.containsKey(DESCRIPTION_KEY), "Can't have duplicate server description");
        queryParameters.put(DESCRIPTION_KEY, description);
        return this;
    }

    /**
     * Make server a sandbox instance.
     * By default, it's not.
     *
     * @return itself for convenience
     */
    public AddServerOptions makeSandboxType() {
        checkState(!queryParameters.containsKey(IS_SANDBOX_KEY), "Can only have one sandbox option per server");
        queryParameters.put(IS_SANDBOX_KEY, "true");
        return this;
    }

}
