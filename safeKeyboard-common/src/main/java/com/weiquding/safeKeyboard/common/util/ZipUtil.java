package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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

    /**
     * 解压缩zip包
     *
     * @param zipFileData zip包文件数据
     * @return zip包内数据
     */
    public static List<FileData> unzip(byte[] zipFileData) {
        if (zipFileData == null || zipFileData.length == 0) {
            throw new IllegalArgumentException("Zip package data must not be null and of length greater than 0.");
        }
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(zipFileData);
                ZipInputStream zis = new ZipInputStream(bais);
        ) {
            return getZipEntryData(zis);
        } catch (IOException e) {
            throw BaseBPError.UNZIP.getInfo().initialize(e);
        }
    }


    /**
     * 解压缩zip包
     *
     * @param zipFile zip包文件
     * @return zip包内数据
     */
    public static List<FileData> unzip(File zipFile) {
        if (zipFile == null || !zipFile.exists()) {
            throw new IllegalArgumentException("zipFile must not be null and must be existing.");
        }
        try (
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ) {
            return getZipEntryData(zis);
        } catch (IOException e) {
            throw BaseBPError.UNZIP.getInfo().initialize(e);
        }
    }

    private static List<FileData> getZipEntryData(ZipInputStream zis) throws IOException {
        List<FileData> fileDataList = new ArrayList<>(2);
        byte[] buffer = new byte[1024];
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = zis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            fileDataList.add(new FileData(zipEntry.getName(), baos.toByteArray()));
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        return fileDataList;
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


    public static class FileData {

        private String fileName;
        private byte[] data;

        public FileData() {
        }

        public FileData(String fileName, byte[] data) {
            this.fileName = fileName;
            this.data = data;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FileData fileData = (FileData) o;
            return Objects.equals(fileName, fileData.fileName) &&
                    Arrays.equals(data, fileData.data);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(fileName);
            result = 31 * result + Arrays.hashCode(data);
            return result;
        }

        @Override
        public String toString() {
            return "FileData{" +
                    "fileName='" + fileName + '\'' +
                    ", data=" + Base64.getEncoder().encodeToString(data) +
                    '}';
        }
    }
}
