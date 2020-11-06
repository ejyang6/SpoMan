package spo.tis.index;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j;
import spo.tis.common.util.CommonUtil;
import spo.tis.domain.ClubVO;
import spo.tis.domain.MemberVO;
import spo.tis.domain.UserVO;
import spo.tis.service.ClubService;
import spo.tis.service.MemberService;

@Controller
@RequestMapping("/club")
@Log4j
public class ClubController {
	
	@Autowired
	private ClubService ClubSvc;
	
	@Autowired
	private MemberService MemberSvc;
	
	@Inject
	private CommonUtil util;
	
	/*���� ����Ʈ*/
	@RequestMapping("/list")
	public String clubList(Model model, @RequestParam(defaultValue="0") int cpage) {
		
		// ���� ������
		//String cpStr = req.getParameter("cpage");
		if (cpage==0) {
			cpage = 1;
		}
		//int cpage = Integer.parseInt(cpStr);

		// 1. �� �Խñ� �� ��������
		int totalCount = ClubSvc.getTotalCount();
		System.out.println(totalCount);

		int pageSize = 4; // �� �������� ������ �Խñ� ��

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
		ClubVO cvo = new ClubVO();// start, end���� ���� ��ü ����
		cvo.setStart(start);
		cvo.setEnd(end);

		List<ClubVO> clubList = ClubSvc.listClub2(cvo);
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

		// ��ȯ���ִ� List<ClubdVO>�� req�� ����
		model.addAttribute("clubList", clubList);

		return "club/clubList";
	}
	
	
	
	
	/*���� �����*/
	@RequestMapping("/add")
	public String clubAdd() {
		
		return "club/clubAdd";
	}
	
	


