package spo.tis.index;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import spo.tis.domain.NotUserException;
import spo.tis.domain.UserVO;
import spo.tis.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userSvc;
	
	@RequestMapping(value="/signin")
	public String signin(Model mv,@CookieValue(value="userid",required=false) Cookie idCookie,HttpServletRequest req){
		
		return "member/signin";
	}
	
	@PostMapping("/login")
	//jsp���Ͽ��� post�� ���±⶧����
	public String loginEnd(HttpSession ses, @ModelAttribute("user") UserVO user,
			HttpServletResponse res,
			HttpServletRequest req,@CookieValue(value="userid",required=false) Cookie idCookie) throws NotUserException {
		//log.info("user=="+user);
		
		//System.out.println("user==="+user);
		UserVO loginUser=this.userSvc.loginCheck(user);
		if(loginUser!=null) {
			//���ǿ� �α��� ������ ���� ȸ�� ������ ��������.
			ses.setAttribute("loginUser", loginUser);
		}
		boolean saveId = user.isSaveId();
		System.out.println(saveId);
		if(saveId) {
			Cookie cookie = new Cookie("userid", loginUser.getId());
			cookie.setMaxAge(3600); // ��Ű ��ȿ�Ⱓ ���� (�� ����)
			res.addCookie(cookie);
			System.out.println(cookie);
		}else {
			Cookie cookie = new Cookie("userid", null);
			cookie.setMaxAge(0); // ��Ű ��ȿ�Ⱓ ���� (�� ����)
			res.addCookie(cookie);
		}
		
		return "redirect:index";
	}//------------------------------------------------
	
	
		
	@GetMapping("/logout")
	public String logout(HttpSession ses) {
		ses.invalidate();//���� ���� ��� ��ȿȭ �����ִ� ��.
		return "redirect:index";
	}
	
}
	

