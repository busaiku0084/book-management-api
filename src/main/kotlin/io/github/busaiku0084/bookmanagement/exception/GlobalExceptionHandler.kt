package io.github.busaiku0084.bookmanagement.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * グローバルエラーハンドラー
 *
 * Spring Boot の例外をキャッチし、適切なレスポンスを返す
 */
@RestControllerAdvice
class GlobalExceptionHandler {

	private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

	/**
	 * バリデーションエラー時のレスポンスデータ
	 *
	 * @property message エラーメッセージ
	 * @property errors エラー詳細リスト
	 */
	data class ValidationErrorResponse(
		val message: String = "入力値が不正です",
		val errors: List<FieldError>
	)

	/**
	 * フィールド単位のエラー情報
	 *
	 * @property field エラーが発生したフィールド名
	 * @property message エラーメッセージ
	 */
	data class FieldError(
		val field: String,
		val message: String
	)

	/**
	 * 汎用的な単一メッセージレスポンス
	 *
	 * @property message エラーメッセージ
	 */
	data class SimpleErrorResponse(
		val message: String
	)

	/**
	 * バリデーションエラー発生時のハンドリング
	 *
	 * @param e バリデーション例外（@Valid での失敗）
	 * @return バリデーションエラーの詳細レスポンス
	 */
	@ExceptionHandler(MethodArgumentNotValidException::class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	fun handleValidationExceptions(e: MethodArgumentNotValidException): ValidationErrorResponse {
		val errors = e.bindingResult.fieldErrors.map {
			FieldError(
				field = it.field,
				message = it.defaultMessage ?: "不正な値です"
			)
		}
		return ValidationErrorResponse(errors = errors)
	}

	/**
	 * リソースが見つからない等のビジネス例外ハンドリング
	 *
	 * @param e ビジネスロジック上の例外
	 * @return メッセージのみを含むシンプルなレスポンス
	 */
	@ExceptionHandler(IllegalArgumentException::class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	fun handleIllegalArgumentException(e: IllegalArgumentException): SimpleErrorResponse {
		return SimpleErrorResponse(message = e.message ?: "指定されたリソースが見つかりません")
	}

	/**
	 * 想定外の例外をキャッチして内部エラーとして返す
	 *
	 * @param e 処理中に発生した未処理例外
	 * @return 内部エラーの共通メッセージ
	 */
	@ExceptionHandler(Exception::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	fun handleGenericException(e: Exception): SimpleErrorResponse {
		logger.error("予期しないエラーが発生しました", e)
		return SimpleErrorResponse(message = "予期しないエラーが発生しました")
	}
}
