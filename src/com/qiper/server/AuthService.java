package com.qiper.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class AuthService {

    /*
    private static final List<Entry> entries;


    static {
        entries = List.of(
                new Entry("name1", "nick1", "pass1"),
                new Entry("name2", "nick2", "pass2"),
                new Entry("name3", "nick3", "pass3")
        );
    }

    */

    public Optional<Entry> findUserByLoginAndPassword(String login, String password) {
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE nick = ? AND password = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return Optional.of(
                    new Entry(
                            resultSet.getString("name"),
                            resultSet.getString("nick"),
                            resultSet.getString("password")
                    )
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }






        /**
         for (AuthService.Entry entry : entries) {
         if (entry.login.equals(login) && entry.password.equals(password)) {
         return Optional.of(entry);
         }
         }

         return Optional.empty();



        return entries.stream()
                .filter(entry -> entry.login.equals(login) && entry.password.equals(password))
                .findFirst();

    }
         */
    }
    // Task *
    public boolean changeNick(Entry entry, String nickNew) {
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
            statement.setString(1, nickNew);
            statement.setString(2, entry.getLogin());
            if (statement.execute()) {
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connection);
        }
        return false;
    }

    static class Entry {
        String name;
        String login;
        String password;

        Entry(String name, String login, String password) {
            this.name = name;
            this.login = login;
            this.password = password;
        }

        String getName() {
            return name;
        }

        String getLogin() {
            return login;
        }

        String getPassword() {
            return password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(name, entry.name) && Objects.equals(login, entry.login) && Objects.equals(password, entry.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, login, password);
        }
    }

}