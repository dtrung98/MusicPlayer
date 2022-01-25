package com.ldt.musicr.model.core

interface Searchable {
    var searchScore: Float
    var logMessage: String?
    val spanPosList: MutableList<Int>
}