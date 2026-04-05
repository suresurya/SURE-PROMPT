package com.suresurya.sureprompt.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY id DESC")
    LiveData<List<PromptEntity>> getAllPrompts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrompts(List<PromptEntity> prompts);

    @Query("DELETE FROM prompts")
    void deleteAll();
}
