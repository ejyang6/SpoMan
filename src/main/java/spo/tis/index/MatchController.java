package spo.tis.index;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spo.tis.domain.MtVO;
import spo.tis.domain.TeamVO;
import spo.tis.domain.UserVO;
import spo.tis.domain.matchaddVO;
import spo.tis.service.MatchService;
import spo.tis.service.TeamService;

@Controller
public class MatchController {

	@Autowired
	private MatchService matchSvc;

	@Inject
	private TeamService teamSvc;

	@RequestMapping(value = "/matchBoard")
	public String matchBoard(Model model,
			 @RequestParam(defaultValue = "0") int cpage) {

		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}

		model.addAttribute("cpage", cpage);
		
		return "match/matchBoard";
	}

	@RequestMapping(value = "/matchList")
	public String matchList(Model m, HttpServletRequest req) throws Exception {


		// ���� ������
		String cpStr = req.getParameter("cpage");
		if (cpStr == null || cpStr.trim().isEmpty()) {
			cpStr = "1";
		}
		int cpage = Integer.parseInt(cpStr);
		System.out.println("cpage="+cpage);
		
		// 1. �� �Խñ� �� ��������
		int totalCount = matchSvc.getTotalCount();
		//System.out.println(totalCount);

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
		matchaddVO mvo = new matchaddVO();// start, end���� ���� ��ü ����
		mvo.setStart(start);
		mvo.setEnd(end);

		List<matchaddVO> mlist = matchSvc.getAllMatch(mvo); //����
		int pagingBlock = 5; // �������� 5�� ������ �� ó��
		int prevBlock = 1, nextBlock = 1;

		prevBlock = (cpage - 1) / pagingBlock * pagingBlock;
		nextBlock = prevBlock + (pagingBlock + 1);

		req.setAttribute("cpage", cpage);
		req.setAttribute("totalCount", totalCount);
		req.setAttribute("pageCount", pageCount);
		req.setAttribute("pagingBlock", pagingBlock);
		req.setAttribute("prevBlock", prevBlock);
		req.setAttribute("nextBlock", nextBlock);

		// ��ȯ���ִ� List<CmatchaddVO>�� req�� ����
		m.addAttribute("mlist", mlist);

		return "match/list";
	}

	@RequestMapping(value = "/matchAdd")
	public String matchAdd(Model mv, HttpServletRequest req, HttpSession ses) {
		UserVO mtuser = (UserVO) ses.getAttribute("loginUser");
		String user = mtuser.getName();
		System.out.println(user);
		TeamVO vo = teamSvc.Teaminfo(user);

		req.setAttribute("tvo", vo);

		return "match/matchAdd";
	}

	@RequestMapping(value = "/matchAddEnd")
	public String matchAddEnd(Model model, @ModelAttribute("match") matchaddVO match,
			@ModelAttribute("Team") TeamVO Team, HttpSession ses, HttpServletRequest req) {

		UserVO uvo = (UserVO) ses.getAttribute("loginUser");
		TeamVO tvo = teamSvc.selectByKname(uvo.getName());
		System.out.println(tvo);
		String mno = req.getParameter("mno");
		int tno = tvo.getTno();
		int idx = uvo.getIdx(); //�߰�
		String game = req.getParameter("game");
		String mname = req.getParameter("mname");
		String away = req.getParameter("away");
		String mcontent = req.getParameter("mcontent");
		String addr = req.getParameter("addr");
		String ph1 = req.getParameter("ph1");
		String ph2 = req.getParameter("ph2");
		String ph3 = req.getParameter("ph3");
		String map = req.getParameter("map");
		String tlevel = req.getParameter("tlevel");

		matchaddVO vo = new matchaddVO(0, tno, idx, game, mname, away, mcontent, null, addr, ph1, ph2, ph3, map, tlevel,
				null);

		int n = matchSvc.insertMatching(vo);

		String msg = (n > 0) ? "��ϼ���" : "��Ͻ���";
		String loc = (n > 0) ? "matchBoard" : "javascript:history.back()";

		req.setAttribute("message", msg);
		req.setAttribute("loc", loc);

		return "msg";
	}

	@RequestMapping(value = "/match")
	public String match(Model m, HttpServletRequest req, @ModelAttribute("matchadd") matchaddVO match)
			throws Exception {
		String mno = match.getMno() + ""; //�۾� ��� ��ġ�۹�ȣ
		
		matchaddVO vo = matchSvc.getMatchinfo(mno);
		//System.out.println(vo.getGame());
		m.addAttribute("vo", vo);

		return "match/match";
	}
	
	@RequestMapping(value = "/mtstart")
	public String mtStart(Model model, HttpServletRequest req, 
			@ModelAttribute("mtvo") MtVO mtvo) {
		String bidx = mtvo.getBidx();
		int amno = mtvo.getAmno();
		
		matchaddVO vo = matchSvc.getMatchinfo(bidx); //�۹�ȣ�� �۾� ��� ���� ��������
		int hmno=vo.getIdx();
		
		mtvo.setBidx(bidx); //�۹�ȣ 
		mtvo.setAmno(amno); //��ġ ��û�� ����� ȸ����ȣ
		mtvo.setHmno(hmno); //�� �� ��� ȸ����ȣ
		
		TeamVO tvo1 = teamSvc.Teaminfo2(amno);
		TeamVO tvo2 = teamSvc.Teaminfo2(hmno);
		
		mtvo.setAtno(tvo1.getIdx()); //��ġ ��û�� ����� ����ȣ
		mtvo.setHtno(tvo2.getIdx()); //�۾� ����� ����ȣ
		
		int n = this.matchSvc.applymatch(mtvo);

		String msg = (n > 0) ? "��û����" : "��û����";
		String loc = (n > 0) ? "index" : "javascript:history.back()";

		req.setAttribute("message", msg);
		req.setAttribute("loc", loc);

		return "msg"; 
		
	}
	
	
	@RequestMapping(value = "/matchFind")
	public String matchFind(Model model, 
			@ModelAttribute("mtvo") matchaddVO mtvo,
			@RequestParam(defaultValue = "0") int cpage) {

		//String findType = mtvo.getFindType();
		//String findKeyword = mtvo.getFindKeyword();
		//String selSports = mtvo.getSelSports();
		//System.out.println("findType="+findType);
		//System.out.println("findKeyword="+findKeyword);
		
		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}
		
		//vo����
		//matchaddVO mvo = new matchaddVO();// start, end���� ���� ��ü ����
		//mvo.setFindType(findType);
		//mvo.setFindKeyword(findKeyword);
		//mvo.setSelSports(selSports);
		
		// 1. �� �Խñ� �� ��������
		int totalCount = matchSvc.getFindCount(mtvo);
		//System.out.println("totalCount="+totalCount);

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
		mtvo.setStart(start);
		mtvo.setEnd(end);

		List<matchaddVO> mlist = matchSvc.getFindMatch(mtvo); //����
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
		model.addAttribute("mlist", mlist);
		
		return "match/matchFind";
		
	}
	
	
	@RequestMapping(value = "/matchFindCate")
	public String matchFindCate(Model model, 
			@ModelAttribute("mtvo") matchaddVO mtvo,
			@RequestParam(defaultValue = "0") int cpage) {

		//String selSports = mtvo.getSelSports();
		//System.out.println("selSports="+selSports);
		
		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}
		
		//vo����
		//matchaddVO mvo = new matchaddVO();// start, end���� ���� ��ü ����
		//mvo.setSelSports(selSports);
		
		// 1. �� �Խñ� �� ��������
		int totalCount = matchSvc.getFindCateCount(mtvo);
		//System.out.println("totalCount="+totalCount);

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
		mtvo.setStart(start);
		mtvo.setEnd(end);

		List<matchaddVO> mlist = matchSvc.getFindMatchCate(mtvo); //����
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
		model.addAttribute("mlist", mlist);

		return "match/matchFindCate";
	}
	
}
