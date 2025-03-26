package io.github.busaiku0084.bookmanagement.service

import io.github.busaiku0084.bookmanagement.dto.BookRequest
import io.github.busaiku0084.bookmanagement.model.Author
import io.github.busaiku0084.bookmanagement.model.Book
import io.github.busaiku0084.bookmanagement.repository.AuthorRepository
import io.github.busaiku0084.bookmanagement.repository.BookRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("BookServiceのテスト")
class BookServiceTests {

	private val bookRepository: BookRepository = mockk(relaxed = true)
	private val authorRepository: AuthorRepository = mockk(relaxed = true)
	private val bookService = BookService(bookRepository, authorRepository)

	@Nested
	inner class CreateBook {
		@Test
		fun `createBook - 正常系`() {
			val request = BookRequest("本のタイトル", 1200, "published", listOf(1, 2))
			val authors = listOf(
				Author(1, "著者1", LocalDate.of(1970, 1, 1)),
				Author(2, "著者2", LocalDate.of(1980, 2, 2))
			)
			val createdBook = Book(10, request.title, request.price, request.status)

			every { authorRepository.findById(1) } returns authors[0]
			every { authorRepository.findById(2) } returns authors[1]
			every { bookRepository.create(any()) } returns createdBook
			every { bookRepository.setAuthors(any(), any()) } just Runs

			val result = bookService.createBook(request)

			Assertions.assertEquals(10, result.id)
			Assertions.assertEquals(request.title, result.title)
			Assertions.assertEquals(request.price, result.price)
			Assertions.assertEquals(request.status, result.status)
			Assertions.assertEquals(2, result.authors.size)
		}

		@Test
		fun `createBook - 異常系 - 著者が存在しない`() {
			val request = BookRequest("本のタイトル", 1200, "published", listOf(999))

			every { authorRepository.findById(999) } returns null

			val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
				bookService.createBook(request)
			}
			Assertions.assertEquals("ID=999 の著者が見つかりません", exception.message)
		}
	}

	@Nested
	inner class GetBook {
		@Test
		fun `getBook - 正常系`() {
			val book = Book(1, "本のタイトル", 1000, "published")
			val authors = listOf(Author(1, "著者1", LocalDate.of(1970, 1, 1)))

			every { bookRepository.findById(1) } returns book
			every { bookRepository.findAuthorIdsByBookId(1) } returns listOf(1)
			every { authorRepository.findById(1) } returns authors[0]

			val result = bookService.getBook(1)

			Assertions.assertEquals(1, result.id)
			Assertions.assertEquals("本のタイトル", result.title)
		}

		@Test
		fun `getBook - 異常系 - 書籍が存在しない`() {
			every { bookRepository.findById(999) } returns null

			val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
				bookService.getBook(999)
			}
			Assertions.assertEquals("指定された書籍が見つかりません", exception.message)
		}
	}

	@Nested
	inner class UpdateBook {
		@Test
		fun `updateBook - 正常系`() {
			val request = BookRequest("新タイトル", 1600, "published", listOf(1))
			val existing = Book(1, "旧タイトル", 1000, "unpublished")
			val author = Author(1, "著者1", LocalDate.of(1970, 1, 1))

			every { bookRepository.findById(1) } returns existing
			every { authorRepository.findById(1) } returns author
			every { bookRepository.update(any()) } just Runs
			every { bookRepository.setAuthors(1, listOf(1)) } just Runs

			val result = bookService.updateBook(1, request)

			Assertions.assertEquals("新タイトル", result.title)
		}

		@Test
		fun `updateBook - 異常系 - 書籍が存在しない`() {
			val request = BookRequest("新タイトル", 1600, "published", listOf(1))

			every { bookRepository.findById(999) } returns null

			val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
				bookService.updateBook(999, request)
			}
			Assertions.assertEquals("ID=999 の書籍が見つかりません", exception.message)
		}

		@Test
		fun `updateBook - 異常系 - 出版済みから未出版`() {
			val request = BookRequest("本のタイトル", 1200, "unpublished", listOf(1))
			val existing = Book(1, "本のタイトル", 1200, "published")

			every { bookRepository.findById(1) } returns existing

			val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
				bookService.updateBook(1, request)
			}
			Assertions.assertEquals("出版済みの書籍を未出版に戻すことはできません", exception.message)
		}
	}
}
