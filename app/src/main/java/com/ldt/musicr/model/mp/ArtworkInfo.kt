package com.ldt.musicr.model.mp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artwork_info")
class ArtworkInfo(
    @PrimaryKey val mediaId: String,
    @ColumnInfo val localPath: String,
    @ColumnInfo val timestamp: Long,
)