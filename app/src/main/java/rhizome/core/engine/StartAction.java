package rhizome.core.engine;

import io.activej.promise.Promise;

class StartCommand implements Action {
    @Override
    public Promise<Void> execute(Task task) {
        TaskEvent startEvent = new TaskEvent(); // Créez un événement de démarrage
        return task.getCurrentState().handleEvent(task, startEvent);
    }
}