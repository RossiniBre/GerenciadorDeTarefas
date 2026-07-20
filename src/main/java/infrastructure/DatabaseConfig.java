package infrastructure;

import domain.exceptions.InvalidFieldException;

public class DatabaseConfig {

    private final String host;
    private final String port;
    private final String user;
    private final String password;

    public DatabaseConfig(String host, String port, String user, String password){
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        validateNotNull(host, "DB_HOST");
        validateNotNull(port, "DB_PORT");
        validateNotNull(user, "DB_USER");
        validateNotNull(password, "DB_PASSWORD");

        return new DatabaseConfig(host, port, user, password);
    }

    private static void validateNotNull(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidFieldException(fieldName + " não configurado");
        }
    }

    public String getHost(){ return host; }
    public String getPort(){ return port; }
    public String getUser(){ return user; }
    public String getPassword(){ return password; }

    public String getUrl(){
        return String.format("jdbc:mysql://%s:%s/taskmanager", this.host, this.port);
    }
}