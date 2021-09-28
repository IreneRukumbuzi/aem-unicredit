package it.codeland.unicreditirenee.core.models.Controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import it.codeland.unicreditirenee.core.models.Header;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = Header.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class HeaderController implements Header {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderController.class);

    @SlingObject
    private Resource currentResource;

    @Override
    public List<Map<String, String>> getLinkDetailsWithMap() {
        List<Map<String, String>> allLinks = new ArrayList<>();
        
        try {
            Resource linkDetails = currentResource.getChild("linkwithdetails");
            if (linkDetails == null) {
                return Collections.emptyList();
             }
            if(linkDetails!=null){
                for (Resource child : linkDetails.getChildren()) {
                    Map<String,String> linksMap=new HashMap<>();
                    linksMap.put("linklabel",child.getValueMap().get("linklabel",String.class));
                    linksMap.put("icon",child.getValueMap().get("icon",String.class));
                    linksMap.put("link",child.getValueMap().get("link",String.class));
                    allLinks.add(linksMap);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting link Details {} ",e.getMessage());
        }
        return allLinks;
    }
    
} 