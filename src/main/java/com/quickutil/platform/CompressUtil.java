package com.quickutil.platform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import ch.qos.logback.classic.Logger;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.LoggerFactory;


public class CompressUtil {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CompressUtil.class);


    /**
     * 解压tar.gz文件：
     * @param sourcePath
     * @param targetPath
     * @return 解压后的根目录路径
     */
    public static String decompressTarGz(String sourcePath, String targetPath) {
        String rootPath = null;
        try(FileInputStream fInput = new FileInputStream(sourcePath);
            BufferedInputStream bufInput = new BufferedInputStream(fInput);
            GZIPInputStream gzipInput = new GZIPInputStream(bufInput);
            ArchiveInputStream archiveInput = new ArchiveStreamFactory().createArchiveInputStream("tar", gzipInput)) {
            // tar压缩文件条目
            TarArchiveEntry entry;
            boolean isRootPath = true;
            while ((entry = (TarArchiveEntry) archiveInput.getNextEntry()) != null) {
                String entryName = entry.getName();
                //转换为目标路径
                if(targetPath != null){
                    entryName = targetPath + File.separator + entryName;
                }
                if(isRootPath){
                    rootPath = entryName;
                    isRootPath = false;
                }
                if(entry.isDirectory()){
                    FileUtil.mkdirByFile(entryName);
                } else if(entry.isFile()){
                    FileUtil.writeFile(archiveInput, entryName, false);
                }
            }
        } catch (Exception e) {
            LOGGER.error("decompressTarGz error", e);
        }
        return rootPath;
    }

}