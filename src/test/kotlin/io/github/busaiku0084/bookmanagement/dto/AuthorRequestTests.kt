package io.github.busaiku0084.bookmanagement.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("AuthorRequestのテスト")
class AuthorRequestTests {

	private lateinit var validator: Validator

	@BeforeEach
	fun setup() {
		val factory = Validation.buildDefaultValidatorFactory()
		validator = factory.validator
	}

	@Test
	fun `著者名が1文字でバリデーション成功`() {
		val request = AuthorRequest(name = "あ", birthDate = LocalDate.of(1949, 1, 12))
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}

	@Test
	fun `著者名が100文字でバリデーション成功`() {
		val request = AuthorRequest(name = "あ".repeat(100), birthDate = LocalDate.of(1949, 1, 12))
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}

	@Test
	fun `著者名が空だとバリデーションエラー`() {
		val request = AuthorRequest(name = "", birthDate = LocalDate.of(1949, 1, 12))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("著者名は1文字以上100文字以下で入力してください", violations.first().message)
	}

	@Test
	fun `著者名が101文字だとバリデーションエラー`() {
		val request = AuthorRequest(name = "あ".repeat(101), birthDate = LocalDate.of(1949, 1, 12))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("著者名は1文字以上100文字以下で入力してください", violations.first().message)
	}

	@Test
	fun `生年月日が未来の日付だとバリデーションエラー`() {
		val request = AuthorRequest(name = "村上春樹", birthDate = LocalDate.now().plusDays(1))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("生年月日は過去の日付を指定してください", violations.first().message)
	}

	@Test
	fun `生年月日が今日ならバリデーション成功`() {
		val request = AuthorRequest(name = "村上春樹", birthDate = LocalDate.now())
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}

	@Test
	fun `生年月日が昨日ならバリデーション成功`() {
		val request = AuthorRequest(name = "村上春樹", birthDate = LocalDate.now().minusDays(1))
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}
}
