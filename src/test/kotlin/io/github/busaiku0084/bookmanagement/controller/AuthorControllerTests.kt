package io.github.busaiku0084.bookmanagement.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.busaiku0084.bookmanagement.dto.AuthorRequest
import io.github.busaiku0084.bookmanagement.dto.AuthorResponse
import io.github.busaiku0084.bookmanagement.exception.GlobalExceptionHandler
import io.github.busaiku0084.bookmanagement.service.AuthorService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate

@DisplayName("AuthorControllerのテスト")
class AuthorControllerTests {

	private val authorService: AuthorService = org.mockito.kotlin.mock()
	private lateinit var mockMvc: MockMvc
	private val objectMapper = ObjectMapper()

	@BeforeEach
	fun setup() {
		objectMapper.registerModule(JavaTimeModule())
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

		mockMvc = MockMvcBuilders
			.standaloneSetup(AuthorController(authorService))
			.setControllerAdvice(GlobalExceptionHandler())
			.build()
	}

	@Nested
	inner class CreateAuthor {

		@Test
		fun `POST authors - 正常系`() {
			val request = AuthorRequest(name = "村上春樹", birthDate = LocalDate.of(1949, 1, 12))
			val response = AuthorResponse(id = 1, name = request.name, birthDate = request.birthDate)

			whenever(authorService.createAuthor(request)).thenReturn(response)

			mockMvc.post("/authors") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isCreated() }
				jsonPath("$.id", equalTo(1))
				jsonPath("$.name", equalTo("村上春樹"))
				jsonPath("$.birthDate", equalTo("1949-01-12"))
			}
		}

		@Test
		fun `POST authors - バリデーションエラー`() {
			val request = AuthorRequest(name = "", birthDate = LocalDate.now().plusDays(1))

			mockMvc.post("/authors") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isBadRequest() }
				jsonPath("$.message", equalTo("入力値が不正です"))
				jsonPath("$.errors[*].field", hasItem("name"))
				jsonPath("$.errors[*].field", hasItem("birthDate"))
				jsonPath("$.errors[*].message", hasItem("著者名は1文字以上100文字以下で入力してください"))
				jsonPath("$.errors[*].message", hasItem("生年月日は過去の日付を指定してください"))
			}
		}
	}

	@Nested
	inner class GetAuthors {

		@Test
		fun `GET authors - 正常系`() {
			val authors = listOf(
				AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12)),
				AuthorResponse(2, "東野圭吾", LocalDate.of(1958, 2, 4))
			)

			whenever(authorService.getAllAuthors()).thenReturn(authors)

			mockMvc.get("/authors")
				.andExpect {
					status { isOk() }
					jsonPath("$.size()", equalTo(2))
					jsonPath("$[0].id", equalTo(1))
					jsonPath("$[0].name", equalTo("村上春樹"))
					jsonPath("$[0].birthDate", equalTo("1949-01-12"))
					jsonPath("$[1].id", equalTo(2))
					jsonPath("$[1].name", equalTo("東野圭吾"))
					jsonPath("$[1].birthDate", equalTo("1958-02-04"))
				}
		}
	}

	@Nested
	inner class GetAuthorById {

		@Test
		fun `GET authors_id - 正常系`() {
			val author = AuthorResponse(1, "村上春樹", LocalDate.of(1949, 1, 12))

			whenever(authorService.getAuthorById(1)).thenReturn(author)

			mockMvc.get("/authors/1")
				.andExpect {
					status { isOk() }
					jsonPath("$.id", equalTo(1))
					jsonPath("$.name", equalTo("村上春樹"))
					jsonPath("$.birthDate", equalTo("1949-01-12"))
				}
		}

		@Test
		fun `GET authors_id - 異常系 - 存在しないID`() {
			whenever(authorService.getAuthorById(999)).thenThrow(IllegalArgumentException("指定された著者が見つかりません"))

			mockMvc.get("/authors/999")
				.andExpect {
					status { isNotFound() }
					jsonPath("$.message", equalTo("指定された著者が見つかりません"))
				}
		}
	}

	@Nested
	inner class UpdateAuthor {

		@Test
		fun `PUT authors_id - 正常系`() {
			val request = AuthorRequest(name = "東野圭吾", birthDate = LocalDate.of(1958, 2, 4))
			val updated = AuthorResponse(2, request.name, request.birthDate)

			whenever(authorService.updateAuthor(2, request)).thenReturn(updated)

			mockMvc.put("/authors/2") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isOk() }
				jsonPath("$.id", equalTo(2))
				jsonPath("$.name", equalTo("東野圭吾"))
				jsonPath("$.birthDate", equalTo("1958-02-04"))
			}
		}

		@Test
		fun `PUT authors_id - 異常系 - 存在しないID`() {
			val request = AuthorRequest(name = "該当なし", birthDate = LocalDate.of(1900, 1, 1))

			whenever(
				authorService.updateAuthor(
					999,
					request
				)
			).thenThrow(IllegalArgumentException("指定された著者が見つかりません"))

			mockMvc.put("/authors/999") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(request)
			}.andExpect {
				status { isNotFound() }
				jsonPath("$.message", equalTo("指定された著者が見つかりません"))
			}
		}
	}
}
