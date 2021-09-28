package it.codeland.unicreditirenee.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
    resourceTypes="cq:Page", 
    methods= "GET",
    selectors="export",
    extensions="json"
  )
@ServiceDescription("Article Servlet")
public class ArticlesServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ArticlesServlet.class);
    
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
                Article article = new Article();
                SimpleDateFormat format1 = new SimpleDateFormat("E dd MMM yyyy"); 

                try {      
                    Resource resource = req.getResource();
                    Resource res = resource.getChild("jcr:content");
                    ValueMap art = res.adaptTo(ValueMap.class);
                    String text = art.get("text", String.class);
                    String title = art.get("jcr:title", String.class);
                    String img = art.get("image", String.class);
                    String[] tags = art.get("cq:tags", String[].class);
                    String url = res.getParent().getPath()+".html";
                    Date dte = art.get("date", Date.class);

                    article.setTitle(title);
                    article.setArticleAbstract(text);
                    article.setImage(img);
                    article.setDate(format1.format(dte));
                    article.setTags(tags);
                    article.setLink(url);

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(article);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonString);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } 
    }
}
