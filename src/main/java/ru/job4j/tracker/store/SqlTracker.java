package ru.job4j.tracker.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store, AutoCloseable {

    private Connection cn;
    private final Logger logger = LoggerFactory.getLogger(SqlTracker.class.getName());

    public void init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item item) {
        String sql = "insert into item(name, created) values(?, ?)";
        try (PreparedStatement statement = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Не удалось создать item");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return item;
    }

    @Override
    public boolean replace(int id, Item item) {
        String sql = "update item set name = ?, created = ? where id = ?";
        boolean result = false;
        try (PreparedStatement statement = cn.prepareStatement(sql)) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.setInt(3, id);
            result = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean delete(int id) {
        String sql = "delete from item where id = ?";
        boolean result = false;
        try (PreparedStatement statement = cn.prepareStatement(sql)) {
            statement.setInt(1, id);
            result = statement.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Item> findAll() {
        String sql = "select * from item";
        List<Item> result = new ArrayList<>();
        try (Statement statement = cn.createStatement()) {
            statement.execute(sql);
            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    result.add(getItemFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Item> findByName(String key) {
        String sql = "select * from item where name = ?";
        List<Item> result = new ArrayList<>();
        try (PreparedStatement statement = cn.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    result.add(getItemFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Item findById(int id) {
        String sql = "select * from item where id = ?";
        Item item = null;
        try (PreparedStatement statement = cn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    item = getItemFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return item;
    }

    private Item getItemFromResultSet(ResultSet resultSet) throws SQLException {
        return new Item(resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }
}