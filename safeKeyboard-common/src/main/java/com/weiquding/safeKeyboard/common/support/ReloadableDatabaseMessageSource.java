package com.weiquding.safeKeyboard.common.support;

import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 可热更新的数据库资源束实现
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
public class ReloadableDatabaseMessageSource extends AbstractMessageSource {

    private boolean fallbackToSystemLocale = true;

    private long cacheMillis = -1;


    /**
     * Set whether to fall back to the system Locale if no files for a specific
     * Locale have been found. Default is "true"; if this is turned off, the only
     * fallback will be the default file (e.g. "messages.properties" for
     * basename "messages").
     * <p>Falling back to the system Locale is the default behavior of
     * {@code java.util.ResourceBundle}. However, this is often not desirable
     * in an application server environment, where the system Locale is not relevant
     * to the application at all: set this flag to "false" in such a scenario.
     */
    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    /**
     * Return whether to fall back to the system Locale if no files for a specific
     * Locale have been found.
     *
     * @since 4.3
     */
    protected boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    /**
     * Set the number of seconds to cache loaded properties files.
     * Default is "-1", indicating to cache forever
     */
    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = (cacheSeconds * 1000);
    }

    /**
     * Set the number of milliseconds to cache loaded properties files.
     * Note that it is common to set seconds instead: {@link #setCacheSeconds}.
     * <ul>
     * <li>Default is "-1", indicating to cache forever (just like
     * {@code java.util.ResourceBundle}).
     * <li>A positive number will cache loaded properties files for the given
     * number of milliseconds. This is essentially the interval between refresh checks.
     * Note that a refresh attempt will first check the last-modified timestamp
     * of the file before actually reloading it; so if files don't change, this
     * interval can be set rather low, as refresh attempts will not actually reload.
     * <li>A value of "0" will check the last-modified timestamp of the file on
     * every message access. <b>Do not use this in a production environment!</b>
     * </ul>
     *
     * @see #setCacheSeconds
     * @since 4.3
     */
    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    /**
     * Return the number of milliseconds to cache loaded properties files.
     *
     * @since 4.3
     */
    protected long getCacheMillis() {
        return this.cacheMillis;
    }



    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return null;
    }
}
