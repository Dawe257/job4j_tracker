package ru.job4j.tracker.store;

import org.junit.jupiter.api.Test;
import ru.job4j.tracker.model.Item;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemTrackerTest {

    @Test
    public void whenAddNewItemThenTrackerHasSameItem() {
        MemTracker memTracker = new MemTracker();
        Item item = new Item("test1");
        memTracker.add(item);
        Item result = memTracker.findById(item.getId());
        assertThat(result.getName()).isEqualTo(item.getName());
    }

    @Test
    public void whenFindAll() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        Item item2 = new Item("second");
        memTracker.add(item1);
        memTracker.add(item2);
        List<Item> expected = List.of(item1, item2);
        List<Item> result = memTracker.findAll();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void whenFindByName() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        Item item2 = new Item("second");
        Item item3 = new Item("first");
        memTracker.add(item1);
        memTracker.add(item2);
        memTracker.add(item3);
        List<Item> expected = List.of(item1, item3);
        List<Item> result = memTracker.findByName("first");
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void whenFindById() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        Item item2 = new Item("second");
        Item item3 = new Item("first");
        memTracker.add(item1);
        memTracker.add(item2);
        memTracker.add(item3);
        Item result = memTracker.findById(item2.getId());
        assertThat(result).isEqualTo(item2);
    }

    @Test
    public void whenFindByIdNotFound() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        Item item2 = new Item("second");
        Item item3 = new Item("first");
        memTracker.add(item1);
        memTracker.add(item2);
        memTracker.add(item3);
        Item result = memTracker.findById(4);
        assertThat(result).isNull();
    }

    @Test
    public void whenReplace() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        memTracker.add(item1);
        memTracker.replace(item1.getId(), new Item("second"));
        assertThat(memTracker.findById(item1.getId()).getName()).isEqualTo("second");
    }

    @Test
    public void whenDelete() {
        MemTracker memTracker = new MemTracker();
        Item item1 = new Item("first");
        memTracker.add(item1);
        memTracker.delete(item1.getId());
        assertThat(memTracker.findById(item1.getId())).isNull();
    }

}