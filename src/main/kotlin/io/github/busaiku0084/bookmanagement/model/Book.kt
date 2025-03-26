package io.github.busaiku0084.bookmanagement.model

/**
 * 書籍のドメインモデル
 *
 * @property id 書籍ID（自動採番）
 * @property title 書籍タイトル
 * @property price 書籍価格
 * @property status 出版ステータス（"published" または "unpublished"）
 */
data class Book(
	val id: Int? = null,
	val title: String,
	val price: Int,
	val status: String
)
