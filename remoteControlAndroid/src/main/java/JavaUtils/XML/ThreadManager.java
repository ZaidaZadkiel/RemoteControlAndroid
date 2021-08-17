package JavaUtils.XML;

class ThreadManager {

    static int maxThreads = Runtime.getRuntime().availableProcessors();
    static int runningThreads = 0;

    public static boolean ifCreateNewThread() {
        if (runningThreads < maxThreads) {
            runningThreads++;
            return true;
        }
        return false;
    }

    public static void removeRunningThread() {
        runningThreads--;
    }


}
