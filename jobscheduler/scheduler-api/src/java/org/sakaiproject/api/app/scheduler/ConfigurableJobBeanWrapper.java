package org.sakaiproject.api.app.scheduler;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * This interface extends the JobWrapper interface such that some jobs may provide definitions of properties which
 * can be configured for the Job, or its Triggers.
 *
 * Created by IntelliJ IDEA.
 * User: duffy
 * Date: Jul 26, 2010
 * Time: 3:41:58 PM
 */
public interface ConfigurableJobBeanWrapper
    extends JobBeanWrapper
{
    /**
     *  Since the job will be defined in a component outside of the job scheduler, the job scheduler has no knowledge
     *  of the keys nor the resource files to use when rendering these properties in the UI. Thus, the job should
     *  be configured in a wrapper which supplies a ResourceBundle to the job scheduler. That ResourceBundle will
     *  supply the strings to use in the UI.
     */
    //public ResourceBundle getResourceBundle();

    //public String getResourceString(String key);

    public String getResourceBundleBase();
    
    /**
     *  Returns the definitions of the properties which should be presented to the user for configuring the job
     *  that this object wraps.
     */
    public Set<ConfigurableJobProperty> getConfigurableJobProperties();

    public ConfigurableJobPropertyValidator getConfigurableJobPropertyValidator();
}
