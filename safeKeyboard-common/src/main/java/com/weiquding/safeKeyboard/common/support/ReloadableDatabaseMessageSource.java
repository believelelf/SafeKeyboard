package com.weiquding.safeKeyboard.common.support;

import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可热更新的数据库资源束实现，basename-->tableName
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/19
 */
public class ReloadableDatabaseMessageSource extends AbstractResourceBasedMessageSource {

    /**
     * Cache to hold table param lists per Locale
     */
    private final ConcurrentMap<String, Map<Locale, List<Param>>> cachedTableParams = new ConcurrentHashMap<>();


    /**
     * Cache to hold already loaded table per table name and locale
     */
    private final ConcurrentMap<Param, TableHolder> cachedTable = new ConcurrentHashMap<>();

    /**
     * Cache to hold already loaded properties per table name
     */
    private final ConcurrentMap<Locale, TableHolder> cachedMergedTable = new ConcurrentHashMap<>();

    private boolean concurrentRefresh = true;

    private final DatabaseMessageLoader databaseMessageLoader;

    public ReloadableDatabaseMessageSource(DatabaseMessageLoader databaseMessageLoader) {
        this.databaseMessageLoader = databaseMessageLoader;
    }

    /**
     * Specify whether to allow for concurrent refresh behavior, i.e. one thread
     * locked in a refresh attempt for a specific cached properties file whereas
     * other threads keep returning the old properties for the time being, until
     * the refresh attempt has completed.
     * <p>Default is "true": this behavior is new as of Spring Framework 4.1,
     * minimizing contention between threads. If you prefer the old behavior,
     * i.e. to fully block on refresh, switch this flag to "false".
     *
     * @see #setCacheSeconds
     * @since 4.1
     */
    public void setConcurrentRefresh(boolean concurrentRefresh) {
        this.concurrentRefresh = concurrentRefresh;
    }

