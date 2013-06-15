/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/trunk/kernel-util/src/main/java/org/sakaiproject/util/FormattedText.java $
 * $Id: FormattedText.java 97738 2011-08-31 17:30:03Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.util.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashSet;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.api.FormattedText;

/**
 * FormattedText provides support for user entry of formatted text; the formatted text is HTML. This includes text formatting in user input such as bold, underline, and fonts.
 */
public class FormattedTextImpl implements FormattedText
{
    /** Our log (commons). */
    private static final Log M_log = LogFactory.getLog(FormattedTextImpl.class);

    private ServerConfigurationService serverConfigurationService = null;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    /** Resource bundle and class for retrieving good and bad html tags, attributes and values */
    private String RESOURCE_BUNDLE = "org.sakaiproject.localization.bundle.content_type.formattedtext";
    private String RESOURCE_CLASS  = "org.sakaiproject.localization.util.ContentTypeProperties"; 

    /** Our ResourceLoader that we can use throughout this class */
    private ResourceLoader resourceLoader = null;

    /** An array of regular expression pattern-matchers, that will match the tags given in M_evilTags */
    private Pattern[] M_evilTagsPatterns;

    /** An array of regular expression pattern-matchers, that will match the tags given in M_goodTags */
    private Pattern[] M_goodTagsPatterns;

    /** An array of regular expression pattern-matchers, that will match the tags given in M_goodCloseTags */
    private Pattern[] M_goodCloseTagsPatterns;

    /** An array of regular expression pattern-matchers, that will match the attributes given in M_goodAttributeTags */
    private Pattern[] M_goodAttributePatterns;

    /** An array of regular expression pattern-matchers, that will match the attributes given in M_evilValues */
    private Pattern[] M_evilValuePatterns;

    /**
     * This is the high level html cleaner object
     */
    private AntiSamy antiSamyHigh = null;
    /**
     * This is the low level html cleaner object
     */
    private AntiSamy antiSamyLow = null;

