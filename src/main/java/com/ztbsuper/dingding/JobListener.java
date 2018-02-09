package com.ztbsuper.dingding;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by Marvin on 16/10/7.
 */
@Extension
public class JobListener extends RunListener<AbstractBuild> {

    private Logger logger = LoggerFactory.getLogger(JobListener.class);

    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
        try {
            getService(r, listener).start();
        } catch (Exception e) {
            logger.error("ignore error");
        }
    }

    @Override
    public void onCompleted(AbstractBuild r, @Nonnull TaskListener listener) {
        try {
            Result result = r.getResult();
            if (null != result && result.equals(Result.SUCCESS)) {
                getService(r, listener).success();
            } else {
                getService(r, listener).failed();
            }
        } catch (Exception e) {
            logger.error("ignore error");
        }
    }

    private DingdingService getService(AbstractBuild build, TaskListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof DingdingNotifier) {
                return ((DingdingNotifier) publisher).newDingdingService(build, listener);
            }
        }
        return null;
    }
}
