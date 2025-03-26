package io.github.busaiku0084.bookmanagement.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * 著者の登録リクエストデータ
 *
 * @property name 著者の名前 (1文字以上100文字以下)
 * @property birthDate 著者の生年月日 (過去の日付のみ許可)
 */
data class AuthorRequest(
	@field:Size(min = 1, max = 100, message = "著者名は1文字以上100文字以下で入力してください")
	val name: String,

	@field:PastOrPresent(message = "生年月日は過去の日付を指定してください")
	val birthDate: LocalDate
)

/**
 * 著者のレスポンスデータ
 *
 * @property id 著者のID
 * @property name 著者の名前
 * @property birthDate 著者の生年月日
 */
data class AuthorResponse(
	val id: Int?,
	val name: String,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	val birthDate: LocalDate
)
