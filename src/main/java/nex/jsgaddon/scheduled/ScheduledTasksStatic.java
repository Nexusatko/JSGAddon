package nex.jsgaddon.scheduled;

import java.util.ArrayList;

public class ScheduledTasksStatic {

    private static final ArrayList<ScheduledTask> TASKS = new ArrayList<>();

    public static void add(ScheduledTask task) {
        TASKS.add(task);
    }

    public static void remove(ScheduledTask task) {
        TASKS.remove(task);
    }

    public static void iterate() {
        for (int i = (TASKS.size() - 1); i >= 0; i--) {
            ScheduledTask task = TASKS.get(i);
            task.update();
            if (task.getWait() <= 0) {
                task.run();
                remove(task);
            }
        }
    }
}
