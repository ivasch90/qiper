import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UsersDbAuthService {
    public List<User> findAll () {
        Connection connection = DatabaseConnection.getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users;");
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(
                new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("nick"),
                        resultSet.getString("password")
                )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }

        return Collections.emptyList();
    }

    public Optional<User> findById(long id) {
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                        new User(
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getString("nick"),
                                resultSet.getString("password")
                )
                );
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }


        return Optional.empty();
    }

    public void save(User user) {
        Connection connection = DatabaseConnection.getConnection();
        try {
            //connection.beginRequest();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (name, nick, password) VALUES (?, ?, ?)");
            statement.setString(1, user.getName());
            statement.setString(2, user.getNick());
            statement.setString(3, user.getPass());

            statement.executeUpdate();

            //connection.commit();
        } catch (SQLException e) {
            //DatabaseConnection.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }

    }

    public void delete(User user) {

    }

}
