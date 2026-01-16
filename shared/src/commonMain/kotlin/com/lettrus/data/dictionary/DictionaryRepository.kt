package com.lettrus.data.dictionary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lettrus.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

interface DictionaryRepository {
    suspend fun isLoaded(): Boolean
    suspend fun loadDictionary()
    fun contains(word: String): Boolean
    fun getRandomWord(letterCount: Int): String
    fun getWordOfDay(letterCount: Int): String
}

@OptIn(ExperimentalResourceApi::class)
class DictionaryRepositoryImpl : DictionaryRepository {

    private val words7 = mutableSetOf<String>()
    private val words8 = mutableSetOf<String>()
    private val words9 = mutableSetOf<String>()

    private val wordsList7 = mutableListOf<String>()
    private val wordsList8 = mutableListOf<String>()
    private val wordsList9 = mutableListOf<String>()

    private var loaded = false

    override suspend fun isLoaded(): Boolean = loaded

    override suspend fun loadDictionary() {
        if (loaded) return

        withContext(Dispatchers.IO) {
            loadWordsFile("files/words_7.txt", words7, wordsList7)
            loadWordsFile("files/words_8.txt", words8, wordsList8)
            loadWordsFile("files/words_9.txt", words9, wordsList9)
            loaded = true
        }
    }

    private suspend fun loadWordsFile(
        path: String,
        set: MutableSet<String>,
        list: MutableList<String>
    ) {
        val bytes = Res.readBytes(path)
        val content = bytes.decodeToString()
        content.lines()
            .filter { it.isNotBlank() }
            .forEach { word ->
                val normalized = word.trim().uppercase()
                set.add(normalized)
                list.add(normalized)
            }
    }

    override fun contains(word: String): Boolean {
        val normalized = word.uppercase()
        return when (normalized.length) {
            7 -> words7.contains(normalized)
            8 -> words8.contains(normalized)
            9 -> words9.contains(normalized)
            else -> false
        }
    }

    override fun getRandomWord(letterCount: Int): String {
        val list = getListForLength(letterCount)
        require(list.isNotEmpty()) { "Dictionary not loaded or no words for length $letterCount" }
        return list.random()
    }

    override fun getWordOfDay(letterCount: Int): String {
        val list = getListForLength(letterCount)
        require(list.isNotEmpty()) { "Dictionary not loaded or no words for length $letterCount" }

        val now = Clock.System.now()
        val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfYear = localDate.dayOfYear
        val year = localDate.year

        // Deterministic index based on day and year
        val seed = (year * 1000 + dayOfYear).toLong()
        val index = (seed % list.size).toInt()

        return list[index]
    }

    private fun getListForLength(letterCount: Int): List<String> {
        return when (letterCount) {
            7 -> wordsList7
            8 -> wordsList8
            9 -> wordsList9
            else -> throw IllegalArgumentException("Letter count must be 7, 8, or 9")
        }
    }
}
