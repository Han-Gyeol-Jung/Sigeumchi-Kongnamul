package com.nigagara.hawaii.controller;
import com.nigagara.hawaii.controller.DTO.LoginFormDTO;
import com.nigagara.hawaii.entity.CommentData;
import com.nigagara.hawaii.entity.TestComment;
import com.nigagara.hawaii.entity.TestEntity;
import com.nigagara.hawaii.entity.User;
import com.nigagara.hawaii.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.persistence.EntityManager;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    // 구현체 JavaMailSenderImpl이 자동 주입됨
    private final JavaMailSender mailSender;
    private final EntityManager em;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @GetMapping("/mail")
    public String sendMail()  {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("chj6703@gmail.com");
        message.setSubject("보내져라");
        message.setText("보내져라요");
        mailSender.send(message);

        return "redirect:/";
    }


    @ResponseBody
    @PostMapping("/ajax")
    public Hello jax(@RequestBody Dello dello){
        //log.info("post 요청받음,{} {} {}", hello.getName(), hello.getAddress(), hello.getAge());
        log.info("*********** jax() 실행 ****  {}", dello.getUserName());
        return new Hello("이름", "두살", "서울시");
    }
    static class Dello{
        private String userName;
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }
    static class Hello{


        private String name;
        private String age;
        private String address;

        public Hello(String name, String age, String address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

    @GetMapping("/test")
    public String goTestPage(){
        return "copy";
    }

    /**
     *spring security는 로그아웃 시 /login?logout으로 get 요청
     *       *  logout() 파라미터 설정을 안해서 기본 경로인 /logout 로그아웃 경로,
     *      *로그아웃 후 경로가 /login?logout으로 잡힌 듯 -> 경로를 /user/logout으로 설정하니
     *      *spring security가 안 먹힘.
      *
     */
    @GetMapping({"/", "/login"})
    public String home(Model model, RedirectAttributes redirectAttributes){

        // 빈 폼 전달하지 않으면 오류 발생
        model.addAttribute("loginFormDTO", new LoginFormDTO());
        return "home";
    }

    @GetMapping("/users")
    public String showUserList(Model model){

        List<User> users = userService.findUsers();
        model.addAttribute("users",users);
        return "/user/userList"; // userList.html
    }

    @PostMapping("/generateData")
    @Transactional
    public String generateUser(){
        generateUserTest();
       generateCommentAndTest(); // 기본 테스트 1, 2와  기본 댓글 생성
        return "redirect:/";
    }
















    public void generateCommentAndTest() {
        // Test는 생성 메서드가 없음. 직접 @Transactional persist
        TestEntity testEntity = new TestEntity();
        testEntity.setTestName("1번 게시물"); testEntity.setCount(9);
        em.persist(testEntity);
        Long testId = testEntity.getId();

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setTestName("2번 게시물"); testEntity2.setCount(7);
        em.persist(testEntity2);
        Long testId2 = testEntity2.getId();

        TestEntity testEntity3 = new TestEntity();
        testEntity3.setTestName("3번 게시물"); testEntity3.setCount(5);
        em.persist(testEntity3);
        Long testId3 = testEntity3.getId();

        TestEntity testEntity4 = new TestEntity();
        testEntity4.setTestName("4번 게시물"); testEntity4.setCount(11);
        em.persist(testEntity4);
        Long testId4 = testEntity4.getId();

        TestComment comment = new TestComment();
        CommentData commentData = new CommentData("다람쥐1", "월요일 좋아1", 4L);
        TestComment comment2 = new TestComment();
        CommentData commentData2 = new CommentData("다람이2", "월요일 안조아2", 44L);
        TestComment comment3 = new TestComment();
        CommentData commentData3 = new CommentData("다람이3", "금요일 안조아3", 444L);
        TestComment comment4 = new TestComment();
        CommentData commentData4 = new CommentData("다람이4", "금요일 조아4", 4444L);
        TestComment comment5 = new TestComment();
        CommentData commentData5 = new CommentData("다람이5", "5번째 댓글", 44444L);

        comment.setCommentData(commentData);
        comment.setTestEntity(em.find(TestEntity.class, testId));
        comment2.setCommentData(commentData2);
        comment2.setTestEntity(em.find(TestEntity.class, testId2));
        comment3.setCommentData(commentData3);
        comment3.setTestEntity(em.find(TestEntity.class, testId2));
        comment4.setCommentData(commentData4);
        comment4.setTestEntity(em.find(TestEntity.class, testId));
        comment5.setCommentData(commentData5);
        comment5.setTestEntity(em.find(TestEntity.class, testId));
        // service 이용하지 않고 바로 넣기
        em.persist(comment);
        em.persist(comment2);
        em.persist(comment3);
        em.persist(comment4);
        em.persist(comment5);
    }

    private void generateUserTest() {

        User user1 = new User();
        user1.setUserName("3");
        user1.setEmail("chj6703@naver.com");

        String encodedPwd = encoder.encode("3"); // salting 처리까지 자동
        user1.setPassword(encodedPwd);

        user1.setPwdQuestion("내 보물 1호는?"); user1.setPwdAnswer("이 웹사이트");
        user1.setPwdHint(user1.getUserName()+"의 힌트");


        User user2 = new User();
        user2.setUserName("jo2");
        user2.setEmail("gdgdg@.com");

        String encodedPwd2 = encoder.encode("22");
        user2.setPassword(encodedPwd2);

        user2.setPwdQuestion("내가 처음 산 차는?"); user2.setPwdAnswer("차가 없음");
        user2.setPwdHint(user2.getUserName()+"의 힌트2");

        User user3 = new User();
        user3.setUserName("jo3");
        user3.setEmail("gxvsdf@.com");
        user3.setPassword("33");

        User user4 = new User();
        user4.setUserName("jo4");
        user4.setEmail("gwefsd@.com");
        user4.setPassword("44");

        userService.joinUser(user1);
        userService.joinUser(user2);
        userService.joinUser(user3);
        userService.joinUser(user4);
    }

    //    @GetMapping("/{id}")
//    public String clickTest(@PathVariable String id){ // String id
//        return "home";
//    }



}

















