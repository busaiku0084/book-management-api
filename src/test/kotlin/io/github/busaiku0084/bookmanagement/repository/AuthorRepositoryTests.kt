package io.github.busaiku0084.bookmanagement.repository

import io.github.busaiku0084.bookmanagement.jooq.tables.Authors.AUTHORS
import io.github.busaiku0084.bookmanagement.model.Author
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DisplayName("AuthorRepositoryのテスト")
@ActiveProfiles("test")
@JooqTest
class AuthorRepositoryTests @Autowired constructor(
	private val dsl: DSLContext
) {

	private val authorRepository = AuthorRepository(dsl)

	@BeforeEach
	fun setUp() {
		dsl.deleteFrom(AUTHORS).execute()
	}

	@Nested
	inner class Create {

		@Test
		fun `create - 正常系 - 著者が登録される`() {
			val author = Author(name = "村上春樹", birthDate = LocalDate.of(1949, 1, 12))

			val result = authorRepository.create(author)

			assertNotNull(result.id)
			assertEquals("村上春樹", result.name)
			assertEquals(LocalDate.of(1949, 1, 12), result.birthDate)
		}
	}

	@Nested
	inner class FindById {

		@Test
		fun `findById - 正常系 - 存在する著者を返す`() {
			val created = authorRepository.create(Author(name = "村上春樹", birthDate = LocalDate.of(1949, 1, 12)))

			val result = authorRepository.findById(created.id!!)
			assertNotNull(result)
			assertEquals(created.id, result!!.id)
			assertEquals("村上春樹", result.name)
			assertEquals(LocalDate.of(1949, 1, 12), result.birthDate)
		}

		@Test
		fun `findById - 異常系 - 該当なしでnull`() {
			val result = authorRepository.findById(999)
			assertNull(result)
		}
	}

	@Nested
	inner class FindAll {

		@Test
		fun `findAll - 正常系 - 複数件返す`() {
			val a = authorRepository.create(Author(name = "著者A", birthDate = LocalDate.of(1950, 1, 1)))
			val b = authorRepository.create(Author(name = "著者B", birthDate = LocalDate.of(1960, 2, 2)))

			val result = authorRepository.findAll()
			assertEquals(2, result.size)

			val resultA = result.find { it.name == "著者A" }
			val resultB = result.find { it.name == "著者B" }

			assertNotNull(resultA)
			assertEquals(a.id, resultA!!.id)
			assertEquals(a.name, resultA.name)
			assertEquals(a.birthDate, resultA.birthDate)

			assertNotNull(resultB)
			assertEquals(b.id, resultB!!.id)
			assertEquals(b.name, resultB.name)
			assertEquals(b.birthDate, resultB.birthDate)
		}

		@Test
		fun `findAll - 正常系 - 空リスト`() {
			val result = authorRepository.findAll()
			assertTrue(result.isEmpty())
		}
	}

	@Nested
	inner class Update {

		@Test
		fun `update - 正常系 - 更新される`() {
			val original = authorRepository.create(Author(name = "著者(旧)", birthDate = LocalDate.of(1950, 1, 1)))
			val updated = original.copy(name = "著者(新)")

			assertDoesNotThrow {
				authorRepository.update(updated)
			}

			val result = authorRepository.findById(updated.id!!)
			assertNotNull(result)
			assertEquals("著者(新)", result!!.name)
			assertEquals(LocalDate.of(1950, 1, 1), result.birthDate)
			assertEquals(original.id, result.id)
		}
	}
}
