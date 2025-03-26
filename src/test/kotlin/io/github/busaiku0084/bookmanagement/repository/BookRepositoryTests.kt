package io.github.busaiku0084.bookmanagement.repository

import io.github.busaiku0084.bookmanagement.jooq.tables.BookAuthors.BOOK_AUTHORS
import io.github.busaiku0084.bookmanagement.jooq.tables.Books.BOOKS
import io.github.busaiku0084.bookmanagement.model.Author
import io.github.busaiku0084.bookmanagement.model.Book
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import kotlin.test.assertTrue

@DisplayName("BookRepositoryのテスト")
@ActiveProfiles("test")
@JooqTest
class BookRepositoryTests @Autowired constructor(
	private val dsl: DSLContext
) {

	private val bookRepository = BookRepository(dsl)
	private val authorRepository = AuthorRepository(dsl)

	@BeforeEach
	fun setup() {
		dsl.deleteFrom(BOOK_AUTHORS).execute()
		dsl.deleteFrom(BOOKS).execute()
	}

	@Nested
	inner class Create {
		@Test
		fun `create - 正常系 - 書籍が登録される`() {
			val book = Book(title = "サンプル本", price = 1000, status = "unpublished")

			val result = bookRepository.create(book)

			assertNotNull(result.id)
			assertEquals("サンプル本", result.title)
			assertEquals(1000, result.price)
			assertEquals("unpublished", result.status)
		}
	}

	@Nested
	inner class FindById {
		@Test
		fun `findById - 正常系 - 存在する書籍を返す`() {
			val created = bookRepository.create(Book(title = "タイトル1", price = 1500, status = "published"))

			val result = bookRepository.findById(created.id!!)

			assertNotNull(result)
			assertEquals(created.id, result!!.id)
			assertEquals(created.title, result.title)
			assertEquals(created.price, result.price)
			assertEquals(created.status, result.status)
		}

		@Test
		fun `findById - 異常系 - 該当なしでnull`() {
			val result = bookRepository.findById(9999)
			assertNull(result)
		}
	}

	@Nested
	inner class FindAll {
		@Test
		fun `findAll - 正常系 - 書籍をすべて返す`() {
			bookRepository.create(Book(title = "タイトルA", price = 500, status = "unpublished"))
			bookRepository.create(Book(title = "タイトルB", price = 800, status = "published"))

			val result = bookRepository.findAll()
			val titles = result.map { it.title }
			val prices = result.map { it.price }

			assertEquals(2, result.size)
			assertTrue(titles.contains("タイトルA"))
			assertTrue(titles.contains("タイトルB"))
			assertTrue(prices.contains(500))
			assertTrue(prices.contains(800))
		}

		@Test
		fun `findAll - 正常系 - 空リスト`() {
			val result = bookRepository.findAll()
			assertTrue(result.isEmpty())
		}
	}

	@Nested
	inner class Update {
		@Test
		fun `update - 正常系 - 書籍情報を更新`() {
			val original = bookRepository.create(Book(title = "旧タイトル", price = 500, status = "unpublished"))
			val updated = original.copy(title = "新タイトル", price = 1200, status = "published")

			bookRepository.update(updated)

			val result = bookRepository.findById(updated.id!!)

			assertNotNull(result)
			assertEquals("新タイトル", result!!.title)
			assertEquals(1200, result.price)
			assertEquals("published", result.status)
		}
	}

	@Nested
	inner class AuthorRelations {
		@Test
		fun `setAuthors - 正常系 - 著者を紐づけ`() {
			// 事前に著者を作成
			val author1 = authorRepository.create(Author(name = "著者1", birthDate = LocalDate.of(1950, 1, 1)))
			val author2 = authorRepository.create(Author(name = "著者2", birthDate = LocalDate.of(1960, 2, 2)))

			val book = bookRepository.create(Book(title = "A", price = 800, status = "published"))
			bookRepository.setAuthors(book.id!!, listOf(author1.id!!, author2.id!!, author2.id!!))

			val authorIds = bookRepository.findAuthorIdsByBookId(book.id!!)
			assertEquals(listOf(author1.id, author2.id), authorIds.sorted())
		}

		@Test
		fun `findByAuthorIds - 正常系 - 該当する書籍を返す`() {
			// 事前に著者を作成
			val author1 = authorRepository.create(Author(name = "著者1", birthDate = LocalDate.of(1950, 1, 1)))
			val author2 = authorRepository.create(Author(name = "著者2", birthDate = LocalDate.of(1960, 2, 2)))

			val bookA = bookRepository.create(Book(title = "本A", price = 800, status = "published"))
			val bookB = bookRepository.create(Book(title = "本B", price = 1000, status = "unpublished"))

			bookRepository.setAuthors(bookA.id!!, listOf(author1.id!!))
			bookRepository.setAuthors(bookB.id!!, listOf(author2.id!!))

			val result = bookRepository.findByAuthorIds(listOf(author2.id!!))

			assertEquals(1, result.size)
			assertEquals("本B", result.first().title)
		}
	}
}
