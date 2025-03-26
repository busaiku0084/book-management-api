package io.github.busaiku0084.bookmanagement.repository

import io.github.busaiku0084.bookmanagement.jooq.tables.BookAuthors.BOOK_AUTHORS
import io.github.busaiku0084.bookmanagement.jooq.tables.Books.BOOKS
import io.github.busaiku0084.bookmanagement.model.Book
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * 書籍情報を管理するリポジトリクラス
 *
 * @property dsl jOOQ DSLContext を使用した DB 操作
 */
@Repository
class BookRepository(private val dsl: DSLContext) {

	/**
	 * 書籍をデータベースに登録する。
	 *
	 * @param book 登録対象の書籍モデル
	 * @return 登録された書籍（IDを含む）
	 * @throws RuntimeException 登録に失敗した場合
	 */
	fun create(book: Book): Book {
		val record = dsl.insertInto(BOOKS)
			.columns(BOOKS.TITLE, BOOKS.PRICE, BOOKS.STATUS)
			.values(book.title, book.price, book.status)
			.returning()
			.fetchOne() ?: throw RuntimeException("書籍の登録に失敗しました")

		return Book(
			id = record.id,
			title = record.title,
			price = record.price,
			status = record.status
		)
	}

	/**
	 * 書籍に著者を紐づけ直す。
	 *
	 * @param bookId 書籍ID
	 * @param authorIds 著者IDのリスト
	 */
	fun setAuthors(bookId: Int, authorIds: List<Int>) {
		// 一旦すべて削除
		dsl.deleteFrom(BOOK_AUTHORS)
			.where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
			.execute()

		// 重複を避けて再挿入
		authorIds.distinct().forEach { authorId ->
			dsl.insertInto(BOOK_AUTHORS)
				.columns(BOOK_AUTHORS.BOOK_ID, BOOK_AUTHORS.AUTHOR_ID)
				.values(bookId, authorId)
				.execute()
		}
	}

	/**
	 * 書籍をIDで取得する
	 *
	 * @param id 書籍ID
	 * @return 書籍またはnull
	 */
	fun findById(id: Int): Book? {
		return dsl.selectFrom(BOOKS)
			.where(BOOKS.ID.eq(id))
			.fetchOneInto(Book::class.java)
	}

	/**
	 * 書籍を全件取得する
	 *
	 * @return 書籍のリスト
	 */
	fun findAll(): List<Book> {
		return dsl.selectFrom(BOOKS).fetchInto(Book::class.java)
	}

	/**
	 * 指定した著者に紐づく書籍を取得する
	 *
	 * @param authorIds 著者IDリスト
	 * @return 書籍のリスト
	 */
	fun findByAuthorIds(authorIds: List<Int>): List<Book> {
		return dsl.selectDistinct()
			.from(BOOKS)
			.join(BOOK_AUTHORS)
			.on(BOOKS.ID.eq(BOOK_AUTHORS.BOOK_ID))
			.where(BOOK_AUTHORS.AUTHOR_ID.`in`(authorIds))
			.fetchInto(Book::class.java)
	}

	/**
	 * 書籍IDに紐づく著者ID一覧を取得する
	 *
	 * @param bookId 書籍ID
	 * @return 著者IDのリスト
	 */
	fun findAuthorIdsByBookId(bookId: Int): List<Int> {
		return dsl.select(BOOK_AUTHORS.AUTHOR_ID)
			.from(BOOK_AUTHORS)
			.where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
			.fetch(BOOK_AUTHORS.AUTHOR_ID)
	}

	/**
	 * 書籍情報を更新する
	 *
	 * @param book 更新対象の書籍情報
	 */
	fun update(book: Book) {
		dsl.update(BOOKS)
			.set(BOOKS.TITLE, book.title)
			.set(BOOKS.PRICE, book.price)
			.set(BOOKS.STATUS, book.status)
			.where(BOOKS.ID.eq(book.id))
			.execute()
	}
}
