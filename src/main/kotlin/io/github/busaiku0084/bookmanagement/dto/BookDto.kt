package io.github.busaiku0084.bookmanagement.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

/**
 * 書籍登録リクエストデータ
 *
 * @property title タイトル
 * @property price 価格（0以上）
 * @property status 出版状況（"unpublished" または "published"）
 * @property authorIds 著者IDのリスト（1件以上）
 */
data class BookRequest(
	@field:NotBlank(message = "タイトルを入力してください")
	val title: String,

	@field:Min(value = 0, message = "価格は0以上で入力してください")
	val price: Int,

	@field:Pattern(
		regexp = "unpublished|published",
		message = "ステータスは 'unpublished' または 'published' のいずれかである必要があります"
	)
	val status: String,

	@field:NotEmpty(message = "著者IDを1つ以上指定してください")
	val authorIds: List<Int>
)

/**
 * 書籍レスポンスデータ
 *
 * @property id 書籍ID
 * @property title 書籍タイトル
 * @property price 書籍価格
 * @property status 出版状況（"published" または "unpublished"）
 * @property authors 紐づく著者一覧（ID、名前、生年月日）
 */
data class BookResponse(
	val id: Int,
	val title: String,
	val price: Int,
	val status: String,
	val authors: List<AuthorResponse>
)
