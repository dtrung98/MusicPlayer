package com.ldt.musicr.model.mp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_hit_info")
class TopHitInfo(
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "tap_type") val tapType: Int,
    @ColumnInfo(name = "timestamp") val timestamps: String,
    @PrimaryKey(autoGenerate = true) val tid: Int = 0,
) {
    companion object {
        const val TAP_TYPE_COMMON = 0
    }
}