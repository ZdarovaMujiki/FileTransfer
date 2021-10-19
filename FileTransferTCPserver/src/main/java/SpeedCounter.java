import lombok.extern.java.Log;

import java.io.File;

@Log
public class SpeedCounter implements Runnable {
    private static final int SPEED_CHECK_DELAY = 3;
    private static final int KILO = 1024;

    private final File file;
    private final String fileName;
    private long oldFileSize = 0;
    private int callCounter = 0;
    public SpeedCounter(File file) {
        this.file = file;
        fileName = file.getName();
    }
    @Override
    public void run() {
        callCounter++;
        long currentFileSize = file.length();
        long sizeDifference = currentFileSize - oldFileSize;
        if (0 == sizeDifference) {
            return;
        }
        log.info(fileName + " instant download speed: " + sizeDifference / ((float) SPEED_CHECK_DELAY * KILO) + "KBs");
        log.info(fileName + " average download speed: " + currentFileSize / ((float) SPEED_CHECK_DELAY * callCounter * KILO) + "KBs");
        oldFileSize = currentFileSize;
    }
}
