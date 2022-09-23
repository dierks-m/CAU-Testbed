package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.YAMLParser;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.constants.UserType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

/**
 * YAML representation of user database, as stored in users.yaml.
 * Wraps loading and storing of data around the actual representation in {@link YAMLUserTable} of data.
 */
public class YAMLUserDatabase implements UserDatabase {
    private static final String USERS_FILE_NAME = "users.yaml";

    private final Path workingDirectory;
    private final YAMLUserTable userTable;

    public YAMLUserDatabase(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.userTable = loadUserTable();
    }

    private YAMLUserTable loadUserTable() {
        try {
            return YAMLParser.parseFile(workingDirectory.resolve(USERS_FILE_NAME), YAMLUserTable.class);
        } catch (IOException e) {
            return new YAMLUserTable(Collections.emptyList(), 1);
        }
    }

    private void writeUserTable() {
        try {
            YAMLParser.writeFile(workingDirectory.resolve(USERS_FILE_NAME), userTable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userTable.getUserById(id);
    }

    @Override
    public Optional<User> getUserByApiKey(String apiKey) {
        return userTable.getUserByApiKey(apiKey);
    }

    @Override
    public User addUser(String name, UserType type) {
        final User user = userTable.addUser(name, type);
        writeUserTable();

        return user;
    }
}
