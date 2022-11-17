package ru.job4j.tracker.store;

import org.junit.jupiter.api.*;
import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class SqlTrackerTest {

    private static Connection connection;

    @BeforeAll
    public static void initConnection() {
        try (InputStream in = SqlTrackerTest.class.getClassLoader().getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @AfterEach
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from items")) {
            statement.execute();
        }
    }

    @Test
    public void whenSaveItemAndFindByGeneratedIdThenMustBeTheSame() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        Assertions.assertEquals(item, tracker.findById(item.getId()));
    }

    @Test
    public void whenSaveItemAndReplace() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        Item newItem = new Item("new item");
        Assertions.assertTrue(tracker.replace(item.getId(), newItem));
    }

    @Test
    public void whenSaveItemAndDelete() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        Assertions.assertTrue(tracker.delete(item.getId()));
    }

    @Test
    public void whenSaveItemsAndFindAll() {
        SqlTracker tracker = new SqlTracker(connection);
        List<Item> expected = List.of(
                new Item("item1"),
                new Item("item2"),
                new Item("item1")
        );
        expected.forEach(tracker::add);
        List<Item> actual = tracker.findAll();
        Assertions.assertEquals(actual.size(), 3);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void whenSaveItemAndFindByName() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        List<Item> actual = tracker.findByName("item");
        Assertions.assertEquals(actual.size(), 1);
        Assertions.assertEquals(item, actual.get(0));
    }
}