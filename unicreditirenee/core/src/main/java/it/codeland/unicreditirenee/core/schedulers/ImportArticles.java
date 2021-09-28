// package it.codeland.unicreditirenee.core.schedulers;

// import it.codeland.unicreditirenee.core.config.ArticlesOSGi;
// import it.codeland.unicreditirenee.core.services.PageService;
// import it.codeland.unicreditirenee.core.utils.ResourceUtil;
// import org.apache.commons.lang.exception.ExceptionUtils;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceResolverFactory;
// import org.apache.sling.commons.scheduler.Job;
// import org.apache.sling.commons.scheduler.JobContext;
// import org.apache.sling.commons.scheduler.ScheduleOptions;
// import org.apache.sling.commons.scheduler.Scheduler;
// import org.osgi.service.component.annotations.Activate;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Deactivate;
// import org.osgi.service.component.annotations.Reference;
// import org.osgi.service.metatype.annotations.Designate;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


// @Component(immediate = true, service = Job.class)
// @Designate(ocd = ArticlesOSGi.class)
// public class ImportArticles implements Job {
//     private static final Logger LOG = LoggerFactory.getLogger(ImportArticles.class);
//     private ArticlesOSGi config;
    
//     private int schedulerJobId;

//     @Reference
//     private Scheduler scheduler;

//     @Reference
//     private ResourceResolverFactory resourceFactory;

//     @Activate
//     protected void activate(ArticlesOSGi config) {
//         this.config = config;
//         schedulerJobId = "import articles".hashCode();
//         addScheduler(config);
//     }

//     @Deactivate
//     protected void deactivate(ArticlesOSGi config) {
//         removeSchedulerJob();
//     }

//     private void removeSchedulerJob() {
//         scheduler.unschedule(String.valueOf(schedulerJobId));
//     }

//     private void addScheduler(ArticlesOSGi config) {
//         ScheduleOptions in = scheduler.EXPR(config.Expression());
//         in.name(String.valueOf(schedulerJobId));
//         scheduler.schedule(this, in);
//     }

//     @Override
//     public void execute(JobContext context) {
        
//         try {
//             ResourceResolver resolver = ResourceUtil.getResourceResolver(resourceFactory);

//             PageService ps = new PageService();
//             ps.CreatePage(this.config.FilePath(), resolver);

//         } catch (Exception e) {
//             LOG.debug("\n\n\n Error: " +ExceptionUtils.getStackTrace(e));
//         }
//     }
// }