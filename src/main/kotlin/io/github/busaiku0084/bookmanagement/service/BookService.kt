package io.github.busaiku0084.bookmanagement.service

import io.github.busaiku0084.bookmanagement.dto.AuthorResponse
import io.github.busaiku0084.bookmanagement.dto.BookRequest
import io.github.busaiku0084.bookmanagement.dto.BookResponse
import io.github.busaiku0084.bookmanagement.model.Author
import io.github.busaiku0084.bookmanagement.model.Book
import io.github.busaiku0084.bookmanagement.repository.AuthorRepository
import io.github.busaiku0084.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍情報を管理するサービスクラス
 *
 * @property bookRepository 書籍のデータ操作を行うリポジトリ
 * @property authorRepository 著者のデータ操作を行うリポジトリ
 */
@Service
class BookService(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository) {

	/**
	 * 書籍を登録する
	 *
	 * @param request 書籍登録リクエスト
	 * @return 登録された書籍情報
	 * @throws IllegalArgumentException 該当する著者が存在しない場合
	 */
	@Transactional
	fun createBook(request: BookRequest): BookResponse {
		// 著者IDが存在しない場合はエラー（複数該当しても最初の1件のみが出力）
		val authors = request.authorIds.map { id ->
			authorRepository.findById(id) ?: throw IllegalArgumentException("ID=$id の著者が見つかりません")
		}

		val book = Book(
			title = request.title,
			price = request.price,
			status = request.status
		)
		val created = bookRepository.create(book)

		// 著者とのリレーションを登録
		bookRepository.setAuthors(created.id!!, request.authorIds)

		return BookResponse(
			id = created.id,
			title = created.title,
			price = created.price,
			status = created.status,
			authors = authors.map { AuthorResponse(it.id, it.name, it.birthDate) }
		)
	}

	/**
	 * 書籍をIDで取得する
	 *
	 * @param id 書籍ID
	 * @return 書籍レスポンス
	 */
	fun getBook(id: Int): BookResponse {
		val book = bookRepository.findById(id)
			?: throw IllegalArgumentException("指定された書籍が見つかりません")
		val authorIds = bookRepository.findAuthorIdsByBookId(id)
		val authors = authorIds.mapNotNull { authorRepository.findById(it) }
		return toResponse(book, authors)
	}

	/**
	 * 全書籍を取得する
	 *
	 * @return 書籍レスポンスのリスト
	 */
	fun getAllBooks(): List<BookResponse> {
		return bookRepository.findAll().map { book ->
			val authorIds = bookRepository.findAuthorIdsByBookId(book.id!!)
			val authors = authorIds.mapNotNull { authorRepository.findById(it) }
			toResponse(book, authors)
		}
	}

	/**
	 * 指定した著者IDに紐づく書籍を取得する
	 *
	 * @param authorIds 著者IDリスト
	 * @return 書籍レスポンスのリスト
	 */
	fun getBooksByAuthorIds(authorIds: List<Int>): List<BookResponse> {
		return bookRepository.findByAuthorIds(authorIds).map { book ->
			val ids = bookRepository.findAuthorIdsByBookId(book.id!!)
			val authors = ids.mapNotNull { authorRepository.findById(it) }
			toResponse(book, authors)
		}
	}

	/**
	 * 書籍情報を更新する
	 *
	 * @param id 書籍ID
	 * @param request 書籍更新リクエスト
	 * @return 更新された書籍情報
	 * @throws IllegalArgumentException 該当する書籍または著者が存在しない場合、またはステータス変更が不正な場合
	 */
	@Transactional
	fun updateBook(id: Int, request: BookRequest): BookResponse {
		val existing = bookRepository.findById(id)
			?: throw IllegalArgumentException("ID=$id の書籍が見つかりません")

		// 「出版済み → 未出版」への変更は不許可
		if (existing.status == "published" && request.status == "unpublished") {
			throw IllegalArgumentException("出版済みの書籍を未出版に戻すことはできません")
		}

		val authors = request.authorIds.map { authorId ->
			authorRepository.findById(authorId)
				?: throw IllegalArgumentException("ID=$authorId の著者が見つかりません")
		}

		val updated = existing.copy(
			title = request.title,
			price = request.price,
			status = request.status
		)
		bookRepository.update(updated)
		bookRepository.setAuthors(id, request.authorIds)

		return BookResponse(
			id = updated.id!!,
			title = updated.title,
			price = updated.price,
			status = updated.status,
			authors = authors.map {
				AuthorResponse(it.id, it.name, it.birthDate)
			}
		)
	}

	/**
	 * 書籍とその著者情報からレスポンス用の DTO に変換する
	 *
	 * @param book 書籍モデル
	 * @param authors 紐づく著者モデルのリスト
	 * @return 書籍レスポンス DTO
	 */
	private fun toResponse(book: Book, authors: List<Author>): BookResponse {
		return BookResponse(
			id = book.id!!,
			title = book.title,
			price = book.price,
			status = book.status,
			authors = authors.map { AuthorResponse(it.id, it.name, it.birthDate) }
		)
	}
}
