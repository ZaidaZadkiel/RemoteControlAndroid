package JavaUtils.DownloadManager;

/**
 * The Interface of an DownloadListener for a Download from {@link JavaDownloader}s
 *
 * @author Max
 * @see JavaUtils
 * @see JavaDownloader
 */
public interface DownloadListener {

    /**
     * Occures when a Part were downloaded (Mostly 1024 Bytes)
     *
     * @param name   The Name of the File
     * @param length The Length of the Downloaded Part
     */
    void downloadTileFile(String name, long length);

    /**
     * Occures when a Download of a File is finished
     *
     * @param name The Name of the Downloaded File
     */
    void downloadedFile(String name);

    /**
     * Occures when a File starts to be downloaded
     *
     * @param name   The Name of the File
     * @param length The Length of the File
     */
    void startedDownloading(String name, long length);

}
