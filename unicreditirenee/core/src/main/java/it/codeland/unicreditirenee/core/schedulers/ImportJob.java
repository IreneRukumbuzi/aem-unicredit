// package it.codeland.unicreditirenee.core.schedulers;

// import com.day.cq.wcm.api.PageManager;
// import org.apache.sling.api.resource.Resource;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceResolverFactory;
// import org.apache.sling.commons.scheduler.ScheduleOptions;
// import org.apache.sling.commons.scheduler.Scheduler;
// import org.joda.time.DateTime;
// import java.util.concurrent.TimeUnit;
// import org.osgi.service.component.annotations.Activate;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Deactivate;
// import org.osgi.service.component.annotations.Reference;
// import org.osgi.service.metatype.annotations.AttributeDefinition;
// import org.osgi.service.metatype.annotations.AttributeType;
// import org.osgi.service.metatype.annotations.Designate;
// import org.osgi.service.metatype.annotations.ObjectClassDefinition;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import it.codeland.unicreditirenee.core.services.PageService;
// import it.codeland.unicreditirenee.core.utils.CsvUtil;
// import it.codeland.unicreditirenee.core.utils.ResourceUtil;

// import javax.jcr.Node;
// import javax.jcr.Session;
// import java.util.*;
// import java.util.stream.Collectors;

// @Designate(ocd=ImportJob.Config.class)
// @Component(service=Runnable.class)
// public class ImportJob implements Runnable {
//     private final Logger logger = LoggerFactory.getLogger(getClass());
//     private final int FIELDS_MAX = 5;
//     private final int FIELD_INDEX_PAGE_TITLE = 0;
//     private final int FIELD_INDEX_PAGE_NAME = 1;
//     private final int FIELD_INDEX_IMAGE = 2;
//     private final int FIELD_INDEX_TAGS = 3;
//     private final int FIELD_INDEX_ABSTRACT = 4;

//     @ObjectClassDefinition(name="UCS Import Job",
//             description = "Import CSV containing articles data")
//     public static @interface Config {
//         String SCHEDULERNAME = "ImportJob";

//         @AttributeDefinition(name = "Cron-job expression",
//             description="Cron-job expression",
//             type=AttributeType.STRING)
//         String scheduler_expression() default "0/10 * * ? * *"; // every 10 seconds

//         @AttributeDefinition(name = "Concurrent task",
//             description = "Whether or not to schedule this task concurrently",
//             type = AttributeType.STRING)
//         boolean scheduler_concurrent() default false;

//         @AttributeDefinition(name = "CSV file path",
//             description = "CSV file path in DAM",
//             type = AttributeType.STRING)
//         String CSVFilePath() default "/content/dam/unicreditirenee/articles.csv";
//     }

//     @Reference
//     private Scheduler scheduler;

//     @Reference
//     private ResourceResolverFactory resolverFactory;

//     private int schedulerId;
//     private String CSVFilePath;

//     @Activate
//     protected void activate(Config config){
//         logger.debug("Starting job");
//         schedulerId = config.SCHEDULERNAME.hashCode();
//         CSVFilePath = config.CSVFilePath();
//         addScheduler(config);
//     }

//     private void addScheduler(Config config) {
//         logger.debug("Adding scheduler");
//         ScheduleOptions options = scheduler.EXPR(config.scheduler_expression());
//         options.name(String.valueOf(schedulerId));
//         options.canRunConcurrently(false);
//         scheduler.schedule(this, options);
//     }

//     @Deactivate
//     protected void deactivate(Config config){
//         logger.debug("Stopping job");
//         removeScheduler();
//     }

//     private void removeScheduler() {
//         logger.debug("Removing scheduler");
//         scheduler.unschedule(String.valueOf(schedulerId));
//     }


//     private class ArticleData{
//         String pageTitle = "";
//         String pageName = "";
//         String image = "";
//         String[] tags;
//         String abstractText = "";
//     }

//     private class Statistics{
//         Integer processedRecords = 0;
//         Integer skippedRecords = 0;
//         Integer errorRecords = 0;
//     }

//     @Override
//     public void run() {
//         PageManager pageManager;
//         Session session = null;
//         ArticleData articleData = new ArticleData();
//         Statistics statistics = new Statistics();

//         try {
//             ResourceResolver resolver = ResourceUtil.getResourceResolver(resolverFactory);
//             Resource resource = resolver.getResource(CSVFilePath);
//             if(resource == null){
//                 logger.info("CSV File does not exists!");
//                 return;
//             }

//             CsvUtil csvFile = new CsvUtil();
//             Iterator<String[]> csvData = csvFile.readCsvFile(resolver, CSVFilePath);
//             pageManager = resolver.adaptTo(PageManager.class);
            
//             session = resolver.adaptTo(Session.class);

//             Resource csvFileRes = resolver.getResource(CSVFilePath);
//             Node csvNode = csvFileRes.adaptTo(Node.class);
//             DateTime fileCreatedAt = new DateTime(csvNode.getProperty("jcr:created").getValue().toString());

//             while (csvData.hasNext()) {

//                 TimeUnit.SECONDS.sleep(5);
//                 String[] row = csvData.next();
//                 if (row.length == 1) {
//                     row = row[0].split(",");
//                 }

//                 // Every row MUST have FIELDS_MAX fields
//                 if(row.length == FIELDS_MAX){
//                     // Extract data from CSV
//                     articleData.pageName = row[FIELD_INDEX_PAGE_NAME];
//                     // Check if page is already existing
//                     if(pageManager.getPage("/content/unicreditirenee/magazine/" + articleData.pageName + "/") != null){
//                         statistics.skippedRecords++;
//                         logger.info("Page \"{}\" already exists", articleData.pageName);
//                         continue;
//                     }
//                     articleData.pageTitle = row[FIELD_INDEX_PAGE_TITLE];
//                     articleData.image = row[FIELD_INDEX_IMAGE];

//                     articleData.tags = Arrays.stream(row[FIELD_INDEX_TAGS].split(",")).map(String::trim).
//                             collect(Collectors.toList()).toArray(new String[0]);
//                     articleData.abstractText = row[FIELD_INDEX_ABSTRACT];
//                     // Create page and set properties in jcr:content node
//                     PageService ps = new PageService();
//                     ps.CreatePage(CSVFilePath, resolver);
//                 } else {
//                     statistics.errorRecords++;
//                     logger.error("Processing failed on row: {}", Arrays.asList(row));
//                 }
//             }
//             session.save();
//             resolver.close();
//             logger.info("Records processed: {}", statistics.processedRecords);
//             logger.info("Records skipped: {}", statistics.skippedRecords);
//             logger.info("Records with errors: {}", statistics.errorRecords);
//         } catch (Exception e) {
//             logger.error("Error occurs: {}", e.getMessage());
//             e.printStackTrace();
//         }
//     }
// }

