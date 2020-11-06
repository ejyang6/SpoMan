package spo.tis.index;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spo.tis.domain.TeamVO;
import spo.tis.domain.UserVO;
import spo.tis.service.TeamService;

@Controller
public class TeamController {

	@Autowired
	private TeamService teamSvc;

	@RequestMapping(value = "/teamBoard")
	public String teamBoard(Model model, @RequestParam(defaultValue = "0") int cpage) {

		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}

		model.addAttribute("cpage", cpage);

		return "match/teamBoard";
	}

	@RequestMapping(value = "/teamList")
	public String matchList(Model model, @RequestParam(defaultValue = "0") int cpage) throws Exception {
		
		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}

		// 1. �� �Խñ� �� ��������
		int totalCount = teamSvc.getTotalCount();
		// System.out.println(totalCount);

		int pageSize = 5; // �� �������� ������ �Խñ� ��

		int pageCount = (totalCount - 1) / pageSize + 1;
		if (cpage < 1) {
			cpage = 1; // ù �������� ����
		}
		if (cpage > pageCount) {
			cpage = pageCount; // ������ �������� ����
		}
		// DB���� ������� ���� ����(start, end) �� ���ϱ�
		int end = cpage * pageSize;
		int start = end - (pageSize - 1);

		// 2. �Խø�� ��������
		TeamVO tvo = new TeamVO();// start, end���� ���� ��ü ����
		tvo.setStart(start);
		tvo.setEnd(end);

		List<TeamVO> tlist = teamSvc.listTeam2(tvo);// ����
		int pagingBlock = 5; // �������� 5�� ������ �� ó��
		int prevBlock = 1, nextBlock = 1;

		prevBlock = (cpage - 1) / pagingBlock * pagingBlock;
		nextBlock = prevBlock + (pagingBlock + 1);

		model.addAttribute("cpage", cpage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("pagingBlock", pagingBlock);
		model.addAttribute("prevBlock", prevBlock);
		model.addAttribute("nextBlock", nextBlock);

		// ��ȯ���ִ� List<CmatchaddVO>�� req�� ����
		model.addAttribute("tlist", tlist);

		return "match/teamList";
	}

	@RequestMapping(value = "/teamAdd")
	public String teamAdd() {
		return "match/teamAdd";
	}

	@RequestMapping(value = "/teamAddEnd")
	public String teamAddEnd(Model model, @ModelAttribute("team") TeamVO team, HttpSession ses) {
		System.out.println("user==" + team);
		UserVO vo = (UserVO) ses.getAttribute("loginUser");
		String tking = vo.getName();
		System.out.println(tking);
		team.setTking(tking);
		team.setIdx(vo.getIdx());

		int n = teamSvc.insertTeam(team);
		String str = (n > 0) ? "����� ����" : "����� ����";
		String loc = (n > 0) ? "index" : "signup";
		model.addAttribute("message", str);
		model.addAttribute("loc", loc);

		return "msg";
	}

}
