package it.codeland.unicreditirenee.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
@Component(service = { Servlet.class }) 
@SlingServletPaths(
            value={"/bin/hashtag"})
@ServiceDescription("Related hashtag for articles Servlet")
public class TagsServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(TagsServlet.class);
   
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
                List<Article> articles = new ArrayList<>();
                List<Node> nodes = new ArrayList<>();
                String tag = ""; 
                Integer maxArticle = 20; 
                Session session = req.getResourceResolver().adaptTo(Session.class);
                try {      
                    tag = req.getParameter("tag"); 
                    maxArticle = Integer.parseInt(req.getParameter("max"));
                    QueryManager queryMngr = session.getWorkspace().getQueryManager();

                    String myQuery = "SELECT * FROM [cq:PageContent] AS nodes WHERE ISDESCENDANTNODE ([/content/unicreditirenee/magazine]) AND nodes.[cq:tags] LIKE '%"+tag+"' ORDER BY [jcr:created] ASC";
                    Query query = queryMngr.createQuery(myQuery, "JCR-SQL2");
                    query.setLimit(maxArticle);
                    QueryResult result = query.execute();
                    
                    Iterator<Node> myNodesIterator = result.getNodes();
                
                    while (myNodesIterator.hasNext()) {
                        Node currentNode = myNodesIterator.next();      
                        nodes.add(currentNode);
                    }

                    SimpleDateFormat format1 = new SimpleDateFormat("E dd MMM yyyy"); 
                    
                    for(Node item : nodes) {
                        String title = item.getProperty("jcr:title").getString();
                        Date dte = item.getProperty("date").getDate().getTime();
                        String img = item.getProperty("image").getString();
                        String txt = item.getProperty("text").getString();
                        String url = item.getParent().getPath()+".html";

                        String[] tags = { tag };
                        Article art = new Article();
                        
                        art.setTitle(title);
                        art.setArticleAbstract(txt);
                        art.setImage(img);
                        art.setDate(format1.format(dte));
                        art.setTags(tags);
                        art.setLink(url);
                        articles.add(art);
                    }
                    ObjectMapper mapValue = new ObjectMapper();
                    String jsonString = mapValue.writeValueAsString(articles);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonString);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                finally{
                    session.logout();
                }
    }
}