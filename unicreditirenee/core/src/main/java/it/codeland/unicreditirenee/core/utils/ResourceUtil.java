package it.codeland.unicreditirenee.core.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class ResourceUtil {
    public static ResourceResolver getResourceResolver(ResourceResolverFactory resourceFactory) throws LoginException {
        Map<String,Object> systemUser = new HashMap<String, Object>();

        systemUser.put(ResourceResolverFactory.SUBSERVICE, "sysuser");

        return resourceFactory.getServiceResourceResolver(systemUser);
    }
}