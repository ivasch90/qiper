import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();
        System.out.println(connection);

        UsersDbAuthService usersDbAuthService = new UsersDbAuthService();
        //System.out.println(usersDbAuthService.findAll());
        System.out.println(usersDbAuthService.findById(1L));
        //User user1 = new User("name1", "nick1", "pass1");
        //User user2 = new User("name2", "nick2", "pass2");
        //usersDbAuthService.save(user2);
    }
}
