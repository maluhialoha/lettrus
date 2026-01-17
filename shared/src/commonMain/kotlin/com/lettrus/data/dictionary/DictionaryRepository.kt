package com.lettrus.data.dictionary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lettrus.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

enum class Difficulty {
    EASY,
    HARD
}

interface DictionaryRepository {
    suspend fun isLoaded(): Boolean
    suspend fun loadDictionary()
    fun contains(word: String): Boolean
    fun getRandomWord(letterCount: Int, difficulty: Difficulty): String
    fun getWordOfDay(letterCount: Int, difficulty: Difficulty): String
    fun getWordCount(letterCount: Int, difficulty: Difficulty): Int
}

@OptIn(ExperimentalResourceApi::class)
class DictionaryRepositoryImpl : DictionaryRepository {

    // All words for validation
    private val allWords = mutableMapOf<Int, MutableSet<String>>()

    // Words by difficulty for selection
    private val wordsByDifficulty = mutableMapOf<Pair<Int, Difficulty>, List<String>>()

    private var loaded = false

    override suspend fun isLoaded(): Boolean = loaded

    override suspend fun loadDictionary() {
        if (loaded) return

        withContext(Dispatchers.IO) {
            // Load all words for validation
            for (len in 7..9) {
                allWords[len] = mutableSetOf()
                loadWordsToSet("files/words_$len.txt", allWords[len]!!)
            }

            // Load words by difficulty for selection
            for (len in 7..9) {
                for (difficulty in Difficulty.entries) {
                    val diffName = difficulty.name.lowercase()
                    val path = "files/words_${len}_$diffName.txt"
                    val words = mutableListOf<String>()
                    loadWordsToList(path, words)
                    wordsByDifficulty[len to difficulty] = words
                }
            }

            loaded = true
        }
    }

    private suspend fun loadWordsToSet(path: String, set: MutableSet<String>) {
        val bytes = Res.readBytes(path)
        val content = bytes.decodeToString()
        content.lines()
            .filter { it.isNotBlank() }
            .forEach { word ->
                set.add(word.trim().uppercase())
            }
    }

    private suspend fun loadWordsToList(path: String, list: MutableList<String>) {
        val bytes = Res.readBytes(path)
        val content = bytes.decodeToString()
        content.lines()
            .filter { it.isNotBlank() }
            .forEach { word ->
                list.add(word.trim().uppercase())
            }
    }

    override fun contains(word: String): Boolean {
        val normalized = word.uppercase()
        val len = normalized.length
        return allWords[len]?.contains(normalized) == true
    }

    override fun getRandomWord(letterCount: Int, difficulty: Difficulty): String {
        val words = getWordList(letterCount, difficulty)
        require(words.isNotEmpty()) { "No words for $letterCount letters, $difficulty" }
        return words.random()
    }

    override fun getWordOfDay(letterCount: Int, difficulty: Difficulty): String {
        val words = getWordList(letterCount, difficulty)
        require(words.isNotEmpty()) { "No words for $letterCount letters, $difficulty" }

        val now = Clock.System.now()
        val localDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dayOfYear = localDate.dayOfYear
        val year = localDate.year

        // Deterministic index based on day, year, letter count, and difficulty
        val seed = (year * 1000L + dayOfYear) * 100 + letterCount * 10 + difficulty.ordinal
        val index = (seed % words.size).toInt()

        return words[index]
    }

    override fun getWordCount(letterCount: Int, difficulty: Difficulty): Int {
        return getWordList(letterCount, difficulty).size
    }

    private fun getWordList(letterCount: Int, difficulty: Difficulty): List<String> {
        require(letterCount in 7..9) { "Letter count must be 7, 8, or 9" }
        return wordsByDifficulty[letterCount to difficulty]
            ?: throw IllegalStateException("Dictionary not loaded")
    }
}
