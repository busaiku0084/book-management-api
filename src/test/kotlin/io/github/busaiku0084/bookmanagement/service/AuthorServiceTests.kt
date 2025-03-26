package io.github.busaiku0084.bookmanagement.service

import io.github.busaiku0084.bookmanagement.dto.AuthorRequest
import io.github.busaiku0084.bookmanagement.model.Author
import io.github.busaiku0084.bookmanagement.repository.AuthorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("AuthorServiceのテスト")
class AuthorServiceTests {

	private val authorRepository: AuthorRepository = mockk(relaxed = true)
	private val authorService = AuthorService(authorRepository)

	@Nested
	inner class CreateAuthor {

		@Test
		fun `createAuthor - 正常系 - 著者を登録できる`() {
			val request = AuthorRequest(name = "村上春樹", birthDate = LocalDate.of(1949, 1, 12))
			val createdAuthor = Author(id = 1, name = request.name, birthDate = request.birthDate)

			every { authorRepository.create(any()) } returns createdAuthor

			val result = authorService.createAuthor(request)

			assertEquals(1, result.id)
			assertEquals("村上春樹", result.name)
			assertEquals(LocalDate.of(1949, 1, 12), result.birthDate)
		}
	}

	@Nested
	inner class GetAllAuthors {

		@Test
		fun `getAllAuthors - 正常系 - 全著者を取得`() {
			val authors = listOf(
				Author(1, "村上春樹", LocalDate.of(1949, 1, 12)),
				Author(2, "東野圭吾", LocalDate.of(1958, 2, 4))
			)

			every { authorRepository.findAll() } returns authors

			val result = authorService.getAllAuthors()

			assertEquals(2, result.size)
			assertEquals(1, result[0].id)
			assertEquals("村上春樹", result[0].name)
			assertEquals(LocalDate.of(1949, 1, 12), result[0].birthDate)
			assertEquals(2, result[1].id)
			assertEquals("東野圭吾", result[1].name)
			assertEquals(LocalDate.of(1958, 2, 4), result[1].birthDate)
		}

		@Test
		fun `getAllAuthors - 正常系 - 空リスト`() {
			every { authorRepository.findAll() } returns emptyList()

			val result = authorService.getAllAuthors()

			assertTrue(result.isEmpty())
		}
	}

	@Nested
	inner class GetAuthorById {

		@Test
		fun `getAuthorById - 正常系 - 存在する著者を取得`() {
			val author = Author(1, "村上春樹", LocalDate.of(1949, 1, 12))

			every { authorRepository.findById(1) } returns author

			val result = authorService.getAuthorById(1)

			assertEquals(1, result.id)
			assertEquals("村上春樹", result.name)
			assertEquals(LocalDate.of(1949, 1, 12), result.birthDate)
		}

		@Test
		fun `getAuthorById - 異常系 - 著者が存在しない`() {
			every { authorRepository.findById(999) } returns null

			val exception = assertThrows(IllegalArgumentException::class.java) {
				authorService.getAuthorById(999)
			}

			assertEquals("指定された著者が見つかりません", exception.message)
		}
	}

	@Nested
	inner class UpdateAuthor {

		@Test
		fun `updateAuthor - 正常系 - 著者情報を更新`() {
			val request = AuthorRequest(name = "村上春樹(改)", birthDate = LocalDate.of(1950, 1, 1))
			val existingAuthor = Author(1, "村上春樹", LocalDate.of(1949, 1, 12))

			every { authorRepository.findById(1) } returns existingAuthor

			val updatedAuthor = existingAuthor.copy(
				name = request.name,
				birthDate = request.birthDate
			)

			// モックで戻り値は不要。副作用を検証。
			every { authorRepository.update(updatedAuthor) } returns Unit

			val result = authorService.updateAuthor(1, request)

			assertEquals("村上春樹(改)", result.name)
			verify { authorRepository.update(updatedAuthor) }
		}

		@Test
		fun `updateAuthor - 異常系 - 該当IDなし`() {
			every { authorRepository.findById(999) } returns null

			val request = AuthorRequest(name = "該当なし", birthDate = LocalDate.of(2000, 1, 1))

			val exception = assertThrows(IllegalArgumentException::class.java) {
				authorService.updateAuthor(999, request)
			}

			assertEquals("指定された著者が見つかりません", exception.message)
		}
	}
}
