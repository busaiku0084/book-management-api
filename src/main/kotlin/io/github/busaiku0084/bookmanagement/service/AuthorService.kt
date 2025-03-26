package io.github.busaiku0084.bookmanagement.service

import io.github.busaiku0084.bookmanagement.dto.AuthorRequest
import io.github.busaiku0084.bookmanagement.dto.AuthorResponse
import io.github.busaiku0084.bookmanagement.model.Author
import io.github.busaiku0084.bookmanagement.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者情報を管理するサービスクラス
 *
 * @property authorRepository 著者のデータ操作を行うリポジトリ
 */
@Service
class AuthorService(private val authorRepository: AuthorRepository) {

	/**
	 * 著者を登録する
	 *
	 * @param request 著者の情報（DTO）
	 * @return 登録された著者のレスポンスデータ
	 */
	@Transactional
	fun createAuthor(request: AuthorRequest): AuthorResponse {
		val author = Author(
			name = request.name,
			birthDate = request.birthDate
		)
		val created = authorRepository.create(author)
		return AuthorResponse(
			id = created.id,
			name = created.name,
			birthDate = created.birthDate
		)
	}

	/**
	 * すべての著者を取得する
	 *
	 * @return 著者レスポンスDTOのリスト
	 */
	fun getAllAuthors(): List<AuthorResponse> {
		return authorRepository.findAll().map {
			AuthorResponse(
				id = it.id,
				name = it.name,
				birthDate = it.birthDate
			)
		}
	}

	/**
	 * 指定されたIDの著者情報を取得する
	 *
	 * @param id 著者ID
	 * @return 著者のレスポンスデータ
	 * @throws IllegalArgumentException 該当する著者が存在しない場合
	 */
	@Transactional(readOnly = true)
	fun getAuthorById(id: Int): AuthorResponse {
		val author = authorRepository.findById(id)
			?: throw IllegalArgumentException("指定された著者が見つかりません")

		return AuthorResponse(
			id = author.id,
			name = author.name,
			birthDate = author.birthDate
		)
	}

	/**
	 * 著者情報を更新する
	 *
	 * @param id 更新対象の著者ID
	 * @param request 更新内容（DTO）
	 * @return 更新された著者のレスポンスデータ
	 */
	@Transactional
	fun updateAuthor(id: Int, request: AuthorRequest): AuthorResponse {
		val existingAuthor = authorRepository.findById(id)
			?: throw IllegalArgumentException("指定された著者が見つかりません")

		val updatedAuthor = existingAuthor.copy(
			name = request.name,
			birthDate = request.birthDate
		)
		authorRepository.update(updatedAuthor)

		return AuthorResponse(
			id = updatedAuthor.id,
			name = updatedAuthor.name,
			birthDate = updatedAuthor.birthDate
		)
	}
}
