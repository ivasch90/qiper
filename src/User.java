public class User {
    private long id;
    private String name;
    private String nick;
    private String pass;

    public User(String name, String nick, String pass) {
        this(0L, name, nick,pass);
    }

    public User(long id, String name, String nick, String pass) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.pass = pass;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public String getPass() {
        return pass;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
