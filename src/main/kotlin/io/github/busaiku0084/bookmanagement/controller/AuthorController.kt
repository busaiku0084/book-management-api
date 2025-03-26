package io.github.busaiku0084.bookmanagement.controller

import io.github.busaiku0084.bookmanagement.dto.AuthorRequest
import io.github.busaiku0084.bookmanagement.dto.AuthorResponse
import io.github.busaiku0084.bookmanagement.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 著者に関する API を提供するコントローラー
 *
 * @property authorService 著者情報を操作するサービス
 */
@RestController
@RequestMapping("/authors")
class AuthorController(private val authorService: AuthorService) {

	/**
	 * 著者を登録する
	 *
	 * @param request 著者の情報 (リクエストボディ)
	 * @return 登録された著者情報
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createAuthor(
		@Valid @RequestBody
		request: AuthorRequest
	): AuthorResponse {
		return authorService.createAuthor(request)
	}

	/**
	 * 著者を全件取得する
	 *
	 * @return 著者レスポンスDTOのリスト
	 */
	@GetMapping
	fun getAllAuthors(): List<AuthorResponse> {
		return authorService.getAllAuthors()
	}

	/**
	 * 指定されたIDの著者情報を取得する
	 *
	 * @param id 著者ID（パスパラメータ）
	 * @return 該当する著者情報
	 */
	@GetMapping("/{id}")
	fun getAuthorById(@PathVariable id: Int): ResponseEntity<AuthorResponse> {
		val author = authorService.getAuthorById(id)
		return ResponseEntity.ok(author)
	}

	/**
	 * 指定されたIDの著者情報を更新する
	 *
	 * @param id 著者ID（パスパラメータ）
	 * @return 該当する著者情報
	 */
	@PutMapping("/{id}")
	fun updateAuthor(
		@PathVariable id: Int,
		@Valid @RequestBody
		request: AuthorRequest
	): ResponseEntity<AuthorResponse> {
		val updatedAuthor = authorService.updateAuthor(id, request)
		return ResponseEntity.ok(updatedAuthor)
	}
}
