package io.github.busaiku0084.bookmanagement.model

import java.time.LocalDate

/**
 * 著者のドメインモデル
 *
 * @property id 著者ID
 * @property name 著者名
 * @property birthDate 生年月日
 */
data class Author(
	val id: Int? = null,
	val name: String,
	val birthDate: LocalDate
)
