package io.github.busaiku0084.bookmanagement.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("BookRequestのテスト")
class BookRequestTests {

	private lateinit var validator: Validator

	@BeforeEach
	fun setup() {
		val factory = Validation.buildDefaultValidatorFactory()
		validator = factory.validator
	}

	@Test
	fun `タイトルが空はバリデーションエラー`() {
		val request = BookRequest(title = "", price = 1000, status = "published", authorIds = listOf(1))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("タイトルを入力してください", violations.first().message)
	}

	@Test
	fun `価格が0はバリデーション成功`() {
		val request = BookRequest(title = "タイトル", price = 0, status = "published", authorIds = listOf(1))
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}

	@Test
	fun `価格が負数はバリデーションエラー`() {
		val request = BookRequest(title = "タイトル", price = -1, status = "published", authorIds = listOf(1))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("価格は0以上で入力してください", violations.first().message)
	}

	@Test
	fun `ステータスが不正な文字列はバリデーションエラー`() {
		val request = BookRequest(title = "タイトル", price = 1000, status = "draft", authorIds = listOf(1))
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals(
			"ステータスは 'unpublished' または 'published' のいずれかである必要があります",
			violations.first().message
		)
	}

	@Test
	fun `著者IDが空はバリデーションエラー`() {
		val request = BookRequest(title = "タイトル", price = 1000, status = "published", authorIds = emptyList())
		val violations = validator.validate(request)
		assertEquals(1, violations.size)
		assertEquals("著者IDを1つ以上指定してください", violations.first().message)
	}

	@Test
	fun `すべて正しい場合はバリデーション成功`() {
		val request = BookRequest(title = "タイトル", price = 1000, status = "published", authorIds = listOf(1, 2))
		val violations = validator.validate(request)
		assertTrue(violations.isEmpty())
	}
}
