package de.cau.testbed.server.service;

import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.constants.UserType;

public class UserService {
    private final UserDatabase database;

    public UserService(UserDatabase database) {
        this.database = database;
    }

    public User createUser(String name, UserType type) {
        return database.addUser(name, type);
    }
}
