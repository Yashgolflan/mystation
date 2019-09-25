/*
 * 
 */
package com.stayprime.storage.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.stayprime.util.gson.CustomExclusionStrategy;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author benjamin
 */
public class JsonStorage {
    private static final String PATH_JSON = "json";
    private static final String EXT_JSON = ".json";

    static final TypeToken<List<String>> stringToken = new TypeToken<List<String>>() {};

    private final Gson gson;

    private final File basePath;

    private Map<String, Object> listLocks;

    public JsonStorage(String basePath) {
        this.basePath = new File(basePath, PATH_JSON);
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.setExclusionStrategies(new CustomExclusionStrategy());
        builder.setDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        this.gson = builder.create();
        listLocks = new HashMap<>();
    }

    public void mkDirs() {
        basePath.mkdirs();
    }

    public <T> void storeList(Class cls, List<T> list) {
        storeObject(cls.getSimpleName(), list);
    }

    public <T> void storeRoot(Object root) {
        storeObject(root.getClass().getSimpleName(), root);
    }

    public void storeObject(String className, Object obj) {
        Object lock = getLock(className);
        JsonElement json = gson.toJsonTree(obj);
        File file = new File(basePath, className + EXT_JSON);
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(json, writer);
                writer.flush();
            }
            catch (IOException ex) {
                throw new RuntimeException("Error storing object to file: " + className + EXT_JSON, ex);
            }
        }
    }

    public <T> void addToList(Class cls, TypeToken<T> listType, Object obj, boolean replaceIfExists) {
        Object lock = getLock(cls.getSimpleName());
        synchronized (lock) {
            try {
                T list = getList(cls, listType);

                if (list == null) {
                    ArrayList newList = new ArrayList();
                    newList.add(obj);
                    list = (T) newList;
                }
                else {
                    if(replaceIfExists == true){
                        ((List)list).remove(obj);
                    }
                    ((List)list).add(obj);
                }

                storeObject(cls.getSimpleName(), list);
            }
            catch (Exception ex) {
                throw new RuntimeException("Error adding object " + obj + " to list " + cls.getSimpleName(), ex);
            }
        }
    }

    public <T> void removeFromList(Class cls, TypeToken<T> listType, Object obj){
        Object lock = getLock(cls.getSimpleName());
        synchronized (lock) {
            try {
                T list = getList(cls, listType);

                if (list != null) {
                    ((List)list).remove(obj);
                    storeObject(cls.getSimpleName(), list);
                }
            }
            catch (Exception ex) {
                throw new RuntimeException("Error removing object " + obj + " from list " + cls.getSimpleName(), ex);
            }
        }
    }

    public <T> T getList(Class cls, TypeToken<T> listType) {
        String listName = cls.getSimpleName();
        return getObject(listName, listType, null);
    }

    public <T> T getList(String listName, TypeToken<T> listType) {
        return getObject(listName, listType, null);
    }

    public <T> T getRoot(Class<T> cls) {
        String className = cls.getSimpleName();
        return getObject(className, null, cls);
    }

    private <T> T getObject(String className, TypeToken<T> typeToken, Class<T> cls) {
        Object lock = getLock(className);
        File file = new File(basePath, className + EXT_JSON);

        if (file.exists() == false) {
            return null;
        }

        synchronized (lock) {
            try (FileReader reader = new FileReader(file)) {
                if (typeToken != null) {
                    return gson.fromJson(reader, typeToken.getType());
                }
                else {
                    return gson.fromJson(reader, cls);
                }
            }
            catch (IOException ex) {
                throw new RuntimeException("Error getting object from file: " + className + EXT_JSON, ex);
            }
        }
    }

    private synchronized Object getLock(String listName) {
        if (listLocks.containsKey(listName)) {
            return listLocks.get(listName);
        }
        else {
            Object lock = new Object();
            listLocks.put(listName, lock);
            return lock;
        }
    }

}
