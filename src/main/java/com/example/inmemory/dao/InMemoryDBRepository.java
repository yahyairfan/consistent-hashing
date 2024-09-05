package com.example.inmemory.dao;

import com.example.inmemory.dao.entity.User;
import com.example.inmemory.service.dto.UserDTO;
import com.example.inmemory.utils.HashUtils;
import org.springframework.stereotype.Repository;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryDBRepository {
    private final Map<String, List<User>> userDbs = new HashMap<>();
    private final Map<Long, String> userDbNamesMap;
    private static final int BACKUP = 2;

    private final HashUtils hashUtils;

    public InMemoryDBRepository(HashUtils hashUtils){
        this.hashUtils = hashUtils;
        userDbNamesMap = new HashMap<>();
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db1");
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db2");
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db3");
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db4");
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db5");
        userDbNamesMap.put(hashUtils.generateHashKey(null), "db6");
    }

    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addDb(String dbName) {
        Long key = hashUtils.generateHashKey(null);
        userDbNamesMap.put(key, dbName);
    }

    public void removeDb(String dbName) {
        Long key = getKeyByValue(userDbNamesMap, dbName);
        if (key != null) {
            hashUtils.removeKey(key);
            userDbNamesMap.remove(key);
        }
    }

    public void saveUser(User user) {
        Long key = hashUtils.generateHashKey(user.getUsername());
        String dbName = userDbNamesMap.get(key);
        saveUserWithBackup(key, dbName, user);
    }

    public UserDTO getUser(String username) {
        Long key = hashUtils.generateHashKey(username);
        String dbName = userDbNamesMap.get(key);
        return getOrSearchInBackup(key, dbName, username);
    }

    private UserDTO getOrSearchInBackup(Long dbKey, String dbName, String username) {
        List<User> primaryUsers = userDbs.get(dbName);
        List<User> userList;
        if (primaryUsers != null) {
            userList = primaryUsers.stream().filter(user -> user.getUsername().equals(username)).toList();
            if (!userList.isEmpty())
                return prepareUserDTO(dbName, userList.getFirst());
        }

        Long key = dbKey;
        String backupDbName;
        for (int i = 1; i <= BACKUP; i++) {
            key = hashUtils.getNextKey(key);
            backupDbName = userDbNamesMap.get(key);

            List<User> backupUsers = userDbs.get(backupDbName);
            if (backupUsers != null) {
                userList = backupUsers.stream().filter(user -> user.getUsername().equals(username)).toList();
                if (!userList.isEmpty())
                    return prepareUserDTO(dbName, userList.getFirst());
            }
        }

        return null;
    }

    private UserDTO prepareUserDTO(String dbName, User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setDbName(dbName);
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    private void saveUserWithBackup(Long dbKey, String dbName, User user) {
        addUser(dbName, user);

        Long key = dbKey;
        String backupDbName;
        for (int i = 1; i <= BACKUP; i++) {

            key = hashUtils.getNextKey(key);
            backupDbName = userDbNamesMap.get(key);

            addUser(backupDbName, user);
        }
    }

    private void addUser(String dbName, User user) {
        List<User> updatedList;
        if (userDbs.containsKey(dbName)) {
            updatedList = userDbs.get(dbName);
            if (updatedList.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())))
                throw new KeyAlreadyExistsException();
            updatedList.add(user);
            userDbs.put(dbName, updatedList);
        }
        else userDbs.put(dbName, new ArrayList<>(List.of(user)));
    }
}