	/*���� �����(end)*/
	@RequestMapping("/addEnd")
	public String clubAddEnd(Model model, 
			HttpServletRequest req,
			@ModelAttribute("clubVo") ClubVO clubVo,
			@RequestParam("mcimage") MultipartFile mcimage) {
		
		log.info("clubVo="+clubVo);
		log.info("mcimage="+mcimage);
		
		String cname=clubVo.getCname();
		String csports=clubVo.getCsports();
		String cplace=clubVo.getCplace();
		String chp1=clubVo.getChp1();
		String chp2=clubVo.getChp2();
		String chp3=clubVo.getChp3();
		String cinfo=clubVo.getCinfo();
		
		int chp1_int = Integer.parseInt(chp1);
		int chp2_int = Integer.parseInt(chp2);
		int chp3_int = Integer.parseInt(chp3);
		
		//��ȿ�� üũ
		if (cname == null || cname.trim().isEmpty() || chp1 == null || chp1.trim().isEmpty()
			|| chp2 == null || chp2.trim().isEmpty() || chp3 == null || chp3.trim().isEmpty()
			|| cinfo == null || cinfo.trim().isEmpty() || mcimage == null) {
			
			return util.addMsgBack(model, "���� �Է����� �ʾҽ��ϴ�. �ٽ� ������ ����� �ּ���.");
			
		}
			
		//���ε��� ���丮�� ������ ������
		ServletContext ctx = req.getServletContext();
		String upDir = ctx.getRealPath("/clubimage");
		log.info("upDir===" + upDir);
		
		File dir=new File(upDir);
		if(! dir.exists()) {
			dir.mkdirs();//���丮 ����
		}
		//������ ÷���ߴٸ�
		if(!mcimage.isEmpty()) {
			
			//÷�����ϸ�, ���� ũ�⸦ �˾Ƴ���.
			String fname=mcimage.getOriginalFilename();//�������ϸ�
			
			//÷�������� �̹� �����ϴ� ������ ��� ����⸦ �����ϱ� ����
			UUID uuid=UUID.randomUUID();//������ ���ڿ��� �߻���Ű�� ���� UUID��ü�� ������.
			String str=uuid.toString();
			String fname2=str+"_"+fname;//���������ϸ�
			
			clubVo.setCimage(fname2); ;//���������ϸ�

			//���� ���ε� ó��==> transferTo()�� �̿��ؼ� ���ε� ó��			
			try {
				///////////////////////////////////
				mcimage.transferTo(new File(upDir,fname2));
				////////////////////////////////
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//���� ���̺� insert
		//ClubVO cvo=new ClubVO(cname, csports, cplace, cinfo, chp1, chp2, chp3, cimage);
		int n=ClubSvc.insertClub(clubVo);
		
		//�� ���� ��������
		HttpSession ses=req.getSession();
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");
		
		//mc���̺� ������ �߰�(grade�� 1)
		if(uvo!=null) {
			
			clubVo.setIdx(uvo.getIdx());
			int n2=ClubSvc.insertMCKing(clubVo);
		}
				
		//���ε� ���� >����ø �̵�, ����>�ڷΰ���
		String msg=(n>0)? "���� ��� ����":"���� ��� ����";
		String loc=(n>0)? "list" : "javascript:history.back()"; 
		
		return util.addMsgLoc(model, msg, loc);
	}
	

	
	/*���� ��������*/
	@RequestMapping("/view")
	public String clubView(Model model, HttpSession ses,
			@RequestParam(defaultValue="0") int cno) {
		
		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}
		//System.out.println("cno="+cno);
		
		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		// ���ӹ�ȣ�� ���ӿ� ���Ե� ����� ���� ��������
		List<MemberVO> mList = MemberSvc.getJoinInfo(cno);

		// �� ���� ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");

		// �������� idx ��������
		int kingIdx = ClubSvc.getClubKingInfo(cno);

		String joincheck = "noJoin";
		if (uvo != null && kingIdx != 0) {
			int idx = uvo.getIdx();
			// System.out.println(idx);

			for (MemberVO mvo : mList) {
				// System.out.println(mvo.getIdx());

				if (idx == mvo.getIdx()) {
					joincheck = "join";

					if (idx == kingIdx) {
						joincheck = "king";
					}
				}
			}
		}

		// req�� ����
		model.addAttribute("clubView", clubView);
		model.addAttribute("mList", mList);
		model.addAttribute("joincheck", joincheck);

		return "club/clubView";
	}
	
	
	
	
	
	
	/*���� �����ϱ�*/
	@RequestMapping("/join")
	public ModelAndView clubJoin(ModelAndView mv, HttpSession ses,
			@RequestParam(defaultValue = "0") int cno) {
		
		//��ȿ�� üũ
		if (cno == 0) {
			mv.setViewName("redirect:list");
			return mv;
		}

		// �� ���� ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");

		// n�� �������� �������� üũ����=> �ʿ�??
		if (uvo != null) {
			ClubVO cvo = new ClubVO();
			cvo.setCno(cno);
			cvo.setIdx(uvo.getIdx());

			// ���ӿ� �����ϱ�
			int n1 = ClubSvc.insertMC(cvo);

			// ���� ��� �� ������Ű��
			int n2 = ClubSvc.incCNumber(cno);
		
		}
		
		mv.setViewName("redirect:view?cno="+cno);
		return mv;
	}
	
	
	/*���� Ż���ϱ� */
	@RequestMapping("/joinDel")
	public ModelAndView clubJoinDel(ModelAndView mv, HttpSession ses,
			@RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ
		if (cno == 0) {
			mv.setViewName("redirect:list");
			return mv;
		}
		
		//�� ���� ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");
		
		
		//n�� �������� �������� üũ����=> �ʿ�??
		if(uvo!=null) {		
			//���ӿ��� Ż���ϱ�(mcstate�� 0���� update)
			int n1=ClubSvc.updateMC(uvo.getIdx());
						
			//Ż���� ���
			int n2=ClubSvc.updateOutdate(uvo.getIdx());
			
			//���� ��� �� ���ҽ�Ű��
			int n3=ClubSvc.decCNumber(cno);
		}
		
		mv.setViewName("redirect:view?cno="+cno);
		return mv;		
	}
	
	
	/*�������� ����ȸ�� Ż���Ű�� */
	@RequestMapping("/drop")
	public ModelAndView clubDrop(Model model, ModelAndView mv, @RequestParam(defaultValue = "0") int idx,
			@RequestParam(defaultValue = "0") int cno) {
		
		// ��ȿ�� üũ => list.do�� redirect�̵�
		if (cno == 0) {
			mv.setViewName(util.addMsgBack(model, "������ �����ϴ�."));
			return mv;
		}
		if (idx == 0) {
			mv.setViewName(util.addMsgBack(model, "Ż���ų ȸ���� �������ּ���."));
			return mv;
		}
		
		//���ӿ��� Ż���ϱ�(mcstate�� 0���� update)
		int n1=ClubSvc.updateMC(idx);
					
		//Ż���� ���
		int n2=ClubSvc.updateOutdate(idx);
		
		//���� ��� �� ���ҽ�Ű��
		int n3=ClubSvc.decCNumber(cno);
		
		mv.setViewName("redirect:view?cno="+cno);
		return mv;	
	}
	
	
	
	/* �˻��ϱ� */
	@RequestMapping("/find")
	public String clubFind(Model model, HttpSession ses,
			@ModelAttribute("clubVo") ClubVO clubVo,
			@RequestParam(defaultValue="0") int cpage) {

		//�˻� ���� ���׵� �ޱ�
		String findType=clubVo.getFindType();
		String findKeyword=clubVo.getFindKeyword();
		String sportsType=clubVo.getSportsType();
		
		//�� Ȯ��
		//System.out.println("findType :"+findType);
		//System.out.println("findKeyword :"+findKeyword);
		//System.out.println("sportsType :"+sportsType);
		
		
		//��ȿ�� üũ
		if(findType==null || findType.equals("0")) {
			findType=clubVo.getFindTypeh();
			findKeyword=clubVo.getFindKeywordh();
			sportsType=clubVo.getSportsTypeh();
			
			if(findType==null || findType.equals("0")||
					(findType.equals("1") && (findKeyword.trim().isEmpty() || findKeyword==null)) 	) {
				return util.addMsgBack(model, "�˻������� �˻�� �Է��ϼ���.");
			}
		}
		
		if(findType.equals("1") && (findKeyword.trim().isEmpty() || findKeyword==null) ) {
			findType=clubVo.getFindTypeh();
			findKeyword=clubVo.getFindKeywordh();
			sportsType=clubVo.getSportsTypeh(); 
			
			if(findType==null || findType.equals("0")||
					(findType.equals("1") && (findKeyword.trim().isEmpty() || findKeyword==null)) 	) {
				return util.addMsgBack(model, "�˻������� �˻�� �Է��ϼ���.");
			}
		}
		
		//���� �� Ȯ��
		//System.out.println("findType :"+findType);
		//System.out.println("findKeyword :"+findKeyword);
		//System.out.println("sportsType :"+sportsType);
		
		//���� ������
		if (cpage==0) {
			cpage = 1;
		}
		
		//1. �� �Խñ� �� �������� 
		ClubVO cvo = new ClubVO();//start, end���� ���� ��ü ����
		cvo.setFindKeyword(findKeyword);
		cvo.setSportsType(sportsType);
		
		int totalCount=0; //�� �Խñ� �� 
		if(findType.equals("1")) { //�����̸�
			totalCount = ClubSvc.getFindNameCount(cvo);  //�˻��� �Խñ� ��
		}else if(findType.equals("2")){ //����
			totalCount = ClubSvc.getFindSportsCount(cvo); 
		}
		//System.out.println(totalCount);
		
		int pageSize = 4; //�� �������� ������ �Խñ� �� 
		
		int pageCount=(totalCount-1)/pageSize + 1;
		if(cpage<1) {
			cpage=1; // ù �������� ���� 
		}
		if(cpage>pageCount) {
			cpage=pageCount; //������ �������� ����
		}
		//DB���� ������� ���� ����(start, end) �� ���ϱ� 
		int end=cpage * pageSize;
		int start = end - (pageSize-1);
		
		//2. �Խø�� ��������	 	
		cvo.setStart(start);
		cvo.setEnd(end);
		
		List<ClubVO> clubList=null; //�Խø��
		if(findType.equals("1")) { //�����̸�
			clubList = ClubSvc.findClubByName(cvo);   
		}else if(findType.equals("2")){ //����
			clubList = ClubSvc.findClubBySports(cvo); 
		}
		
		int pagingBlock=5; //�������� 5�� ������ �� ó��
		int prevBlock=1, nextBlock=1;
		
		prevBlock=(cpage-1)/pagingBlock * pagingBlock;
		nextBlock=prevBlock + (pagingBlock+1);
		
		
		model.addAttribute("cpage", cpage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("pagingBlock", pagingBlock);
		model.addAttribute("prevBlock", prevBlock);
		model.addAttribute("nextBlock", nextBlock);		
		
		//�˻�
		model.addAttribute("findType", findType);
		model.addAttribute("findKeyword", findKeyword);
		model.addAttribute("sportsTypeh", sportsType);
		
		//��ȯ���ִ� List<ClubdVO>�� req�� ����
		model.addAttribute("clubList", clubList);
		
		return "club/clubFind";
	}
}
