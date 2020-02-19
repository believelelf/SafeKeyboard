package com.weiquding.safeKeyboard.common.config;

import com.weiquding.safeKeyboard.common.core.DefaultMessagesProvider;
import com.weiquding.safeKeyboard.common.core.MessagesProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;

/**
 * 区分装配{@link ReloadableResourceBundleMessageSource} 和 {@link ReloadableDatabaseMessageSource}
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/24
 */
@Slf4j
@Configuration
public class MessageSourceConfiguration {


    /**
     * 装置ResourceBundle资源
     */
    @Configuration
    @ConditionalOnProperty(name = "base.message-source.reloadable-resource-bundle", havingValue = "true", matchIfMissing = true)
    public static class ReloadableResourceBundleMessageSourceConfiguration implements ApplicationContextAware {

        private static final String PROPERTIES_SUFFIX = "properties";

        private static final String XML_SUFFIX = "xml";

        @Value("${base.exception.defaultMappingCode:BASEBP0001}")
        private String defaultMappingCode;

        @Value("${base.encoding:UTF-8}")
        private String encoding;

        @Value("${base.default-message-source.path:config/msg/values}")
        private String defaultMessageSourcePath;

        @Value("${base.error-message-source.path:config/msg/errors}")
        private String errorMessageSourcePath;

        /**
         * ApplicationContext所用的ClassLoader
         */
        private ClassLoader classLoader;

        @Bean
        @ConditionalOnMissingBean(MessagesProvider.class)
        public MessagesProvider messagesProvider(@Qualifier("defaultMessageSource") MessageSource defaultMessageSource,
                                                 @Qualifier("errorMessageSource") MessageSource errorMessageSource) {
            DefaultMessagesProvider messagesProvider = new DefaultMessagesProvider();
            messagesProvider.setDefaultMappingCode(defaultMappingCode);
            messagesProvider.setDefaultMessageSource(defaultMessageSource);
            messagesProvider.setErrorMessageSource(errorMessageSource);
            return messagesProvider;
        }

        @Bean("defaultMessageSource")
        @ConditionalOnMissingBean(name = "defaultMessageSource")
        public MessageSource defaultMessageSource() throws IOException {
            return getMessageSource(defaultMessageSourcePath);
        }

        @Bean("errorMessageSource")
        @ConditionalOnMissingBean(name = "errorMessageSource")
        public MessageSource errorMessageSource() throws IOException {
            return getMessageSource(errorMessageSourcePath);
        }

        /**
         * 从指定资源路径加载MessageSource
         *
         * @param path 资源路径
         * @return MessageSource
         * @throws IOException IO异常
         */
        private MessageSource getMessageSource(String path) throws IOException {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setDefaultEncoding(encoding);
            Enumeration<URL> urls = this.classLoader.getResources(path);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    if (ResourceUtils.isJarURL(url) || ResourceUtils.isFileURL(url)) {
                        // 对于jar文件中资源，增加一层映射，。
                        // 即config/msg/values 必须为文件，内容为多行文本，一行一个resourceLocation.如classpath:config/msg/common/values.
                        // 以便加载多个Jar中资源文件.
                        String[] paths = calculatePropertiesFiles(url);
                        messageSource.addBasenames(paths);
                    } else {
                        // 对于当前应用，resourceLocation 支持目录或文件。
                        calculatePropertiesFiles(path, url, messageSource);
                    }
                }
            }
            return messageSource;
        }


        /**
         * 对于当前应用，从目录或文件解析basename.
         *
         * @param path          resourceLocation
         * @param url           Uniform Resource Locator
         * @param messageSource 资源束
         */
        private void calculatePropertiesFiles(String path, URL url, ReloadableResourceBundleMessageSource messageSource) {
            try {
                File file = ResourceUtils.getFile(url);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File sub : files) {
                            String subName = sub.getName();
                            calculatePropertiesFiles(path + "/" + subName, new URL(url, file.getName() + "/" + subName), messageSource);
                        }
                    }
                } else {
                    String fileName = file.getName();
                    if (fileName.indexOf('_') < 0) {
                        // 排除locale后缀资源，messageSource会查找
                        String ext = StringUtils.getFilenameExtension(fileName);
                        if (XML_SUFFIX.equals(ext) || PROPERTIES_SUFFIX.equals(ext)) {
                            path = path.substring(0, path.length() - ext.length() - 1);
                            log.info("addBasename: [{}] from [{}] ", path, url);
                            messageSource.addBasenames("classpath:" + path);
                        }
                    }
                }
            } catch (FileNotFoundException | MalformedURLException e) {
                log.error("Failed to resolve resource file: [{}]", url);
            }

        }

        /**
         * 从jar或file中解析resourceLocation
         *
         * @param url fileURL or jarURL
         * @return resourceLocations
         */
        private String[] calculatePropertiesFiles(URL url) {
            try {
                File file = ResourceUtils.getFile(url);
                if (file.exists() && file.canRead() && file.isFile()) {
                    List<String> lines = Files.readAllLines(Paths.get(url.toURI()));
                    return lines.toArray(new String[]{});
                }
            } catch (URISyntaxException | IOException e) {
                log.error("Failed to resolve resource file: [{}]", url);
            }
            return new String[]{};
        }


        /**
         * Expose the ClassLoader used by this ResourceLoader.
         * <p>Clients which need to access the ClassLoader directly can do so
         * in a uniform manner with the ResourceLoader, rather than relying
         * on the thread context ClassLoader.
         *
         * @param applicationContext ApplicationContext
         * @throws BeansException
         */
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.classLoader = applicationContext.getClassLoader();
        }
    }

    /**
     * 装置基于database的资源
     */
    @Configuration
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(name = "base.message-source.reloadable-database", havingValue = "true")
    public static class ReloadableDatabaseMessageSourceConfiguration {
        // TODO 数据库实现
    }

}
