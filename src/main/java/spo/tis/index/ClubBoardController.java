package spo.tis.index;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spo.tis.common.util.CommonUtil;
import spo.tis.domain.CBoardVO;
import spo.tis.domain.ClubVO;
import spo.tis.domain.UserVO;
import spo.tis.service.CBoardService;
import spo.tis.service.ClubService;

@Controller
@RequestMapping("/club")
public class ClubBoardController {

	@Inject
	private CommonUtil util;

	@Autowired
	private CBoardService CBoardSvc;

	@Autowired
	private ClubService ClubSvc;

	@RequestMapping("/board")
	public String clubBoard(Model model, @RequestParam(defaultValue = "0") int cpage,
			@RequestParam(defaultValue = "0") int cno) {

		// ���� ������
		if (cpage == 0) {
			cpage = 1;
		}

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// �۹�ȣ, ����, �۾���, ��¥, ��ȸ�� ��������
		// List<CBoardVO> cbList = cbdao.getClubBoardList(cno);

		// 1. �� �Խñ� �� ��������
		int totalCount = CBoardSvc.getCbdTotalCount(cno);
		System.out.println(totalCount);

		int pageSize = 6; // �� �������� ������ �Խñ� ��

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
		CBoardVO cbvo = new CBoardVO();// start, end���� ���� ��ü ����
		cbvo.setStart(start);
		cbvo.setEnd(end);
		cbvo.setCno(cno);

		List<CBoardVO> cbList = CBoardSvc.getCbdList(cbvo);
		int pagingBlock = 5; // �������� 5�� ������ �� ó��
		int prevBlock = 1, nextBlock = 1;

		prevBlock = (cpage - 1) / pagingBlock * pagingBlock;
		nextBlock = prevBlock + (pagingBlock + 1);

		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cpage", cpage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("pagingBlock", pagingBlock);
		model.addAttribute("prevBlock", prevBlock);
		model.addAttribute("nextBlock", nextBlock);
		model.addAttribute("clubView", clubView);

		// req�� ����
		model.addAttribute("cno", cno);
		model.addAttribute("cbList", cbList);

		return "/club/clubBoard";
	}

	@RequestMapping("/boardAdd")
	public String clubBoardAdd(Model model, @RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cno", cno);
		model.addAttribute("clubView", clubView);

		return "/club/clubBoardAdd";
	}

	
	
	@RequestMapping("/boardAddEnd")
	public String clubBoardAddEnd(Model model, HttpSession ses,
			@ModelAttribute("cboardVo") CBoardVO cboardVo,
			@RequestParam(defaultValue="0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}
		//������, �۳��� �ޱ�
		String cbtitle=cboardVo.getCbtitle();
		String cbcontent=cboardVo.getCbcontent();
		
		if(cbtitle==null || cbcontent==null || 
				cbtitle.trim().isEmpty() || cbcontent.trim().isEmpty()) {
			
			return util.addMsgBack(model, "������� �۳����� �Է����� �ʾҽ��ϴ�.");
		}
		
		//ȸ����ȣ ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");
		if(uvo!=null) {
			//�Խñ� ����ϱ�
			CBoardVO cbvo=new CBoardVO(); 
			cbvo.setCno(cno);	//���ӹ�ȣ
			cbvo.setIdx(uvo.getIdx());  //ȸ����ȣ
			cbvo.setCbtitle(cbtitle);
			cbvo.setCbcontent(cbcontent);
	
			int n=CBoardSvc.insertBoard(cbvo);
			//System.out.println("n=="+n);
			
			String msg=(n>0)? "��� ����":"��� ����";
			String loc=(n>0)?  "board?cno="+cno:"javascript:history.back()";
			
			return util.addMsgLoc(model, msg, loc);
		}
		
		return "club/clubBoard";
	}

		
		
	@RequestMapping("/boardMy")
	public String clubBoardMy(Model model, HttpSession ses, @RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// ȸ����ȣ ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");

		if (uvo != null) {
			// ���� �Խñ� ��� ��������
			CBoardVO cbvo = new CBoardVO();
			cbvo.setCno(cno); // ���ӹ�ȣ
			cbvo.setIdx(uvo.getIdx()); // ȸ����ȣ

			List<CBoardVO> cbList = CBoardSvc.getMyBoard(cbvo);

			model.addAttribute("cbList", cbList);
		}

		// ȸ����ȣ�� �������� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cno", cno);
		model.addAttribute("clubView", clubView);

		return "/club/clubBoardMy";
	}

	@RequestMapping("/boardDel")
	public String clubBoardDel(Model model, @RequestParam(defaultValue = "0") int cno,
			@RequestParam(defaultValue = "0") int cbno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		if (cbno == 0) {
			return util.addMsgBack(model, "������ ���� �����ϴ�.");
		}

		int n = CBoardSvc.deleteBoard(cbno);

		String msg = (n > 0) ? "���� ����" : "���� ����";
		String loc = "boardMy.do?cno=" + cno;
		
		return util.addMsgLoc(model, msg, loc);
	}

	@RequestMapping("/boardEdit")
	public String clubBoardEdit(Model model, @RequestParam(defaultValue = "0") int cno,
			@RequestParam(defaultValue = "0") int cbno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		if (cbno == 0) {
			return util.addMsgBack(model, "������ ���� �����ϴ�.");
		}

		CBoardVO cbvo = CBoardSvc.getBoardDetail(cbno);

		// ȸ����ȣ�� �������� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cno", cno);
		model.addAttribute("cbno", cbno);
		model.addAttribute("clubView", clubView);
		model.addAttribute("cbvo", cbvo);

		return "/club/clubBoardEdit";
	}
	
	
	
	
	@RequestMapping("/boardEditEnd")
	public String clubBoardEditEnd(Model model, 
			@RequestParam(defaultValue = "0") int cno,
			@RequestParam(defaultValue = "0") int cbno,
			@ModelAttribute("cboardVo") CBoardVO cboardVo) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		if (cbno == 0) {
			return util.addMsgBack(model, "������ ���� �����ϴ�.");
		}
		
		//������, �۳��� �ޱ�
		String cbtitle=cboardVo.getCbtitle();
		String cbcontent=cboardVo.getCbcontent();
		
		if(cbtitle==null || cbcontent==null || 
				cbtitle.trim().isEmpty() || cbcontent.trim().isEmpty()) {
			
			return util.addMsgBack(model, "������� �۳����� �Է����� �ʾҽ��ϴ�.");
		}
		
		
		//�Խñ� ����ϱ�
		CBoardVO cbvo=new CBoardVO(); 
		cbvo.setCbno(cbno);
		cbvo.setCbtitle(cbtitle);
		cbvo.setCbcontent(cbcontent);

		int n=CBoardSvc.updateBoard(cbvo);
		
		String msg=(n>0)? "���� ����":"���� ����";
		String loc="boardMy.do?cno="+cno;
		
		return util.addMsgLoc(model, msg, loc);
		
	}
		
		

	@RequestMapping("/boardView")
	public String clubBoardView(Model model, @RequestParam(defaultValue = "0") int cno,
			@RequestParam(defaultValue = "0") int cbno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		if (cbno == 0) {
			return util.addMsgBack(model, "�ش��ϴ� �Խñ��� �����ϴ�.");
		}

		// ��ȸ�� ����
		int n = CBoardSvc.udpateCbReadnum(cbno);

		CBoardVO cboard = CBoardSvc.getBoardDetail(cbno);

		// req�� ����
		model.addAttribute("cboard", cboard);
		model.addAttribute("cno", cno);

		// ������ �̵�
		return "/club/clubBoardView";

	}
}
