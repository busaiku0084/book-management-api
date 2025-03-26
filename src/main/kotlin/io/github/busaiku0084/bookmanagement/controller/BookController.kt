package io.github.busaiku0084.bookmanagement.controller

import io.github.busaiku0084.bookmanagement.dto.BookRequest
import io.github.busaiku0084.bookmanagement.dto.BookResponse
import io.github.busaiku0084.bookmanagement.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 書籍に関する操作を提供するコントローラー
 *
 * @property bookService 書籍情報を操作するサービス
 */
@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

	/**
	 * 書籍を登録する
	 *
	 * @param request 書籍登録リクエスト
	 * @return 登録された書籍レスポンス
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createBook(
		@Valid @RequestBody
		request: BookRequest
	): BookResponse {
		return bookService.createBook(request)
	}

	/**
	 * 書籍をIDで取得する
	 *
	 * @param id 書籍ID
	 * @return 該当する書籍情報
	 */
	@GetMapping("/{id}")
	fun getBook(@PathVariable id: Int): ResponseEntity<BookResponse> {
		return ResponseEntity.ok(bookService.getBook(id))
	}

	/**
	 * 書籍を取得する（全件 or 著者でフィルタ）
	 *
	 * @param authors クエリパラメータとして渡される著者IDリスト（省略可）
	 * @return 書籍情報のリスト
	 */
	@GetMapping
	fun getBooks(@RequestParam("author", required = false) authors: List<Int>?): List<BookResponse> {
		return if (authors.isNullOrEmpty()) {
			bookService.getAllBooks()
		} else {
			bookService.getBooksByAuthorIds(authors)
		}
	}

	/**
	 * 書籍情報を更新する
	 *
	 * @param id 書籍ID
	 * @param request 書籍更新リクエスト
	 * @return 更新された書籍情報
	 */
	@PutMapping("/{id}")
	fun updateBook(
		@PathVariable id: Int,
		@Valid @RequestBody
		request: BookRequest
	): BookResponse {
		return bookService.updateBook(id, request)
	}
}
