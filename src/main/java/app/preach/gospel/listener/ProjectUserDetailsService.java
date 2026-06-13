package app.preach.gospel.listener;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dao.AuthorityRepository;
import app.preach.gospel.dao.RoleRepository;
import app.preach.gospel.dao.StudentRepository;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.model.Authority;
import app.preach.gospel.model.AuthorityRef;

/**
 * ログインコントローラ(SpringSecurity関連) - Spring Data JDBC 移行版
 *
 * @author ArkamaHozota
 * @since 2.00
 */
@Component
public class ProjectUserDetailsService implements UserDetailsService {

	private final AuthorityRepository authorityRepository;
	private final RoleRepository roleRepository;
	// jOOQのDSLContextを排除し、Spring Data JDBCのリポジトリを注入
	private final StudentRepository studentRepository;

	public ProjectUserDetailsService(final AuthorityRepository authorityRepository, final RoleRepository roleRepository,
			final StudentRepository studentRepository) {
		this.authorityRepository = authorityRepository;
		this.studentRepository = studentRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		// 1. アカウント名、またはメールアドレスから有効な学生（ユーザー）を取得
		final var student = this.studentRepository.findActiveUserByUsernameOrEmail(username)
				.orElseThrow(() -> new DisabledException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1));
		// 2. 学生の持つ役割ID（roleId）から、紐づく権限IDのセット（集約オブジェクト）を取得
		// 推奨構成により、roleを取得した時点で中間テーブル(ROLE_AUTH)の内容が role.authorities() に自動ロードされています
		final var role = this.roleRepository.findByIdAndVisibleFlgTrue(student.roleId())
				.orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
						ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR2));
		final Set<AuthorityRef> authorityRefs = role.authorities();
		if (authorityRefs == null || authorityRefs.isEmpty()) {
			throw new AuthenticationCredentialsNotFoundException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR3);
		}
		// 3. 中間テーブルから得られた「権限IDのリスト」を使って、AUTHORITIESテーブルから一括検索
		final List<Long> authIds = authorityRefs.stream().map(AuthorityRef::authId).toList();
		final List<Authority> authoritiesRecords = this.authorityRepository.findAllById(authIds);
		if (authoritiesRecords.isEmpty()) {
			throw new AuthenticationCredentialsNotFoundException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR3);
		}
		// 4. DTOおよびSpring Security用認可オブジェクトへのマッピング
		final var studentDto = new StudentDto(student.id(), student.loginAccount(), student.username(),
				student.password(), student.email(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd").format(student.dateOfBirth()), student.roleId());
		final List<SimpleGrantedAuthority> authorities = authoritiesRecords.stream()
				.map(item -> new SimpleGrantedAuthority(item.name())).toList();
		return new ProjectSecurityAdmin(studentDto, authorities);
	}

}