    /**
     * Resolves the given message code as key in the retrieved tables,
     * returning the value found in the table as-is (without MessageFormat parsing).
     *
     * @param code   message code
     * @param locale Locale
     * @return MessageFormat
     */
    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            TableHolder tableHolder = getMergedTable(locale);
            String result = tableHolder.getMessage(code);
            if (result != null) {
                return result;
            }
        } else {
            for (String basename : getBasenameSet()) {
                List<Param> params = calculateAllTableParams(basename, locale);
                for (Param param : params) {
                    TableHolder tableHolder = getTable(param);
                    String result = tableHolder.getMessage(code);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Resolves the given message code as key in the retrieved tables,
     * using a cached MessageFormat instance per message code.
     *
     * @param code   message code
     * @param locale Locale
     * @return MessageFormat
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            TableHolder tableHolder = getMergedTable(locale);
            MessageFormat result = tableHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
        } else {
            for (String basename : getBasenameSet()) {
                List<Param> params = calculateAllTableParams(basename, locale);
                for (Param param : params) {
                    TableHolder tableHolder = getTable(param);
                    MessageFormat result = tableHolder.getMessageFormat(code, locale);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get a TableHolder for the given table param, either from the
     * cache or freshly loaded.
     *
     * @param param the table param(tableName + Locale)
     * @return the current TableHolder for the param
     */
    protected TableHolder getTable(Param param) {
        TableHolder tableHolder = this.cachedTable.get(param);
        long originalTimestamp = -2;

        if (tableHolder != null) {
            originalTimestamp = tableHolder.getRefreshTimestamp();
            if (originalTimestamp == -1 || originalTimestamp > System.currentTimeMillis() - getCacheMillis()) {
                // Up to date
                return tableHolder;
            }
        } else {
            tableHolder = new TableHolder();
            // double check?
            TableHolder existingHolder = this.cachedTable.putIfAbsent(param, tableHolder);
            if (existingHolder != null) {
                tableHolder = existingHolder;
            }
        }
        // At this point, we need to refresh...
        if (this.concurrentRefresh && tableHolder.getRefreshTimestamp() >= 0) {
            // A populated but stale holder -> cloud keep using it.
            if (!tableHolder.refreshLock.tryLock()) {
                // Getting refreshed by another thread already ->
                // let's return the existing properties for the time being.
                return tableHolder;
            }
        } else {
            tableHolder.refreshLock.lock();
        }
        try {
            TableHolder existingHolder = this.cachedTable.get(param);
            if (existingHolder != null && existingHolder.getRefreshTimestamp() > originalTimestamp) {
                return existingHolder;
            }
            return refreshTable(param, tableHolder);
        } finally {
            tableHolder.refreshLock.unlock();
        }
    }

    /**
     * Refresh the TableHolder for the given table param.
     * The holder can be {@code null} if not cached before, or a timed-out cache entry
     * (potentially getting re-validated against the current last-modified timestamp).
     *
     * @param param       the table param (tableName + Locale)
     * @param tableHolder the current TableHolder for the param
     */
    protected TableHolder refreshTable(Param param, @Nullable TableHolder tableHolder) {
        Assert.notNull(databaseMessageLoader, "databaseMessageLoader must not be null.");

        long refreshTimestamp = getCacheMillis() < 0 ? -1 : System.currentTimeMillis();
        Timestamp lastModified = databaseMessageLoader.lastModified(param.getTableName(), param.getLocale());
        if (lastModified != null) {
            // source of the table param existing
            if (getCacheMillis() >= 0) {
                //  Last-modified timestamp of table will just be read if caching with timeout.
                if (tableHolder != null && lastModified.getTime() == tableHolder.getParamTimestamp()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Re-caching table for param [" + param + "] - table hasn't been modified");
                    }
                    tableHolder.setRefreshTimestamp(refreshTimestamp);
                    return tableHolder;
                }
            }
            try {
                Map<String, String> source = databaseMessageLoader.loadMessageSource(param.getTableName(), param.getLocale());
                tableHolder = new TableHolder(source, lastModified.getTime());
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could load source [" + param + "]", e);
                }
                // Empty holder representing "not valid".
                tableHolder = new TableHolder();
            }
        } else {
            // Resource does not exist.
            if (logger.isDebugEnabled()) {
                logger.debug("No table found for [" + param + "]");
            }
            tableHolder = new TableHolder();
        }
        tableHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedTable.put(param, tableHolder);
        return tableHolder;
    }


    /**
     * Calculate all names for the given table name and Locale.
     * Will calculate names for the given Locale, the system Locale
     * (if applicable).
     *
     * @param basename the name of the table
     * @param locale   the locale
     * @return the List of table to check
     * @see #setFallbackToSystemLocale
     * @see #calculateTableParamsForLocale
     */
    protected List<Param> calculateAllTableParams(String basename, Locale locale) {
        Map<Locale, List<Param>> localeMap = this.cachedTableParams.get(basename);
        if (localeMap != null) {
            List<Param> params = localeMap.get(locale);
            if (params != null) {
                return params;
            }
        }
        List<Param> params = new ArrayList<>(6);
        params.addAll(calculateTableParamsForLocale(basename, locale));
        if (isFallbackToSystemLocale() && !locale.equals(Locale.getDefault())) {
            List<Param> fallbackParams = calculateTableParamsForLocale(basename, Locale.getDefault());
            for (Param fallbackParam : fallbackParams) {
                if (!params.contains(fallbackParam)) {
                    // Entry for fallback locale that isn't already in params list.
                    params.add(fallbackParam);
                }
            }
        }
        if (localeMap == null) {
            localeMap = new ConcurrentHashMap<>(2);
            Map<Locale, List<Param>> existing = this.cachedTableParams.putIfAbsent(basename, localeMap);
            if (existing != null) {
                localeMap = existing;
            }
        }
        localeMap.put(locale, params);
        return params;
    }

    /**
     * Calculate the params for the given table name and Locale,
     * appending language code, country code, and variant code.
     * E.g.: basename "messages", Locale "de_AT_oo" -> "messages_de_AT_OO",
     * "messages_de_AT", "messages_de".
     * <p>Follows the rules defined by {@link java.util.Locale#toString()}.
     *
     * @param basename the name of the table
     * @param locale   the locale
     * @return the List of params to check
     */
    protected List<Param> calculateTableParamsForLocale(String basename, Locale locale) {
        List<Param> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder();
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, new Param(basename, temp.toString()));
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, new Param(basename, temp.toString()));
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, new Param(basename, temp.toString()));
        }
        return result;
    }

    /**
     * Get a TableHolder that contains the actually visible properties
     * for a Locale, after merging all specified table.
     * Either fetches the holder from the cache or freshly loads it.
     * <p>Only used when caching resource bundle contents forever, i.e.
     * with cacheSeconds < 0. Therefore, merged properties are always
     * cached forever.
     */
    protected TableHolder getMergedTable(Locale locale) {
        TableHolder mergedHolder = this.cachedMergedTable.get(locale);
        if (mergedHolder != null) {
            return mergedHolder;
        }
        Map<String, String> mergedMap = new ConcurrentHashMap<>();
        long latestTimestamp = -1;
        String[] basenames = StringUtils.toStringArray(getBasenameSet());
        for (int i = basenames.length - 1; i >= 0; i--) {
            List<Param> params = calculateAllTableParams(basenames[i], locale);
            for (int j = params.size() - 1; j >= 0; j--) {
                Param param = params.get(j);
                TableHolder tableHolder = getTable(param);
                if (tableHolder.getSource() != null) {
                    mergedMap.putAll(tableHolder.getSource());
                    if (tableHolder.getParamTimestamp() > latestTimestamp) {
                        latestTimestamp = tableHolder.getParamTimestamp();
                    }
                }
            }
        }
        mergedHolder = new TableHolder(mergedMap, latestTimestamp);
        TableHolder existing = this.cachedMergedTable.putIfAbsent(locale, mergedHolder);
        if (existing != null) {
            mergedHolder = existing;
        }
        return mergedHolder;
    }

    /**
     * Clear the table cache.
     * Subsequent resolve calls will lead to reloading of table.
     */
    public void clearCache() {
        logger.debug("Clearing entire resource bundle cache");
        this.cachedTable.clear();
        this.cachedMergedTable.clear();
    }

    /**
     * Clear the table caches of this MessageSource and all its ancestors.
     *
     * @see #clearCache
     */
    public void clearCacheIncludingAncestors() {
        clearCache();
        if (getParentMessageSource() instanceof ReloadableResourceBundleMessageSource) {
            ((ReloadableResourceBundleMessageSource) getParentMessageSource()).clearCacheIncludingAncestors();
        } else if (getParentMessageSource() instanceof ReloadableDatabaseMessageSource) {
            ((ReloadableDatabaseMessageSource) getParentMessageSource()).clearCacheIncludingAncestors();
        }
    }


    @Override
    public String toString() {
        return getClass().getName() + ": basenames=" + getBasenameSet();
    }


    /**
     * 数据表查找参数
     */
    protected class Param {
        private String tableName;
        private String locale;

        Param(String tableName, String locale) {
            this.tableName = tableName;
            this.locale = locale;
        }

        String getTableName() {
            return tableName;
        }

        void setTableName(String tableName) {
            this.tableName = tableName;
        }

        String getLocale() {
            return locale;
        }

        void setLocale(String locale) {
            this.locale = locale;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Param param = (Param) o;
            return Objects.equals(tableName, param.tableName) &&
                    Objects.equals(locale, param.locale);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableName, locale);
        }

        @Override
        public String toString() {
            return "Param{" +
                    "tableName='" + tableName + '\'' +
                    ", locale='" + locale + '\'' +
                    '}';
        }
    }

    /**
     * 数据表
     * TableHolder for caching.
     * Stores the last-modified timestamp of the table for efficient
     * change detection, and the timestamp of the last refresh attempt
     * (updated every time the cache entry gets re-validated).
     */
    protected class TableHolder {

        @Nullable
        private final Map<String, String> source;

        private final long paramTimestamp;

        private volatile long refreshTimestamp = -2;

        private final ReentrantLock refreshLock = new ReentrantLock();

        /**
         * Cache to hold already generated MessageFormats per message code.
         */
        private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats =
                new ConcurrentHashMap<>();

        public TableHolder() {
            this.source = null;
            this.paramTimestamp = -1L;
        }

        public TableHolder(Map<String, String> source, long paramTimestamp) {
            this.source = source;
            this.paramTimestamp = paramTimestamp;
        }

        @Nullable
        public MessageFormat getMessageFormat(String code, Locale locale) {
            if (this.source == null) {
                return null;
            }
            Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
            if (localeMap != null) {
                MessageFormat result = localeMap.get(locale);
                if (result != null) {
                    return result;
                }
            }
            String msg = this.source.get(code);
            if (msg != null) {
                if (localeMap == null) {
                    localeMap = new ConcurrentHashMap<>();
                    Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(code, localeMap);
                    if (existing != null) {
                        localeMap = existing;
                    }
                }
                MessageFormat result = createMessageFormat(msg, locale);
                localeMap.put(locale, result);
                return result;
            }
            return null;
        }

        @Nullable
        public String getMessage(String code) {
            if (this.source == null) {
                return null;
            }
            return this.source.get(code);
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }

        @Nullable
        public Map<String, String> getSource() {
            return source;
        }

        public long getParamTimestamp() {
            return paramTimestamp;
        }
    }

}
