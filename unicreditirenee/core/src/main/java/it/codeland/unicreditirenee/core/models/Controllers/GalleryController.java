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

import it.codeland.unicreditirenee.core.models.Gallery;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = Gallery.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class GalleryController implements Gallery {
    private static final Logger LOG = LoggerFactory.getLogger(GalleryController.class);

    @SlingObject
    private Resource currentResource;

    @Override
    public List<Map<String, String>> getSlideWithDetails() {
        List<Map<String, String>> allSlides = new ArrayList<>();
        
        try {
            Resource slideDetails = currentResource.getChild("slidewithdetails");
            if (slideDetails == null) {
                return Collections.emptyList();
             }
            if(slideDetails!=null){
                for (Resource child : slideDetails.getChildren()) {
                    Map<String,String> slidesMap=new HashMap<>();
                    slidesMap.put("description",child.getValueMap().get("description",String.class));
                    slidesMap.put("image",child.getValueMap().get("imageref",String.class));
                    slidesMap.put("smallimage",child.getValueMap().get("smallimageref",String.class));
                    allSlides.add(slidesMap);
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting slide Details {} ",e.getMessage());
        }
        return allSlides;
    }
    
} 