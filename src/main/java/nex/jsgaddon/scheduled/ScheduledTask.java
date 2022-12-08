package nex.jsgaddon.scheduled;

public class ScheduledTask {
    private final Runnable runnable;
    private int wait;
    private ScheduledTask runAfter = null;

    public ScheduledTask(int wait, Runnable runnable) {
        this.wait = wait;
        this.runnable = runnable;
    }

    public int getWait() {
        return wait;
    }

    public void run() {
        runnable.run();
        if (runAfter != null)
            runAfter.run();
    }

    public void setRunAfter(ScheduledTask runAfter) {
        this.runAfter = runAfter;
    }

    public void update() {
        wait--;
    }
}

