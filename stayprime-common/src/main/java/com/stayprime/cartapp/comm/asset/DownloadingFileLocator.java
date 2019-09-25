/*
 *
 */
package com.stayprime.cartapp.comm.asset;

import com.stayprime.localservice.CartUnitCommunication;
import com.stayprime.util.MD5Checksum;
import com.stayprime.util.file.FileLocator;
import com.stayprime.util.file.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class DownloadingFileLocator implements FileLocator {

    private static final Logger log = LoggerFactory.getLogger(DownloadingFileLocator.class);

    private final CartUnitCommunication comm;
    private final File finalFolder;
    private final File stagingFolder;
    private final File tempFolder;
    private int downloadRetries = 2;
    private final List<FilePair> fileList;

    private boolean fileDownloaded = false;
    private long totalBytes;

    public DownloadingFileLocator(CartUnitCommunication comm, File finalFolder, File stagingFolder, File tempFolder) {
        this.comm = comm;
        this.finalFolder = finalFolder;
        this.stagingFolder = stagingFolder;
        this.tempFolder = tempFolder;

        fileList = new ArrayList<FilePair>();

        finalFolder.mkdirs();
        stagingFolder.mkdirs();
        tempFolder.mkdirs();
    }

    public void setDownloadRetries(int downloadRetries) {
        this.downloadRetries = downloadRetries;
    }

    public void reset() {
        fileList.clear();
    }

    public void commitDownloadedFiles() {
        for (FilePair file : fileList) {
            if (file.tmp.exists()) {
                //Only delete a file if the temp file still exists,
                //otherwise we can delete a file we just commited
                if (file.dest.exists()) {
                    file.dest.delete();
                }

                file.tmp.renameTo(file.dest);
            }
        }
    }

    public File tryToDownload(String filename) throws Exception {
        return tryToDownload(filename, filename);
    }

    public File tryToDownload(String filename, String remoteFile) throws Exception {
        if (StringUtils.isBlank(remoteFile) || StringUtils.isBlank(filename)) {
            return null;
        }

        File finalFile = new File(finalFolder, filename);
        File stagingFile = new File(stagingFolder, filename);
        File tempFile = tempFolder == null ? stagingFile : new File(tempFolder, filename);

        File file = checkAndDownload(remoteFile, finalFile, stagingFile, tempFile);

        if (stagingFile.equals(file)) {
            fileList.add(new FilePair(finalFile, stagingFile));
        }

        return file;
    }

    @Override
    public File getFile(String filename) {
        return getFile(filename, filename);
    }

    public File getFile(String localName, String remoteName) {
        try {
            fileDownloaded = false;
            totalBytes = 0l;
            return tryToDownload(localName, remoteName);
        }
        catch (Exception ex) {
            RuntimeException rte = new RuntimeException("Error downloading file" + localName, ex);
            log.warn(rte.toString());
            throw rte;
        }
    }

    public boolean isFileDownloaded() {
        return fileDownloaded;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    private File checkAndDownload(String remoteFile, File finalFile, File stagingFile, File tempFile) throws Exception {
        log.info("Check and download: " + finalFile.getName());
        int retries = 0;
        String local = null, remote;

        do {
            retries++;

            try {
                remote = comm.getMD5Checksum(remoteFile);

                if (StringUtils.isBlank(remote)) {
                    throw new NullPointerException("Server returned null checksum");
                }

                //If the file exists in the destination directory, compare to
                //the remote file, if the destination file exists
                if (finalFile.exists()) {
                    local = MD5Checksum.getMD5Checksum(finalFile);
                    if (ObjectUtils.equals(local, remote)) {
                        //Delete leftover files in the staging directory?
                        if (stagingFile.exists()) {
                            log.debug("Staging file exists for some reason, deleting");
                            stagingFile.delete();
                        }

                        return finalFile;
                    }
                }

                if (stagingFile.exists()) {
                    local = MD5Checksum.getMD5Checksum(stagingFile);
                    if (ObjectUtils.equals(local, remote)) {
                        return stagingFile;
                    }
                }

                log.info("Downloading: " + finalFile.getName() + " try " + (retries)
                        + " local md5 " + local + (remote.equals(local) ? " == " : " != ") + remote);

                fileDownloaded = true;
                totalBytes += FileUtils.copyFile(comm.download(remoteFile), tempFile);

                if (ObjectUtils.notEqual(tempFile, stagingFile)) {
                    stagingFile.delete();
                    FileUtils.copyFile(new FileInputStream(tempFile), stagingFile);
                    tempFile.delete();
                }

                local = MD5Checksum.getMD5Checksum(stagingFile);
                if (local.equals(remote) == false) {
                    throw new RuntimeException("Download of file failed: local and remote MD5 checksum don't match");
                }

                return stagingFile;
            }
            catch (Exception ex) {
                if (retries == downloadRetries) {
                    log.warn("Retry limit reached for file: " + finalFile.getName() + ": " + ex);
                    log.trace("Retry limit reached in checkAndDownload ", ex);

                    throw ex;
                }
            }
        } while (retries < downloadRetries);
        throw new RuntimeException("Download of file failed: retry limit reached");
    }

    private class FilePair {

        File dest, tmp;

        public FilePair(File dest, File tmp) {
            this.dest = dest;
            this.tmp = tmp;
        }
    }
}
