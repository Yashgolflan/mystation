/*
 *
 */
package com.stayprime.util.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author benjamin
 */
public class TaskList implements Task {
    private static final Logger log = LoggerFactory.getLogger(TaskList.class);
    private List<Task> tasks;

    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<Task>(Arrays.asList(tasks));
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public void startTask() {
        for (Task operation : tasks) {
            try {
                operation.startTask();
            }
            catch (Throwable t) {
                log.error(t.toString());
                log.debug(t.toString(), t);
            }
        }
    }

    public void runTask() throws Exception {
        for (Task operation : tasks) {
            try {
                operation.runTask();
            }
            catch (Throwable t) {
                log.error(t.toString());
                log.debug(t.toString(), t);
            }
        }
    }

}
