package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩工具类
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/9
 */
@Slf4j
public class ZipUtil {

    private ZipUtil() {
    }

    /**
     * 归档文件为zip包
     *
     * @param originFile 源文件
     * @return zip包
     */
    public static byte[] zipping(File originFile) {
        if (originFile == null || !originFile.exists()) {
            throw new IllegalArgumentException("originFile must not be null and must be existing.");
        }
        try (
                ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(zipBaos);
        ) {
            // 压缩文件
            zipFile(originFile, originFile.getName(), zos);
            zos.finish();
            return zipBaos.toByteArray();
        } catch (IOException e) {
            throw BaseBPError.COMPRESSING_ZIP.getInfo().initialize(e);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
                }
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
