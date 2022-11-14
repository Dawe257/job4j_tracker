package ru.job4j.tracker.action;

import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.MemTracker;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.Store;

public class DeleteAction implements UserAction {
    Output output;

    public DeleteAction(Output output) {
        this.output = output;
    }

    @Override
    public String name() {
        return "=== Delete item ====";
    }

    @Override
    public boolean execute(Input input, Store tracker) {
        int id = input.askInt("Enter id: ");
        if (tracker.delete(id)) {
            output.println("Item is successfully deleted!");
        } else {
            output.println("Wrong id!");
        }
        return true;
    }
}
