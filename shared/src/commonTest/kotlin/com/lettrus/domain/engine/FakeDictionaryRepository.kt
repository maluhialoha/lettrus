package com.lettrus.domain.engine

import com.lettrus.data.dictionary.Difficulty
import com.lettrus.data.dictionary.DictionaryRepository

class FakeDictionaryRepository(
    private val validWords: Set<String> = setOf(
        "POISSON", "POULETS", "PENDANT", "PRESQUE",
        "TOUJOURS", "QUELQUES", "BEAUCOUP"
    ),
    private val wordToReturn: String = "POISSON"
) : DictionaryRepository {

    override suspend fun isLoaded(): Boolean = true

    override suspend fun loadDictionary() {}

    override fun contains(word: String): Boolean {
        return validWords.contains(word.uppercase())
    }

    override fun getRandomWord(letterCount: Int, difficulty: Difficulty): String {
        return wordToReturn
    }

    override fun getWordOfDay(letterCount: Int, difficulty: Difficulty): String {
        return wordToReturn
    }

    override fun getWordCount(letterCount: Int, difficulty: Difficulty): Int {
        return validWords.count { it.length == letterCount }
    }
}