    public void init() {
        boolean useLegacyCleaner = useLegacyCleaner();

        if (!useLegacyCleaner) {
            // INIT Antisamy
            // added in support for antisamy html cleaner - KNL-1015
            // https://www.owasp.org/index.php/Category:OWASP_AntiSamy_Project
            try {
                ClassLoader current = FormattedTextImpl.class.getClassLoader();
                URL lowPolicyURL = current.getResource("antisamy/low-security-policy.xml");
                URL highPolicyURL = current.getResource("antisamy/high-security-policy.xml");
                // Allow lookup of the policy files in sakai home - KNL-1047
                String sakaiHomePath = getSakaiHomeDir();
                File lowFile = new File(sakaiHomePath, "antisamy"+File.separator+"low-security-policy.xml");
                if (lowFile.canRead()) {
                    lowPolicyURL = lowFile.toURI().toURL();
                    M_log.info("AntiSamy found override for low policy file at: "+lowPolicyURL);
                }
                File highFile = new File(sakaiHomePath, "antisamy"+File.separator+"high-security-policy.xml");
                if (highFile.canRead()) {
                    highPolicyURL = highFile.toURI().toURL();
                    M_log.info("AntiSamy found override for high policy file at: "+highPolicyURL);
                }
                Policy policyHigh = Policy.getInstance(highPolicyURL);
                antiSamyHigh = new AntiSamy(policyHigh);
                Policy policyLow = Policy.getInstance(lowPolicyURL);
                antiSamyLow = new AntiSamy(policyLow);
                // TODO should we attempt to fallback to internal files if the parsing/init fails of external ones?
                M_log.info("AntiSamy INIT default security level ("+(defaultLowSecurity()?"LOW":"high")+"), policy files: high="+highPolicyURL+", low="+lowPolicyURL);
            } catch (Exception e) {
                useLegacyCleaner = true;
                antiSamyHigh = null;
                antiSamyLow = null;
                M_log.warn("Unable to startup the antisamy html code cleanup handler (using the legacy cleaner): " + e, e);
            }
        }

        // INIT for the non-antisamy code
        // DEFAULT values to allow for testing and in case the resource loader values do not exist
        M_evilTags = "applet,base,body,bgsound,button,col,colgroup,comment, dfn,fieldset,form,frame,frameset,head,html,iframe,ilayer,inlineinput,isindex,input,keygen,label,layer,legend,link,listing,map,meta,multicol,nextid,noframes,nolayer,noscript,optgroup,option,plaintext,script,select,sound,spacer,spell,submit,textarea,title,wbr".split(",");
        M_goodTags = "a,abbr,acronym,address,b,big,blockquote,br,center,cite,code,dd,del,dir,div,dl,dt,em,font,hr,h1,h2,h3,h4,h5,h6,i,ins,kbd,li,marquee,menu,nobr,noembed,ol,p,pre,q,rt,ruby,rbc,rb,rtc,rp,s,samp,small,span,strike,strong,sub,sup,tt,u,ul,var,xmp,img,embed,object,table,tr,td,th,tbody,caption,thead,tfoot,colgroup,col,param".split(",");
        M_goodAttributes = "abbr,accept,accesskey,align,alink,alt,axis,background,bgcolor,border,cellpadding,cellspacing,char,charoff,charset,checked,cite,class,classid,clear,color,cols,colspan,compact,content,coords,datetime,dir,disabled,enctype,face,for,header,height,href,hreflang,hspace,id,ismap,label,lang,longdesc,maxlength,multiple,name,noshade,nowrap,profile,readonly,rel,rev,rows,rowspan,rules,scope,selected,shape,size,span,src,start,style,summary,tabindex,target,text,title,type,usemap,valign,value,vlink,vspace,width,pluginspage,play,loop,menu,codebase,data,pluginspace,wmode,allowscriptaccess,allowfullscreen,data(?:-[A-Za-z]+)+".split(",");
        M_evilValues = "javascript:,behavior:,vbscript:,mocha:,livescript:,expression".split(",");

        try {
            ClassLoader cl = ComponentManager.get(RESOURCE_CLASS).getClass().getClassLoader();
            resourceLoader = new ResourceLoader(RESOURCE_BUNDLE, cl);
            M_evilTags = getResourceLoader().getString("evilTags").split(",");
            M_goodTags = getResourceLoader().getString("goodTags").split(",");
            M_goodAttributes = getResourceLoader().getString("goodAttributes").split(",");
            M_evilValues = getResourceLoader().getString("evilValues").split(",");
        } catch (Exception e) {
            // this is a failure and cannot really be recovered from
            M_log.warn("Error collecting formattedtext.properties (using defaults instead) - if this is a test then everything is OK, if this is live then there is a problem BUT things will probably still work OK if you are english speaking");
        }

        M_evilTagsPatterns = new Pattern[M_evilTags.length];
        for (int i = 0; i < M_evilTags.length; i++)
        {
            // matches the start of the particular evil tag "<" followed by whitespace,
            // followed by the tag name, followed by anything, followed by ">", case insensitive,
            // allowed to match over multiple lines.
            M_evilTagsPatterns[i] = Pattern.compile(".*<\\s*" + M_evilTags[i] + ".*>.*", Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
        }

        M_goodTagsPatterns = new Pattern[M_goodTags.length];
        M_goodCloseTagsPatterns = new Pattern[M_goodTags.length];
        for (int i = 0; i < M_goodTags.length; i++)
        {
            // matches the start of the particular good tag "<" followed by whitespace,
            // followed by the tag name, followed by anything, followed by ">", case insensitive,
            // allowed to match over multiple lines.
            M_goodTagsPatterns[i] = Pattern.compile(".*<\\s*" + M_goodTags[i] + "(\\s+.*>|>|/>).*", Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
            M_goodCloseTagsPatterns[i] = Pattern.compile("<\\s*/\\s*" + M_goodTags[i] + "(\\s.*>|>)", Pattern.CASE_INSENSITIVE
                    | Pattern.UNICODE_CASE | Pattern.DOTALL);
        }

        M_goodAttributePatterns = new Pattern[M_goodAttributes.length];
        for (int i = 0; i < M_goodAttributes.length; i++) 
        {
            M_goodAttributePatterns[i] = Pattern.compile("(\\s+" + M_goodAttributes[i] + ")" +
                    "(?:(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))|(?=(\\s+|$)))", 
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        }

        M_evilValuePatterns = new Pattern[M_evilValues.length];
        String pads = "(\\s)*?(?:/\\*.*\\*/|<!--.*-->|\0|)*?(\\s)*?";
        for (int i = 0; i < M_evilValues.length; i++)
        {
            StringBuilder complexPattern = new StringBuilder();
            complexPattern.append("\\s*");
            String value = M_evilValues[i];
            for (int j = 0; j < value.length(); j++)
            {
                complexPattern.append(value.charAt(j));
                complexPattern.append(pads);
            }
            M_evilValuePatterns[i] = Pattern.compile(complexPattern.toString(), Pattern.CASE_INSENSITIVE | 
                    Pattern.UNICODE_CASE | Pattern.DOTALL);
        }

    }

    boolean defaultUseLegacyCleaner = false;
    /**
     * For TESTING
     * Sets the default - if not set, this will be "false"
     * @param defaultUseLegacyCleaner if true, use the old legacy security cleaner (no longer supported), if false use the new antisamy based cleaner
     */
    void setDefaultUseLegacyCleaner(boolean defaultUseLegacyCleaner) {
        this.defaultUseLegacyCleaner = defaultUseLegacyCleaner;
    }
    /**
     * Asks SCS for the value of the "content.cleaner.use.legacy.html", DEFAULT is false
     * @return true if the legacy HTML cleaner is used OR false use the antiSamy html cleaner
     */
    private boolean useLegacyCleaner() {
        boolean useLegacy = this.defaultUseLegacyCleaner;
        if (serverConfigurationService != null) { // this keeps the tests from dying
            useLegacy = serverConfigurationService.getBoolean("content.cleaner.use.legacy.html", useLegacy);
        }
        return useLegacy;
    }

    boolean defaultAddBlankTargetToLinks = true;
    /**
     * For TESTING
     * Sets the default - if not set, this will be "true"
     * @param addBlankTargetToLinks if we should add ' target="_blank" ' to all A tags which contain no target, false if they should not be touched
     */
    void setDefaultAddBlankTargetToLinks(boolean addBlankTargetToLinks) {
        this.defaultAddBlankTargetToLinks = addBlankTargetToLinks;
    }
    /**
     * Asks SCS for the value of the "content.cleaner.add.blank.target", DEFAULT is true (match legacy)
     * @return true if we should add ' target="_blank" ' to all A tags which contain no target, false if they should not be touched
     */
    private boolean addBlankTargetToLinks() {
        boolean add = defaultAddBlankTargetToLinks;
        if (serverConfigurationService != null) { // this keeps the tests from dying
            add = serverConfigurationService.getBoolean("content.cleaner.add.blank.target", defaultAddBlankTargetToLinks);
        }
        return add;
    }

    /**
     * Asks SCS for the value of the "content.cleaner.default.low.security", DEFAULT is false
     * @return true if low security is on be default for the scanner OR false to use high security scan (no unsafe embeds or objects)
     */
    private boolean defaultLowSecurity() {
        boolean defaultLowSecurity = false;
        if (serverConfigurationService != null) { // this keeps the tests from dying
            defaultLowSecurity = serverConfigurationService.getBoolean("content.cleaner.default.low.security", defaultLowSecurity);
        }
        return defaultLowSecurity;
    }

    /**
     * @return the path to the sakai home directory on the server
     */
    private String getSakaiHomeDir() {
        String sakaiHome = ""; // current dir (should be tomcat home) - this failsafe should not be used
        if (serverConfigurationService != null) { // this keeps the tests from dying
            String sh = serverConfigurationService.getSakaiHomePath();
            if (sh != null) {
                sakaiHome = sh;
            }
        }
        sakaiHome = new File(sakaiHome).getAbsolutePath(); // standardize
        return sakaiHome;
    }

    /** Matches HTML-style line breaks like &lt;br&gt; */
    private Pattern M_patternTagBr = Pattern.compile("<\\s*br\\s+?[^<>]*?>", Pattern.CASE_INSENSITIVE);

    /** Matches any HTML-style tag, like &lt;anything&gt; */
    private Pattern M_patternTag = Pattern.compile("<.*?>", Pattern.DOTALL);

    /** Matches the 3 main pieces of any HTML tag */
    private Pattern M_patternTagPieces = Pattern.compile("((?:<|</)\\w+)(\\s[^>]*?)?(/>|>)", Pattern.DOTALL);

    /** Matches newlines */
    private Pattern M_patternNewline = Pattern.compile("\\n");

    /**
     * Matches anchor tags so that a target can be added to them. This ensures that links in formatted text are forced to open up in a new window. This pattern matches as follows: "<a " followed by (string not containing characters "<>") possibly
     * followed by "target=something" (which is ignored), followed by "href" followed by (string not containing characters "<>") possibly followed by "target=something" (which is ignored), followed by ">". This should match all anchor tags that have an
     * href attribute. See escapeHtmlFormattedText()
     */
    private Pattern M_patternAnchorTag = Pattern.compile("([<]a\\s[^<>]*?)(\\s+href[^<>\\s]*=[^<>\\s]*?)?+([^<>]*?)[>]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** Matches all close anchor tags. */
    private Pattern M_patternCloseAnchorTag = Pattern.compile("[<]\\s[^<>]*?/\\s[^<>]*?a\\s[^<>]*?[>]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** Matches all anchor tags that have a target attribute. */
    public final Pattern M_patternAnchorTagWithTarget = Pattern.compile("([<]a\\s[^<>]*?)target=[^<>\\s]*([^<>]*?)[>]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /** Matches all anchor tags that do not have a target attribute. */
    public final Pattern M_patternAnchorTagWithOutTarget = 
        Pattern.compile("([<]a\\s)(?![^>]*target=)([^>]*?)[>]",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /** Matches href attribute */
    private Pattern M_patternHref = Pattern.compile("\\shref\\s*=\\s*(\".*?\"|'.*?')",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private Pattern M_patternHrefTarget = Pattern.compile("\\starget\\s*=\\s*(\".*?\"|'.*?')",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private Pattern M_patternHrefTitle = Pattern.compile("\\stitle\\s*=\\s*(\".*?\"|'.*?')",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuffer)
     */
    public String processFormattedText(final String strFromBrowser, StringBuffer errorMessages) {
        StringBuilder sb = new StringBuilder(errorMessages.toString());
        String fixed = processFormattedText(strFromBrowser, sb);
        errorMessages.setLength(0);
        errorMessages.append(sb.toString());
        return fixed;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuilder)
     */
    public String processFormattedText(final String strFromBrowser, StringBuilder errorMessages)
    {
        boolean checkForEvilTags = true;
        boolean replaceWhitespaceTags = true;
        return processFormattedText(strFromBrowser, errorMessages, null, checkForEvilTags, replaceWhitespaceTags, useLegacyCleaner());
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.api.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuilder, org.sakaiproject.util.api.FormattedText.Level)
     */
    public String processFormattedText(String strFromBrowser, StringBuilder errorMessages, Level level) {
        boolean checkForEvilTags = true;
        boolean replaceWhitespaceTags = true;
        return processFormattedText(strFromBrowser, errorMessages, level, checkForEvilTags, replaceWhitespaceTags, useLegacyCleaner());
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuilder, boolean)
     */
    public String processFormattedText(final String strFromBrowser,
            StringBuilder errorMessages, boolean useLegacySakaiCleaner) {
        boolean checkForEvilTags = true;
        boolean replaceWhitespaceTags = true;
        return processFormattedText(strFromBrowser, errorMessages, null, checkForEvilTags,
                replaceWhitespaceTags, useLegacySakaiCleaner);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processHtmlDocument(java.lang.String, java.lang.StringBuilder)
     */
    public String processHtmlDocument(final String strFromBrowser, StringBuilder errorMessages)
    {
        return strFromBrowser;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuilder, boolean, boolean)
     */
    public String processFormattedText(final String strFromBrowser, StringBuilder errorMessages, boolean checkForEvilTags,
            boolean replaceWhitespaceTags)
    {
        return processFormattedText(strFromBrowser, errorMessages, null, checkForEvilTags, replaceWhitespaceTags, useLegacyCleaner());
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.api.FormattedText#processFormattedText(java.lang.String, java.lang.StringBuilder, org.sakaiproject.util.api.FormattedText.Level, boolean, boolean, boolean)
     */
    public String processFormattedText(final String strFromBrowser, StringBuilder errorMessages, Level level,
            boolean checkForEvilTags, boolean replaceWhitespaceTags, boolean useLegacySakaiCleaner) {
        if (level == null || Level.DEFAULT.equals(level)) {
            // Select the default policy as high or low - KNL-1015
            level = defaultLowSecurity() ? Level.LOW : Level.HIGH; // default to system setting
        } else if (Level.NONE.equals(level)) {
            checkForEvilTags = false; // disable scan
        }
        if (!useLegacySakaiCleaner && antiSamyHigh == null) {
            useLegacySakaiCleaner = false;
            M_log.warn("Cannot use the new html cleaner (useLegacySakaiCleaner=false), Antisamy cleaner not available, falling back to the legacy cleaner");
        }

        String val = strFromBrowser;
        if (val == null || val.length() == 0) {
            return val;
        }

        try {
            if (replaceWhitespaceTags) {
                // normalize all variants of the "<br>" HTML tag to be "<br />\n"
                val = M_patternTagBr.matcher(val).replaceAll("<br />");

                // replace "<p>" with nothing. Replace "</p>" and "<p />" HTML tags with "<br />"
                // val = val.replaceAll("<p>", "");
                // val = val.replaceAll("</p>", "<br />\n");
                // val = val.replaceAll("<p />", "<br />\n");
            }

            if (checkForEvilTags) {
                if (useLegacySakaiCleaner || antiSamyHigh == null) {
                    val = processHtml(strFromBrowser, errorMessages);
                } else {
                    // use the owasp antisamy processor
                    AntiSamy as = antiSamyHigh;
                    if (Level.LOW.equals(level)) {
                        as = antiSamyLow;
                    }
                    try {
                        CleanResults cr = as.scan(strFromBrowser);
                        if (cr.getNumberOfErrors() > 0) {
                            // TODO currently no way to get internationalized versions of error messages
                            for (Object errorMsg : cr.getErrorMessages()) {
                                errorMessages.append(errorMsg.toString()+"\n\r");
                            }
                        }
                        val = cr.getCleanHTML();

                        // now replace all the A tags WITHOUT a target with _blank (to match the old functionality)
                        if (addBlankTargetToLinks() && StringUtils.isNotBlank(val)) {
                            Matcher m = M_patternAnchorTagWithOutTarget.matcher(val);
                            if (m.find()) {
                                val = m.replaceAll("$1$2 target=\"_blank\">"); // adds a target to A tags without one
                            }
                        }
                    } catch (ScanException e) {
                        // this will match the legacy behavior
                        val = "";
                        M_log.warn("processFormattedText: Failure during scan of input html: " + e, e);
                    } catch (PolicyException e) {
                        // this is an unrecoverable failure
                        throw new RuntimeException("Unable to access the antiSamy policy file: "+e, e);
                    }
                }
            }

            // deal with hardcoded empty space character from Firefox 1.5
            if (val.equalsIgnoreCase("&nbsp;")) {
                val = "";
            }

            if (useLegacySakaiCleaner || antiSamyHigh == null) {
                // close any open HTML tags (that the user may have accidentally left open)
                StringBuilder buf = new StringBuilder();
                trimFormattedText(val, Integer.MAX_VALUE, buf);
                val = buf.toString();
            }
        } catch (Exception e) {
            // We catch all exceptions here because doing so will usually give the user the
            // opportunity to work around the issue, rather than causing a tool stack trace

            M_log.warn("Unexpected error processing text", e);
            errorMessages.append(getResourceLoader().getString("unknown_error_markup"));
            val = null;
        }

        return val;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#escapeHtmlFormattedText(java.lang.String)
     */
    public String escapeHtmlFormattedText(String value)
    {
        boolean supressNewlines = false;
        return escapeHtmlFormattedText(value, supressNewlines);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#escapeHtmlFormattedTextSupressNewlines(java.lang.String)
     */
    public String escapeHtmlFormattedTextSupressNewlines(String value)
    {
        boolean supressNewlines = true;
        return escapeHtmlFormattedText(value, supressNewlines);
    }

    /**
     * Prepares the given HTML formatted text for output as part of an HTML document. Makes sure that links open up in a new window by setting 'target="_blank"' on anchor tags.
     * 
     * @param value
     *        The formatted text to escape
     * @param supressNewlines
     *        If true, remove newlines ("&lt;br /&gt;") when escaping.
     * @return The string to include in an HTML document.
     */
    private String escapeHtmlFormattedText(String value, boolean supressNewlines)
    {
        if (value == null) return "";
        if (value.length() == 0) return "";

        if (supressNewlines)
        {
            // zap HTML line breaks ("<br />") into plain-old whitespace
            value = M_patternTagBr.matcher(value).replaceAll(" ");
        }

        // make sure that links open up in a new window. This
        // makes sure every anchor tag has a blank target.
        // for example:
        // <a href="http://www.microsoft.com">Microsoft</a>
        // becomes:
        // <a href="http://www.microsoft.com" target="_blank">Microsoft</a>
        // removed for KNL-526
        //value = M_patternAnchorTagWithTarget.matcher(value).replaceAll("$1$2>"); // strips out targets
        //value = M_patternAnchorTag.matcher(value).replaceAll("$1$2$3 target=\"_blank\">"); // adds in blank targets
        // added for KNL-526

        if (addBlankTargetToLinks()) {
            Matcher m = M_patternAnchorTagWithOutTarget.matcher(value);
            if (m.find()) {
                value = m.replaceAll("$1$2 target=\"_blank\">"); // adds a target to A tags without one
            }
        }

        return value;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#escapeHtmlFormattedTextarea(java.lang.String)
     */
    public String escapeHtmlFormattedTextarea(String value)
    {
        return escapeHtml(value, false);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#convertPlaintextToFormattedText(java.lang.String)
     */
    public String convertPlaintextToFormattedText(String value)
    {
        return escapeHtml(value, true);
    }

    private final boolean LAZY_CONSTRUCTION = true;

    /* (non-Javadoc)
     * @see org.sakaiproject.util.api.FormattedText#escapeHtml(java.lang.String)
     */
    public String escapeHtml(String value) {
        return escapeHtml(value, true);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#escapeHtml(java.lang.String, boolean)
     */
    public String escapeHtml(String value, boolean escapeNewlines) {
        /*
         * Velocity tools depend on this returning empty string (and never null),
         * they also depend on this handling a null input and converting it to null
         */
        String val = "";
        if (value != null && !"".equals(value)) {
            val = StringEscapeUtils.escapeHtml(value);
            if (escapeNewlines && val != null) {
                val = val.replace("\n", "<br/>\n");
            }
        }
        return val;
    } // escapeHtml

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#encodeFormattedTextAttribute(org.w3c.dom.Element, java.lang.String, java.lang.String)
     */
    public void encodeFormattedTextAttribute(Element element, String baseAttributeName, String value)
    {
        // store the formatted text in an attribute called baseAttributeName-html
        Xml.encodeAttribute(element, baseAttributeName + "-html", value);

        // Store the non-formatted (plaintext) version as well
        Xml.encodeAttribute(element, baseAttributeName, convertFormattedTextToPlaintext(value));
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#encodeUnicode(java.lang.String)
     */
    public String encodeUnicode(String value)
    {
        // TODO call method in each process routine
        if (value == null) return "";

        try
        {
            // lazily allocate the StringBuilder
            // only if changes are actually made; otherwise
            // just return the given string without changing it.
            StringBuilder buf = (LAZY_CONSTRUCTION) ? null : new StringBuilder();
            final int len = value.length();
            for (int i = 0; i < len; i++)
            {
                char c = value.charAt(i);
                if (c < 128)
                {
                    if (buf != null) buf.append(c);
                }
                else
                {
                    // escape higher Unicode characters using an
                    // HTML numeric character entity reference like "&#15672;"
                    if (buf == null) buf = new StringBuilder(value.substring(0, i));
                    buf.append("&#");
                    buf.append(Integer.toString((int) c));
                    buf.append(";");
                }
            } // for

            return (buf == null) ? value : buf.toString();
        }
        catch (Exception e)
        {
            M_log.warn("Validator.escapeHtml: ", e);
            return "";
        }
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#unEscapeHtml(java.lang.String)
     */
    public String unEscapeHtml(String value)
    {
        if (value == null || value.equals("")) return "";
        value = value.replaceAll("&lt;", "<");
        value = value.replaceAll("&gt;", ">");
        value = value.replaceAll("&amp;", "&");
        value = value.replaceAll("&quot;", "\"");
        return value;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processAnchor(java.lang.String)
     */
    public String processAnchor(String anchor) {
        String newAnchor = "";
        String href = null;
        String hrefTarget = null;
        String hrefTitle = null;

        try {
            // get HREF value
            Matcher matcher = M_patternHref.matcher(anchor);
            if (matcher.find()) {
                href = matcher.group();
            }
            // get target value
            matcher = M_patternHrefTarget.matcher(anchor);
            if (matcher.find()) {
                hrefTarget = matcher.group();
            }
            // get title value
            matcher = M_patternHrefTitle.matcher(anchor);
            if (matcher.find()) {
                hrefTitle = matcher.group();
            }
        } catch (Exception e) {
            M_log.warn("FormattedText.processAnchor ", e);
        }

        if (hrefTarget != null) {
            // use the existing one
            hrefTarget = hrefTarget.trim();
            hrefTarget = hrefTarget.replaceAll("\"", ""); // slightly paranoid
            hrefTarget = hrefTarget.replaceAll(">", ""); // slightly paranoid
            hrefTarget = hrefTarget.replaceFirst("target=", ""); // slightly paranoid
            hrefTarget = " target=\"" + hrefTarget + "\"";
        } else {
            // default to _blank if not set and configured to force
            if (addBlankTargetToLinks()) {
                hrefTarget = " target=\"_blank\"";
            }
        }

        if (hrefTitle != null) {
            // use the existing one
            hrefTitle = hrefTitle.trim();
            hrefTitle = hrefTitle.replaceAll("\"", ""); // slightly paranoid
            hrefTitle = hrefTitle.replaceAll(">", ""); // slightly paranoid
            hrefTitle = hrefTitle.replaceFirst("title=", ""); // slightly paranoid
            hrefTitle = " title=\"" + hrefTitle + "\"";
        }

        // open in a new window
        if (href != null) {
            href = href.replaceAll("\"", "");
            href = href.replaceAll(">", "");
            href = href.replaceFirst("href=", "href=\"");
            newAnchor = "<a " + href + "\"" + hrefTarget;
            if (hrefTitle != null)
            {
                newAnchor += hrefTitle;
            }
            newAnchor += ">";
        } else {
            M_log.debug("FormattedText.processAnchor href == null");
            newAnchor = anchor; // default to the original one so we don't lose the anchor
        }
        return newAnchor;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#processEscapedHtml(java.lang.String)
     */
    public String processEscapedHtml(final String source)
    {
        if (source == null) return "";
        if (source.equals("")) return "";

        String Html = null;
        try
        {
            // TODO call encodeUnicode in other process routine
            Html = encodeUnicode(source);
        }
        catch (Exception e)
        {
            M_log.warn("FormattedText.processEscapedHtml encodeUnicode(source):", e);
        }
        try
        {
            // to use the FormattedText functions
            Html = unEscapeHtml(Html);
        }
        catch (Exception e)
        {
            M_log.warn("FormattedText.processEscapedHtml unEscapeHtml(Html):", e);
        }

        return processHtml(Html, new StringBuilder());
    }

    private String processHtml(final String source, StringBuilder errorMessages)
    {
        if ( M_evilTags == null )
            init();

        // normalize all variants of the "<br>" HTML tag to be "<br />\n"
        // TODO call a method to do this in each process routine
        String Html = M_patternTagBr.matcher(source).replaceAll("<br />");

        // process text and tags
        StringBuilder buf = new StringBuilder();
        if (Html != null)
        {
            try
            {
                int start = 0;
                Matcher m = M_patternTag.matcher(Html);

                // if there are no tags, return as is
                if (!m.find()) return Html;
                m.reset(Html);

                // if there are tags, make sure they are safe
                while (m.find())
                {
                    // append text that isn't part of a tag
                    if (m.start() > start) buf.append(Html.substring(start, m.start()));
                    start = m.end();

                    buf.append(checkTag(m.group(), errorMessages));
                }

                // tail
                if (Html.length() > start) buf.append(Html.substring((start)));
            }
            catch (Exception e)
            {
                M_log.warn("FormattedText.processEscapedHtml M_patternTag.matcher(Html):", e);
            }
        }
        return String.valueOf(buf.toString());
    }

    private String checkTag (final String tag, StringBuilder errorMessages)
    {
        if ( M_goodTags == null )
            init();

        StringBuilder buf = new StringBuilder();
        boolean escape = true;

        // Fix up tags that look like: <<<tag>
        String realTag = tag;
        while (realTag.startsWith("<<")) {
            // chop off one char until we get down to a real tag
            realTag = realTag.substring(1);
        }

        // if it's a good open tag, don't escape the HTML
        for (int i = 0; i < M_goodTags.length; i++)
        {
            if (M_goodTagsPatterns[i].matcher(realTag).matches())
            {
                if (M_patternAnchorTag.matcher(realTag).matches()
                        && !M_patternCloseAnchorTag.matcher(realTag).matches())
                {
                    // if it's an anchor tag, sanitize it
                    buf.append(checkAttributes(processAnchor(realTag), errorMessages));
                    escape = false;
                }
                else
                {
                    // otherwise just include it
                    buf.append(checkAttributes(realTag, errorMessages));
                    escape = false;
                }
            }
            else if (M_goodCloseTagsPatterns[i].matcher(realTag).matches())
            {
                // if it's a good close tag, don't escape the HTML
                buf.append(checkAttributes(realTag, errorMessages));
                escape = false;
            }
        }

        // otherwise escape tag
        if (escape)
        {
            buf.append((String) escapeHtml(tag, false));

            Matcher fullTag = M_patternTagPieces.matcher(realTag);
            if (fullTag.matches() && fullTag.groupCount() > 2)
            {
                errorMessages.append(getResourceLoader().getFormattedMessage("html_tag_is_not_allowed", new Object[]{fullTag.group(1) + fullTag.group(fullTag.groupCount())}));
            }
        }

        return buf.toString();
    }

    private String checkAttributes(final String tag, StringBuilder errorMessages)
    {
        if ( M_goodAttributes == null )
            init();

        Matcher fullTag = M_patternTagPieces.matcher(tag);
        String close = "";
        StringBuilder buf = new StringBuilder();
        String leftOvers = "";

        String tagOpen;
        String tagName;
        if (fullTag.matches() && fullTag.groupCount() > 2)
        {
            tagOpen = fullTag.group(1);
            leftOvers = fullTag.group(2);
            close = fullTag.group(fullTag.groupCount());
            tagName = tagOpen.substring(1); 
            buf.append(tagOpen);
        }
        else
        {
            if (M_log.isDebugEnabled()) M_log.debug("Could not parse " + tag);
            return "";
        }
        Matcher matcher;
        HashSet<String> usedAttributes = new HashSet<String>();
        if (leftOvers != null)
        {
            for (int i = 0; i < M_goodAttributePatterns.length; i++)
            {
                matcher = M_goodAttributePatterns[i].matcher(leftOvers);
                while (matcher.find())
                {
                    String attr = matcher.group(1);
                    String cleanedAttr = attr.trim().toLowerCase();
                    // Disallow multiple uses of the same attribute on an element.
                    // This functionality was implicit in the one-shot match before
                    // changing this to a find() loop.
                    if (usedAttributes.contains(cleanedAttr)) {
                        errorMessages.append(getResourceLoader().getFormattedMessage("html_duplicate_attribute_not_allowed", new Object[]{attr.trim()}));
                        continue;
                    }
                    usedAttributes.add(cleanedAttr);
                    buf.append(attr);

                    // We were checking [0..1) (the entire attribute match only)
                    // in a pseudo-loop, since there was only one capturing group.
                    // This is the simpler form of the same behavior and allows
                    // us to have a capturing group for the attribute name above.
                    if (matcher.groupCount() < 3) {
                        M_log.warn("Attribute cleaning failed; regex improperly modified.");
                        break;
                    }
                    // Group 2 is for regular attribute values, including = and quotes
                    // Group 3 is for valueless attributes, capturing either whitespace or tag-end
                    // These are mutually exclusive and one is guaranteed because of the regex construction
                    String value = matcher.group(2);
                    if (matcher.group(3) != null) {
                        // Discard the look-ahead whitespace since it will be included by the next
                        // attribute if there is one, or stripped if this is the end of the tag.
                        value = "";
                    }

                    if (checkValue(tagName, cleanedAttr, value, errorMessages))
                    {
                        buf.append(value);

                        try {
                            leftOvers = leftOvers.replace(attr + value, "");
                        }
                        catch (Exception e)
                        {
                            M_log.warn(attr + value);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (leftOvers != null && leftOvers.trim().length() > 1)
        {
            errorMessages.append(getResourceLoader().getFormattedMessage("html_attribute_pattern_not_allowed", new Object[]{leftOvers}));
        }

        buf.append(close);
        return buf.toString();
    }

    private boolean checkValue(final String tag, final String attr, final String value, StringBuilder errorMessages)
    {
        if ( M_evilValues == null )
            init();

        boolean pass = true;
        Matcher matcher;
        for (int i = 0; i < M_evilValuePatterns.length; i++)
        {
            matcher = M_evilValuePatterns[i].matcher(value);

            if (matcher.find())
            {
                pass = false;
                //errorMessages.append("The attribute value '" + value + "' is not allowed\n");
            }
        }
        if (pass) {
            // Special check for src="data:image/svg+xml;base64,.... : http://jira.sakaiproject.org/browse/SAK-18269
            if ("embed".equalsIgnoreCase(tag) 
                    && attr.equalsIgnoreCase("src")
                    && value != null 
                    && value.toLowerCase().indexOf("data:image/svg") > 0
            ) {
                int pos = value.toLowerCase().indexOf(";base64,");
                if (pos > 0) {
                    pos = pos + 8;
                    String b64text = value.substring(pos);
                    if (! StringUtils.isBlank(b64text)) {
                        String content = new String(Base64.decodeBase64(b64text));
                        if (! StringUtils.isBlank(content)) {
                            boolean foundEvil = false;
                            for (int i = 0; i < M_evilTags.length; i++) {
                                if (M_evilTagsPatterns[i].matcher(content).matches()) {
                                    foundEvil = true;
                                    break;
                                }
                            }
                            if (foundEvil) {
                                //System.err.println("AZ: tag="+tag+",content="+content);
                                errorMessages.append(getResourceLoader().getFormattedMessage("embed_tag_contains_dangerous_content", new Object[]{content}));
                                pass = false;
                            }
                        }
                    }
                }
            }
        }
        return pass;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#decodeFormattedTextAttribute(org.w3c.dom.Element, java.lang.String)
     */
    public String decodeFormattedTextAttribute(Element element, String baseAttributeName)
    {
        String ret;

        // first check if an HTML-encoded attribute exists, for example "foo-html", and use it if available
        ret = StringUtils.trimToNull(Xml.decodeAttribute(element, baseAttributeName + "-html"));
        if (ret != null) return ret;

        // next try the older kind of formatted text like "foo-formatted", and convert it if found
        ret = StringUtils.trimToNull(Xml.decodeAttribute(element, baseAttributeName + "-formatted"));
        ret = convertOldFormattedText(ret);
        if (ret != null) return ret;

        // next try just a plaintext attribute and convert the plaintext to formatted text if found
        // convert from old plaintext instructions to new formatted text instruction
        ret = Xml.decodeAttribute(element, baseAttributeName);
        ret = convertPlaintextToFormattedText(ret);
        return ret;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#convertFormattedTextToPlaintext(java.lang.String)
     */
    public String convertFormattedTextToPlaintext(String value)
    {
        if (value == null) return null;
        if (value.length() == 0) return "";

        // strip out newlines
        value = M_patternNewline.matcher(value).replaceAll("");

        // convert "<br />" to newline
        value = M_patternTagBr.matcher(value).replaceAll("\n");

        // strip out all the HTML-style tags so that:
        // <font face="verdana">Something</font> <b>Something else</b>
        // becomes:
        // Something Something else
        value = M_patternTag.matcher(value).replaceAll("");

        // Replace HTML character entity references (like &gt;)
        // with the plain Unicode characters to which they refer.
        String ref;
        char val;
        for (int i = 0; i < M_htmlCharacterEntityReferences.length; i++)
        {
            ref = M_htmlCharacterEntityReferences[i];
            if (value.indexOf(ref) >= 0)
            {
                val = M_htmlCharacterEntityReferencesUnicode[i];
                // System.out.println("REPLACING "+ref+" WITH UNICODE CHARACTER #"+val+" WHICH IN JAVA IS "+Character.toString(val));
                value = value.replaceAll(ref, Character.toString(val));
            }
        }

        // Replace HTML numeric character entity references (like &#nnnn; or &#xnnnn;)
        // with the plain Unicode characters to which they refer.
        value = decodeNumericCharacterReferences(value);

        return value;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#convertOldFormattedText(java.lang.String)
     */
    public String convertOldFormattedText(String value)
    {
        // previously, formatted text used "\n" to indicate a line break.
        // now we use "<br />\n" as a line break. This code converts old
        // formatted text to new formatted text.
        if (value == null) return null;
        if (value.length() == 0) return "";

        value = M_patternNewline.matcher(value).replaceAll("<br />\n");
        return value;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#trimFormattedText(java.lang.String, int, java.lang.StringBuilder)
     */
    public boolean trimFormattedText(String formattedText, final int maxNumOfChars, StringBuilder strTrimmed)
    {
        // This should return a formatted text substring which contains formatting, but which
        // isn't broken in the middle of formatting, eg, "<strong>Hi there</stro" It also shouldn't
        // break HTML character entities such as "&gt;".

        String str = formattedText;
        strTrimmed.setLength(0);
        strTrimmed.append(str);
        if (str == null) return false;

        int count = 0; // number of displayed characters seen so far
        int pos = 0; // raw position within the formatted text string
        int len = str.length();
        Stack<String> tags = new Stack<String>(); // currently open tags (may need to be closed at the end)
        while (pos < len && count < maxNumOfChars)
        {
            while (pos < len && str.charAt(pos) == '<')
            {
                // currently parsing a tag
                pos++;

                if (pos < len && str.charAt(pos) == '!')
                {
                    // parsing an HTML comment
                    if (pos + 2 < len)
                    {
                        if (str.charAt(pos + 1) == '-' && str.charAt(pos + 2) == '-')
                        {
                            // skip past the close of the comment tag
                            int close = str.indexOf("-->", pos);
                            if (close != -1)
                            {
                                pos = close + 3;
                                continue;
                            }
                        }
                    }
                }

                if (pos < len && str.charAt(pos) == '/')
                {
                    // currently parsing an closing tag
                    if (!tags.isEmpty()) tags.pop();
                    while (pos < len && str.charAt(pos) != '>')
                        pos++;
                    pos++;
                    continue;
                }
                // capture the name of the opening tag and put it on the stack of open tags
                int taglen = 0;
                String tag;
                while (pos < len && str.charAt(pos) != '>' && !Character.isWhitespace(str.charAt(pos)))
                {
                    pos++;
                    taglen++;
                }
                tag = str.substring(pos - taglen, pos);
                tags.push(tag);

                while (pos < len && str.charAt(pos) != '>')
                    pos++;

                if (tag.length() == 0)
                {
                    if (!tags.isEmpty()) tags.pop();
                    continue;
                }
                if (str.charAt(pos - 1) == '/') if (!tags.isEmpty()) tags.pop(); // singleton tag like "<br />" has no closing tag
                if (tag.charAt(0) == '!') if (!tags.isEmpty()) tags.pop(); // comment tag like "<!-- comment -->", so just ignore it
                if ("br".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
                if ("hr".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
                if ("meta".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
                if ("link".equalsIgnoreCase(tag)) if (!tags.isEmpty()) tags.pop();
                pos++;
            }

            if (pos < len && str.charAt(pos) == '&')
            {
                // HTML character entity references, like "&gt;"
                // count this as one single character
                while (pos < len && str.charAt(pos) != ';')
                    pos++;
            }

            if (pos < len)
            {
                count++;
                pos++;
            }
        }

        // close any unclosed tags
        strTrimmed.setLength(0);
        strTrimmed.append(str.substring(0, pos));
        while (tags.size() > 0)
        {
            strTrimmed.append("</");
            strTrimmed.append(tags.pop());
            strTrimmed.append(">");
        }

        boolean didTrim = (count == maxNumOfChars);
        return didTrim;

    } // trimFormattedText()

    /* (non-Javadoc)
     * @see org.sakaiproject.utils.impl.FormattedText#decodeNumericCharacterReferences(java.lang.String)
     */
    public String decodeNumericCharacterReferences(String value)
    {
        // lazily allocate StringBuilder only if needed
        // buf is not null ONLY when a numeric character reference
        // is found - otherwise, buf is not used at all
        StringBuilder buf = null;
        final int valuelength = value.length();
        for (int i = 0; i < valuelength; i++)
        {
            if ((value.charAt(i) == '&' || value.charAt(i) == '^') && (i + 2 < valuelength)
                    && (value.charAt(i + 1) == '#' || value.charAt(i + 1) == '^'))
            {
                int pos = i + 2;
                boolean hex = false;
                if ((value.charAt(pos) == 'x') || (value.charAt(pos) == 'X'))
                {
                    pos++;
                    hex = true;
                }
                StringBuilder num = new StringBuilder(6);
                while (pos < valuelength && value.charAt(pos) != ';' && value.charAt(pos) != '^')
                {
                    num.append(value.charAt(pos));
                    pos++;
                }
                if (pos < valuelength)
                {
                    try
                    {
                        int val = Integer.parseInt(num.toString(), (hex ? 16 : 10));
                        // Found an HTML numeric character reference!
                        if (buf == null)
                        {
                            buf = new StringBuilder();
                            buf.append(value.substring(0, i));
                        }

                        buf.append((char) val);
                        i = pos;
                    }
                    catch (Exception ignore)
                    {
                        if (buf != null) buf.append(value.charAt(i));
                    }
                }
                else
                {
                    if (buf != null) buf.append(value.charAt(i));
                }
            }
            else
            {
                if (buf != null) buf.append(value.charAt(i));
            }
        }

        if (buf != null) value = buf.toString();

        return value;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.util.api.FormattedText#encodeUrlsAsHtml(java.lang.String)
     */
    public String encodeUrlsAsHtml(String text) {
        // MOVED FROM Web
        Pattern p = Pattern.compile("(?<!href=['\"]{1})(((https?|s?ftp|ftps|file|smb|afp|nfs|(x-)?man|gopher|txmt)://|mailto:)[-:;@a-zA-Z0-9_.,~%+/?=&#]+(?<![.,?:]))");
        Matcher m = p.matcher(text);
        StringBuffer buf = new StringBuffer();
        while(m.find()) {
            String matchedUrl = m.group();
            m.appendReplacement(buf, "<a href=\"" + unEscapeHtml(matchedUrl) + "\">$1</a>");
        }
        m.appendTail(buf);
        return buf.toString();
    }

    public String escapeJavascript(String value) {
        if (value == null || "".equals(value)) return "";
        try
        {
            StringBuilder buf = new StringBuilder();

            // prepend 'i' if first character is not a letter
            if (!java.lang.Character.isLetter(value.charAt(0)))
            {
                buf.append("i");
            }

            // change non-alphanumeric characters to 'x'
            for (int i = 0; i < value.length(); i++)
            {
                char c = value.charAt(i);
                if (!java.lang.Character.isLetterOrDigit(c))
                {
                    buf.append("x");
                }
                else
                {
                    buf.append(c);
                }
            }

            String rv = buf.toString();
            return rv;
        }
        catch (Exception e)
        {
            M_log.warn("escapeJavascript: ", e);
            return value;
        }
    }

    public String escapeJsQuoted(String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /** These characters are escaped when making a URL */
    // protected static final String ESCAPE_URL = "#%?&='\"+ ";
    // not '/' as that is assumed to be part of the path
    protected static final String ESCAPE_URL = "$&+,:;=?@ '\"<>#%{}|\\^~[]`";

    /**
     * These can't be encoded in URLs safely even using %nn notation, so encode them using our own custom URL encoding
     */
    protected static final String ESCAPE_URL_SPECIAL = "^?;";

    public String escapeUrl(String id) {
        if (id == null) return "";
        id = id.trim();
        try
        {
            // convert the string to bytes in UTF-8
            byte[] bytes = id.getBytes("UTF-8");

            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)
            {
                byte b = bytes[i];
                // escape ascii control characters, ascii high bits, specials
                if (ESCAPE_URL_SPECIAL.indexOf((char) b) != -1)
                {
                    buf.append("^^x"); // special funky way to encode bad URL characters 
                    buf.append(toHex(b));
                    buf.append('^');
                }
                // 0x1F is the last control character
                // 0x7F is DEL chatecter
                // 0x80 is the start of the top of the 256bit set.
                else if ((ESCAPE_URL.indexOf((char) b) != -1) || (b <= 0x1F) || (b == 0x7F) || (b >= 0x80))
                {
                    buf.append("%");
                    buf.append(toHex(b));
                }
                else
                {
                    buf.append((char) b);
                }
            }

            String rv = buf.toString();
            return rv;
        }
        catch (UnsupportedEncodingException e)
        {
            M_log.warn("Validator.escapeUrl: ", e);
            return "";
        }
    }

    // PRIVATE STUFF

    /**
     * Returns a hex representation of a byte.
     * 
     * @param b
     *        The byte to convert to hex.
     * @return The 2-digit hex value of the supplied byte.
     */
    protected static final String toHex(byte b) {
        char ret[] = new char[2];

        ret[0] = hexDigit((b >>> 4) & (byte) 0x0F);
        ret[1] = hexDigit((b >>> 0) & (byte) 0x0F);

        return new String(ret);
    }

    /**
     * Returns the hex digit corresponding to a number between 0 and 15.
     * 
     * @param i
     *        The number to get the hex digit for.
     * @return The hex digit corresponding to that number.
     * @exception java.lang.IllegalArgumentException
     *            If supplied digit is not between 0 and 15 inclusive.
     */
    protected static final char hexDigit(int i)
    {
        switch (i)
        {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
                return 'F';
        }

        throw new IllegalArgumentException("Invalid digit:" + i);
    }

    /**
     * HTML character entity references. These abbreviations are used in HTML to escape certain Unicode characters, including characters used in HTML markup. These character entity references were taken directly from the HTML 4.0 specification at:
     * 
     * http://www.w3.org/TR/REC-html40/sgml/entities.html
     */
    private final String[] M_htmlCharacterEntityReferences = { "&nbsp;", "&iexcl;", "&cent;", "&pound;", "&curren;",
            "&yen;", "&brvbar;", "&sect;", "&uml;", "&copy;", "&ordf;", "&laquo;", "&not;", "&shy;", "&reg;", "&macr;", "&deg;",
            "&plusmn;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;", "&middot;", "&cedil;", "&sup1;", "&ordm;", "&raquo;",
            "&frac14;", "&frac12;", "&frac34;", "&iquest;", "&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;",
            "&AElig;", "&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;", "&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;",
            "&ETH;", "&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "&times;", "&Oslash;", "&Ugrave;",
            "&Uacute;", "&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;", "&szlig;", "&agrave;", "&aacute;", "&acirc;", "&atilde;",
            "&auml;", "&aring;", "&aelig;", "&ccedil;", "&egrave;", "&eacute;", "&ecirc;", "&euml;", "&igrave;", "&iacute;",
            "&icirc;", "&iuml;", "&eth;", "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;", "&ouml;", "&divide;",
            "&oslash;", "&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;", "&thorn;", "&yuml;", "&fnof;", "&Alpha;",
            "&Beta;", "&Gamma;", "&Delta;", "&Epsilo;", "&Zeta;", "&Eta;", "&Theta;", "&Iota;", "&Kappa;", "&Lambda;", "&Mu;",
            "&Nu;", "&Xi;", "&Omicro;", "&Pi;", "&Rho;", "&Sigma;", "&Tau;", "&Upsilo;", "&Phi;", "&Chi;", "&Psi;", "&Omega;",
            "&alpha;", "&beta;", "&gamma;", "&delta;", "&epsilo;", "&zeta;", "&eta;", "&theta;", "&iota;", "&kappa;", "&lambda;",
            "&mu;", "&nu;", "&xi;", "&omicro;", "&pi;", "&rho;", "&sigmaf;", "&sigma;", "&tau;", "&upsilo;", "&phi;", "&chi;",
            "&psi;", "&omega;", "&thetas;", "&upsih;", "&piv;", "&bull;", "&hellip;", "&prime;", "&Prime;", "&oline;", "&frasl;",
            "&weierp;", "&image;", "&real;", "&trade;", "&alefsy;", "&larr;", "&uarr;", "&rarr;", "&darr;", "&harr;", "&crarr;",
            "&lArr;", "&uArr;", "&rArr;", "&dArr;", "&hArr;", "&forall;", "&part;", "&exist;", "&empty;", "&nabla;", "&isin;",
            "&notin;", "&ni;", "&prod;", "&sum;", "&minus;", "&lowast;", "&radic;", "&prop;", "&infin;", "&ang;", "&and;", "&or;",
            "&cap;", "&cup;", "&int;", "&there4;", "&sim;", "&cong;", "&asymp;", "&ne;", "&equiv;", "&le;", "&ge;", "&sub;",
            "&sup;", "&nsub;", "&sube;", "&supe;", "&oplus;", "&otimes;", "&perp;", "&sdot;", "&lceil;", "&rceil;", "&lfloor;",
            "&rfloor;", "&lang;", "&rang;", "&loz;", "&spades;", "&clubs;", "&hearts;", "&diams;", "&quot;", "&amp;", "&lt;",
            "&gt;", "&OElig;", "&oelig;", "&Scaron;", "&scaron;", "&Yuml;", "&circ;", "&tilde;", "&ensp;", "&emsp;", "&thinsp;",
            "&zwnj;", "&zwj;", "&lrm;", "&rlm;", "&ndash;", "&mdash;", "&lsquo;", "&rsquo;", "&sbquo;", "&ldquo;", "&rdquo;",
            "&bdquo;", "&dagger;", "&Dagger;", "&permil;", "&lsaquo;", "&rsaquo;", "&euro;" };

    /**
     * These character entity references were taken directly from the HTML 4.0 specification at:
     * 
     * http://www.w3.org/TR/REC-html40/sgml/entities.html
     */
    private final char[] M_htmlCharacterEntityReferencesUnicode = { 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170,
            171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194,
            195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218,
            219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242,
            243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 402, 913, 914, 915, 916, 917, 918, 919, 920, 921, 922,
            923, 924, 925, 926, 927, 928, 929, 931, 932, 933, 934, 935, 936, 937, 945, 946, 947, 948, 949, 950, 951, 952, 953, 954,
            955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 977, 978, 982, 8226, 8230, 8242, 8243, 8254,
            8260, 8472, 8465, 8476, 8482, 8501, 8592, 8593, 8594, 8595, 8596, 8629, 8656, 8657, 8658, 8659, 8660, 8704, 8706, 8707,
            8709, 8711, 8712, 8713, 8715, 8719, 8721, 8722, 8727, 8730, 8733, 8734, 8736, 8743, 8744, 8745, 8746, 8747, 8756, 8764,
            8773, 8776, 8800, 8801, 8804, 8805, 8834, 8835, 8836, 8838, 8839, 8853, 8855, 8869, 8901, 8968, 8969, 8970, 8971, 9001,
            9002, 9674, 9824, 9827, 9829, 9830, 34, 38, 60, 62, 338, 339, 352, 353, 376, 710, 732, 8194, 8195, 8201, 8204, 8205,
            8206, 8207, 8211, 8212, 8216, 8217, 8218, 8220, 8221, 8222, 8224, 8225, 8240, 8249, 8250, 8364 };

    private String[] M_goodTags = null;
    private String[] M_goodAttributes = null;
    private String[] M_evilValues =  null;
    private String[] M_evilTags = null;

    private ResourceLoader getResourceLoader() {
        if (resourceLoader == null) {
            resourceLoader = new ResourceLoader(RESOURCE_BUNDLE);
        }
        return resourceLoader;
    }

}
