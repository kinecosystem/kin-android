package kin.sdk.legacytests;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import kin.sdk.internal.Store;

class FakeStore implements Store {

    private HashMap<String, String> map = new HashMap<>();

    FakeStore() {
    }

    @Override
    public void saveString(@NonNull String key, @NonNull String value) {
        map.put(key, value);
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) {
        return map.get(key);
    }

    @Override
    public void clear(@NonNull String key) {
        map.remove(key);
    }
}
