package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;


@RestController
public class DummyController {
	
	@Autowired // 의존성 주입 (DI)
	private UserRepository userRepository;
	
	//save함수는 id를 전달하지 않으면 insert를  해주고
	// save함수는 id를 전달하면 해당 id에 대한 데이터가 있으면 update를 해주고
	// save함수는 id를 전달하면 해당 id에 대한 데이터가 있으면 insert 해줘요
	// email. password
	
	@DeleteMapping("/dummy/user/{id}")
	public String deleteUser(@PathVariable int id) {
		try {
			userRepository.deleteById(id);
			
		} catch (EmptyResultDataAccessException e) {
			return "해당 id가 없습니다. : " + id;
		} 	
		
		return "삭제 되었습니다" + id;
	}
	
	@Transactional // 함수 종료시에 자동 commit이 됨.
	@PutMapping("/dummy/user/{id}")
	public User updateUser(@PathVariable int id, @RequestBody User requestUser) { // json 데이터를 요청 > Java Object(MessageConverter의 Jackson 라이브러리가 받아줌
		System.out.println("id : " + id);
		System.out.println("password : " + requestUser.getPassword());
		System.out.println("email" + requestUser.getEmail());
		
		// 영속화가 됨
		User user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("수정에 실패하였습니다.");
		});
		
		// 데이터가 변화가 없으면 commit 안함 
		user.setPassword(requestUser.getPassword());
		user.setEmail(requestUser.getEmail());
		
		//userRepository.save(user);
		return user;
	}
	
	@GetMapping("/dummy/users")
	public List<User> list(){
		return userRepository.findAll();
	}

	
	// 한페이지당 2건에 데이터를 리턴받아 볼 예정
	@GetMapping("/dummy/user/page")
	public List<User> pageList(@PageableDefault(size=2, sort="id", direction = Sort.Direction.DESC) Pageable pageable){
		Page<User> pagingUser = userRepository.findAll(pageable);

		List<User> users = pagingUser.getContent();
		return users;
	}
	
	// {id} 주소로 파라미터를 전달 받을 수 있음.
	// http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id)	{
		// user/4을 찾으면 내가 데이터베이스에서 못찾아오게 되면 user가 null이 될것아냐?
		// 그럼 return할때 null이 리턴이 되자나... 그럼 프로그램에 문제가 있지 않겟니?
		// Optional로 너의 User객체를 감싸서 가져올테니 null인지 아닌지 판단해서 return
		
		
//		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {
//			@Override
//			public IllegalArgumentException get() {
//				// TODO Auto-generated method stub
//				return new IllegalArgumentException("해당 유저는 없습니다. id : "+id);
//			}
//		});
		
		User user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("해당 유저는 없습니다. id : "+id);
		});
				
		// get으로하는 잘못된 방법
//		User user = userRepository.findById(id).orElseGet(new Supplier<User>() {
//			@Override
//			public User get() {
//				// TODO Auto-generated method stub
//				return new User();
//			}
//		});

		// 요청 : 웹 브라우저
		// user = 자바 오브젝트
		// 변환 ( 웹브라우저가 이해할 수 있는 데이터) -> JSON (Gson 라이브러리)
		// 스프링 부트 = MessageConverter라는 애가 응답시에 자동 작동
		// 만약에 자바 오브젝트를 리턴하게 되면 MessageConverter라는 친구가 자동적으로 JSON으로 변환하여
		// 웹 브라우저에 뿌려주게 됨ㅅ
		return user;
	}
	
	// http://localhost:8000/blog/dummy/join(요청)
	// http body에 username,password,email 데이터를 가지고 (요청)
	@PostMapping("/dummy/join")
	public String join(User user) {
		System.out.println("id: " +user.getId());
		System.out.println("userdname: " +user.getUsername());
		System.out.println("password: " + user.getPassword());
		System.out.println("email: " + user.getEmail());
		System.out.println("role: " +user.getRole());
		System.out.println("CreateData: " +user.getCreateDate());

		user.setRole(RoleType.USER);
		userRepository.save(user);
		return "회원가입 완료";
	}
}
